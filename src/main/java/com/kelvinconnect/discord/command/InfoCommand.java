package com.kelvinconnect.discord.command;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Displays info about the bot
 *
 * Created by Adam on 14/03/2017.
 */
public class InfoCommand implements CommandExecutor {

    @Command(aliases = "!info", description = "Shows some information about the bot.", usage = "!info [author|time]")
    public String onInfoCommand(String[] args) {
        if (args.length > 1) { // more than 1 argument
            return "Too many arguments!";
        }
        if (args.length == 0) { // !info
            return "- **Author:** Adam Docherty\n" +
                    "- **Language:** Java\n" +
                    "- **Command-Lib:** sdcf4j";
        }
        if (args.length == 1) { // 1 argument
            if (args[0].equals("author")) { // !info author
                return "- **Name:** Adam Docherty\n" +
                        "- **Age:** 22\n" +
                        "- **Favourite Food:** Mac 'n' Cheese";
            }
            if (args[0].equals("time")) { // !info time
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                Date currentDate = new Date(System.currentTimeMillis());
                return "It's " + format.format(currentDate);
            }
        }
        return "Unknown argument!";
    }
}
