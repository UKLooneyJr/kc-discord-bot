package com.kelvinconnect.discord.rss;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.channels.TextChannel;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class TracTimeline implements Runnable {

    private static final int MAX_FEED = 5;

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
            if (!lastEntries.contains(message.getLink())) {
                writeMessageToChannel(message, channel);
            }
            lastEntries.set(i, message.getLink());
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
        RSSFeedParser parser = new RSSFeedParser(
                "http://trac.kelvinconnect.local/KC/timeline?ticket=on&milestone=on&changeset=on&wiki=on&max=" +
                        MAX_FEED + "&authors=&daysback=90&format=rss");
        return parser.readFeed();
    }
}
