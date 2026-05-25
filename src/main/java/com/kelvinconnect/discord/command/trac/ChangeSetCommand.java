package com.kelvinconnect.discord.command.trac;

import com.kelvinconnect.discord.command.BotCommand;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Creates a embeded link
 *
 * <p>Created by Adam on 15/03/2017.
 */
public class ChangeSetCommand implements BotCommand {
    private static final Logger logger = LogManager.getLogger(ChangeSetCommand.class);

    private static final String REVISION_NUMBER_FORMAT = "^[1-9]\\d*$";
    private static final int MAX_CHANGES_PER_MESSAGE = 25;

    @Override
    public String[] getAliases() {
        return new String[] {"!changeset"};
    }

    @Override
    public String getDescription() {
        return "Creates a link to the mentioned changeset.";
    }

    @Override
    public String getUsage() {
        return "!changeset <revision-number>";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (args.length != 1) {
            event.getChannel().sendMessage("Incorrect number of arguments!").queue();
            return;
        }
        String revision = args[0].trim();

        if (!isValidRevisionNumber(revision)) {
            event.getChannel()
                    .sendMessage(revision + " is not a valid SVN revision.")
                    .queue();
            return;
        }

        String url = getUrl(revision);

        Document doc;
        try {
            doc = getDocument(url);
        } catch (IOException e) {
            e.printStackTrace();
            event.getChannel().sendMessage("Error connecting to " + url).queue();
            return;
        }

        sendEmbedMessage(revision, url, event, doc);
    }

    private boolean isValidRevisionNumber(String revision) {
        Pattern p = Pattern.compile(REVISION_NUMBER_FORMAT);
        Matcher m = p.matcher(revision);
        return m.find();
    }

    private String getUrl(String revision) {
        return "http://trac.pronto.pri/KC/changeset/" + revision;
    }

    private void sendEmbedMessage(
            String revision, String url, MessageReceivedEvent event, Document doc) {
        List<EmbedBuilder> embeds = new ArrayList<>();
        embeds.add(buildEmbedForDescription(revision, url, doc));

        Element files = doc.body().getElementsByClass("files").get(1);
        buildEmbedForChangeType(files, "add", Color.GREEN, "Files added:").ifPresent(embeds::add);
        buildEmbedForChangeType(files, "rem", Color.RED, "Files removed:").ifPresent(embeds::add);
        buildEmbedForChangeType(files, "mod", Color.ORANGE, "Files modified:")
                .ifPresent(embeds::add);
        buildEmbedForChangeType(files, "cp", Color.BLUE, "Files copied:").ifPresent(embeds::add);
        buildEmbedForChangeType(files, "mv", Color.GRAY, "Files moved:").ifPresent(embeds::add);

        for (EmbedBuilder e : embeds) {
            event.getChannel().sendMessageEmbeds(e.build()).queue();
        }
    }

    private EmbedBuilder buildEmbedForDescription(String revision, String url, Document doc) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Revision " + revision);
        embed.setUrl(url);

        String description = doc.body().getElementsByClass("message").get(1).text();
        embed.setDescription(description);

        String author = doc.body().getElementsByClass("author").get(1).text();
        embed.setAuthor(author);

        return embed;
    }

    private Optional<EmbedBuilder> buildEmbedForChangeType(
            Element files, String type, Color colour, String title) {
        Elements changes = files.getElementsByClass(type);
        if (!changes.isEmpty()) {
            StringBuilder sb = new StringBuilder();
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
