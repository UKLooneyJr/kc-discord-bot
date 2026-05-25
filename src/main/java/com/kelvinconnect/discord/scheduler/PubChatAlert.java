package com.kelvinconnect.discord.scheduler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Sends an alert to the pub chat channel every friday at 4.
 *
 * <p>Created by Adam on 22/03/2017.
 */
public class PubChatAlert implements Runnable {
    private static final Logger logger = LogManager.getLogger(PubChatAlert.class);

    private static final String PUB_CHAT_CHANNEL_ID = "276318041443270657";

    private final JDA jda;

    public PubChatAlert(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        TextChannel channel = jda.getTextChannelById(PUB_CHAT_CHANNEL_ID);
        if (channel != null) {
            channel.sendMessage("PUB TIME 🍻").queue();
        } else {
            logger.warn("Could not find pub chat channel {}", PUB_CHAT_CHANNEL_ID);
        }
    }
}
