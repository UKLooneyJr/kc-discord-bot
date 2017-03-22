package com.kelvinconnect.discord.scheduler;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.message.MessageBuilder;

/**
 * Sends an alert to the pub chat channel every friday at 4.
 *
 * Created by Adam on 22/03/2017.
 */
public class PubChatAlert implements Runnable {

    private final DiscordAPI api;

    public PubChatAlert(DiscordAPI api) {
        this.api = api;
    }

    @Override
    public void run() {
        // bottest = 291689291090886656
        // pubchat = 276318041443270657
        Channel channel = api.getChannelById("276318041443270657");

        MessageBuilder builder = new MessageBuilder();
        builder.append("PUB TIME \uD83C\uDF7B");

        channel.sendMessage(builder.build());
    }
}
