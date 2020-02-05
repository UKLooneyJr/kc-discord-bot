package com.kelvinconnect.discord.command;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

/**
 * Displays info about the bot
 * <p>
 * Created by Adam on 14/03/2017.
 */
public class InfoCommand implements CommandExecutor {

    @Command(aliases = "!info", description = "Shows some information about the bot.", usage = "!info")
    public String onInfoCommand(String[] args) {
        return "- **Author:** Adam Docherty\n" + "- **Language:** Java\n"
                + "- **Source:** https://github.com/UKLooneyJr/kc-discord-bot/";
    }
}
