package com.kelvinconnect.discord.scheduler;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;

/**
 * Sends an alert to the pub chat channel every friday at 4.
 * <p>
 * Created by Adam on 22/03/2017.
 */
public class PubChatAlert implements Runnable {

    private final DiscordApi api;

    public PubChatAlert(DiscordApi api) {
        this.api = api;
    }

    @Override
    public void run() {
        MessageBuilder builder = new MessageBuilder();
        builder.append("PUB TIME \uD83C\uDF7B");

        // bottest = 291689291090886656
        // pubchat = 276318041443270657
        api.getChannelById("276318041443270657").filter(c -> c instanceof TextChannel)
                .ifPresent(c -> builder.send((TextChannel) c));
    }
}
