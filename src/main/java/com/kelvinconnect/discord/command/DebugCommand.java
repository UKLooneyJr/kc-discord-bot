package com.kelvinconnect.discord.command;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;

/**
 * Displays info about the bot
 * <p>
 * Created by Adam on 14/03/2017.
 */
public class DebugCommand implements CommandExecutor {
    private static final Logger logger = LogManager.getLogger(DebugCommand.class);

    @Command(aliases = "!debug", showInHelpPage = false)
    public void onDebugCommand(DiscordApi api) {
        if (!logger.isDebugEnabled()) {
            return;
        }

        // Don't need these anymore, discord has added features to get the channel and server ids from discord.

//        Collection<Server> sList = api.getServers();
//        logger.debug("Server List:");
//        for (Server s : sList) {
//            logger.debug(s.getName() + " : " + s.getId());
//        }
//
//        Collection<Channel> cList = api.getChannels();
//        logger.debug("Channel List:");
//        for (Channel c : cList) {
//            logger.debug(c.getIdAsString());
//        }
    }
}
