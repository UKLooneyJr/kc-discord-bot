package com.kelvinconnect.discord.command;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

/**
 * Created by Adam on 31/03/2017.
 */
public class BangCommand implements CommandExecutor {

    private int line = 0;

    @Command(aliases = "!bang", description = "Sings a line of a song.", usage = "!bang")
    public String onInfoCommand(String[] args) {
        String message;
        switch (line) {
        case 0:
            message = "He shot me down";
            break;
        case 1:
            message = "I hit the ground";
            break;
        case 2:
            message = "That awful sound";
            break;
        case 3:
            message = "My baby shot me down";
            break;
        case 4:
            message = "I shot you down";
            break;
        case 5:
            message = "You hit the ground";
            break;
        case 6:
            message = "That awful sound";
            break;
        case 7:
            message = "I used to shoot you down";
            break;
        default:
            line = 0;
            return "";
        }
        ++line;
        return message;
    }
}
