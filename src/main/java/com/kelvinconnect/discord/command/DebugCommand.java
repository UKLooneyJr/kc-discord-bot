package com.kelvinconnect.discord.command;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.utils.logging.LoggerUtil;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.slf4j.Logger;

/**
 * Displays info about the bot
 *
 * Created by Adam on 14/03/2017.
 */
public class DebugCommand implements CommandExecutor {
    private static final Logger logger = LoggerUtil.getLogger(DebugCommand.class);

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
