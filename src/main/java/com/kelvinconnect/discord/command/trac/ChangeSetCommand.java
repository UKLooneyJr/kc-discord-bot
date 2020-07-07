package com.kelvinconnect.discord.command.trac;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates a embeded link
 * <p>
 * Created by Adam on 15/03/2017.
 */
public class ChangeSetCommand implements CommandExecutor {
    private static final Logger logger = LogManager.getLogger(ChangeSetCommand.class);

    private static final String REVISION_NUMBER_FORMAT = "^[1-9]\\d*$";
    private static final int MAX_CHANGES_PER_MESSAGE = 25;

    @Command(aliases = "!changeset", description = "Creates a link to the mentioned changeset.", usage = "!changeset <revision-number>")
    public String onChangeSetCommand(String[] args, Message message) {
        if (args.length != 1) {
            return "Incorrect number of arguments!";
        }
        String revision = args[0].trim();

        if (!isValidRevisionNumber(revision)) {
            return revision + " is not a valid SVN revision.";
        }

        String url = getUrl(revision);

        Document doc;
        try {
            doc = getDocument(url);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error connecting to " + url;
        }

        sendEmbedMessage(revision, url, message, doc);
        return null;
    }

    private boolean isValidRevisionNumber(String revision) {
        Pattern p = Pattern.compile(REVISION_NUMBER_FORMAT);
        Matcher m = p.matcher(revision);
        return m.find();
    }

    private String getUrl(String revision) {
        return "http://trac.pronto.pri/KC/changeset/" + revision;
    }

    private void sendEmbedMessage(String revision, String url, Message message, Document doc) {
        List<EmbedBuilder> embeds = new ArrayList<>();
        embeds.add(sendEmbedForDescription(revision, url, doc));

        Element files = doc.body().getElementsByClass("files").get(1);
        sendEmbedForChangeType(files, "add", Color.GREEN, "Files added:").ifPresent(embeds::add);
        sendEmbedForChangeType(files, "rem", Color.RED, "Files removed:").ifPresent(embeds::add);
        sendEmbedForChangeType(files, "mod", Color.ORANGE, "Files modified:").ifPresent(embeds::add);
        sendEmbedForChangeType(files, "cp", Color.BLUE, "Files copied:").ifPresent(embeds::add);
        sendEmbedForChangeType(files, "mv", Color.GRAY, "Files moved:").ifPresent(embeds::add);

        for (EmbedBuilder e : embeds) {
            message.getChannel().sendMessage(null, e);
        }
    }

    private EmbedBuilder sendEmbedForDescription(String revision, String url, Document doc) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Revision " + revision);
        embed.setUrl(url);

        String description = doc.body().getElementsByClass("message").get(1).text();
        embed.setDescription(description);

        String author = doc.body().getElementsByClass("author").get(1).text();
        embed.setAuthor(author);

        return embed;
    }

    private Optional<EmbedBuilder> sendEmbedForChangeType(Element files, String type, Color colour, String title) {
        Elements changes = files.getElementsByClass(type);
        if (!changes.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            // ignore the first 'change' as that is just the legend
            int filesToList = Math.min(changes.size(), MAX_CHANGES_PER_MESSAGE);
            for (int i = 1; i < filesToList; ++i) {
                Element change = changes.get(i);
                String fileName = change.nextElementSibling().text();
                sb.append(fileName).append("\n");
            }
            if (filesToList == MAX_CHANGES_PER_MESSAGE) {
                sb.append("... and more (too many to list)");
            }
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(title);
            embed.setDescription(sb.toString());
            embed.setColor(colour);
            return Optional.of(embed);
        } else {
            logger.info("No " + type + " changes found for ticket.");
            return Optional.empty();
        }
    }

    private Document getDocument(String url) throws IOException {
        return Jsoup.connect(url).timeout(10 * 1000).get();
    }
}
