package com.kelvinconnect.discord.command;

import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates a embeded link
 *
 * Created by Adam on 15/03/2017.
 */
public class TicketCommand implements CommandExecutor {

    private static final String TICKET_FORMAT = "^(\\d|#)\\d*$";

    @Command(aliases = "!ticket", description = "Creates a trac link to the mentioned ticket.", usage = "!ticket <ticket-number>")
    public String onTicketCommand(String[] args, Message message) {
        if (args.length != 1) { // more than 1 argument
            return "Incorrect number of arguments!";
        }
        String ticketNumber = args[0].trim();

        if (!isValidTicketNumber(ticketNumber)) {
            return ticketNumber + " is not a valid ticket number.";
        }

        String url = getUrl(ticketNumber);

        Document doc;
        try {
            doc = getDocument(url);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error connecting to " + url;
        }

        sendEmbedMessage(url, message, doc);
        return null;
    }

    private boolean isValidTicketNumber(String ticketNumber) {
        Pattern p = Pattern.compile(TICKET_FORMAT);
        Matcher m = p.matcher(ticketNumber);
        return m.find();
    }

    private String getUrl(String ticketNumber) {
        if (ticketNumber.startsWith("#")) {
            ticketNumber = ticketNumber.substring(1);
        }

        return "http://trac/KC/ticket/" + ticketNumber;
    }

    private void sendEmbedMessage(String url, Message message, Document doc) {
        EmbedBuilder embed = new EmbedBuilder();

        String tracStatus = getTracPropertyByClass(doc, "trac-status");
        setColourByStatus(embed, tracStatus);

        String description = buildDescription(doc, tracStatus);

        embed.setDescription(description);
        embed.setTitle(getTracPropertyByClass(doc, "summary"));

        if (isAssigned(tracStatus)) {
            embed.setAuthor(getTracPropertyByHeaders(doc, "h_owner"));
        }

        if (tracStatus.equals("trac-resolution")) {
            embed.setFooter(getTracPropertyByClass(doc, "summary"));
        }

        embed.setUrl(url);

        message.getChannel().sendMessage(url, embed);
    }

    private String buildDescription(Document doc, String tracStatus) {
        StringBuilder builder = new StringBuilder();
        builder.append("Type: ");
        builder.append(getTracPropertyByClass(doc, "trac-type"));
        builder.append("\nStatus: ");
        builder.append(tracStatus);
        builder.append("\nReporter: ");
        builder.append(getTracPropertyByHeaders(doc, "h_reporter"));
        builder.append("\nMilestone: ");
        builder.append(getTracPropertyByHeaders(doc, "h_milestone"));
        builder.append("\nStory Points: ");
        builder.append(getTracPropertyByHeaders(doc, "h_estimate"));
        builder.append("\n\nDescription: ");
        builder.append(getTracDescription(doc));
        return builder.toString();
    }

    private String getTracDescription(Document doc) {
        Element descEle = doc.body().getElementsByClass("description").first();
        Elements pElements = descEle.getElementsByTag("p");
        Iterator<Element> it = pElements.iterator();
        StringBuilder builder = new StringBuilder();
        while (it.hasNext()) {
            Element p = it.next();
            builder.append("\n");
            builder.append(p.text());
        }
        return builder.toString();
    }

    private void setColourByStatus(EmbedBuilder embed, String tracStatus) {
        switch (tracStatus) {
            case "closed":
                embed.setColor(Color.GRAY);
                break;
            case "new":
                embed.setColor(Color.WHITE);
                break;
            case "dev_ready":
            case "assigned_dev":
                embed.setColor(Color.BLUE);
                break;
            case "test_ready":
            case "assigned_test":
                embed.setColor(Color.GREEN);
                break;
            case "build_ready":
                embed.setColor(Color.CYAN);
                break;
            case "review":
            case "info_needed":
                embed.setColor(Color.YELLOW);
                break;
            case "failed_test":
                embed.setColor(Color.ORANGE);
                break;
            case "blocked":
            case "core_needed":
                embed.setColor(Color.RED);
                break;
        }
    }

    private boolean isAssigned(String tracStatus) {
        return (tracStatus.equals("assigned_dev") ||
                tracStatus.equals("assigned_test") ||
                tracStatus.equals("info_needed"));
    }

    private Document getDocument(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    private String getTracPropertyByClass(Document doc, String className) {
        return doc.body().getElementsByClass(className).text();
    }

    private String getTracPropertyByHeaders(Document doc, String headers) {
        return doc.body().getElementsByAttributeValue("headers", headers).text();
    }
}
