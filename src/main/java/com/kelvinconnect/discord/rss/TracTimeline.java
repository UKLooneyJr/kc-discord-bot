package com.kelvinconnect.discord.rss;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.channels.TextChannel;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class TracTimeline implements Runnable {

    private static final int MAX_FEED = 5;

    private static final boolean INCLUDE_TICKET = true;
    private static final boolean INCLUDE_MILESTONE = true;
    private static final boolean INCLUDE_CHANGESET = true;
    private static final boolean INCLUDE_WIKI = true;

    private final DiscordApi api;
    private final List<String> lastEntries = Arrays.asList(new String[MAX_FEED]);

    public TracTimeline(DiscordApi api) {
        this.api = api;
    }

    @Override
    public void run() {
        printTimeline(api);
    }

    private void printTimeline(DiscordApi api) {
        Feed feed = getFeed();

        api.getTextChannelById(416596650497277962L).ifPresent((channel -> writeFeedToChannel(feed, channel)));
    }

    private void writeFeedToChannel(Feed feed, TextChannel channel) {
        for (int i = 0; i < Math.min(MAX_FEED, feed.getMessages().size()); ++i) {
            FeedMessage message = feed.getMessages().get(i);
            if (!lastEntries.contains(message.getGuid())) {
                writeMessageToChannel(message, channel);
            }
            lastEntries.set(i, message.getGuid());
        }
    }

    private void writeMessageToChannel(FeedMessage message, TextChannel channel) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(message.getTitle());
        eb.setAuthor(message.getAuthor());
        eb.setUrl(message.getLink());

        if (message.getLink().contains("/KC/changeset/")) {
            eb.setColor(Color.BLUE);
        } else if (message.getLink().contains("/KC/ticket/")) {
            eb.setColor(Color.YELLOW);
        } else if (message.getLink().contains("/KC/wiki/")) {
            eb.setColor(Color.GREEN);
        } else if (message.getLink().contains("/KC/milestone/")) {
            eb.setColor(Color.RED);
        }

        channel.sendMessage(eb);
    }

    private Feed getFeed() {
        StringBuilder sb = new StringBuilder();
        sb.append("http://trac.pronto.pri/KC/timeline?");
        if (INCLUDE_TICKET) {
            sb.append("ticket=on&");
        }
        if (INCLUDE_MILESTONE) {
            sb.append("milestone=on&");
        }
        if (INCLUDE_CHANGESET) {
            sb.append("changeset=on&");
        }
        if (INCLUDE_WIKI) {
            sb.append("ticket=on&");
        }
        sb.append("max=");
        sb.append(MAX_FEED);
        sb.append("&authors=&daysback=90&format=rss");
        RSSFeedParser parser = new RSSFeedParser(sb.toString());
        return parser.readFeed();
    }
}
