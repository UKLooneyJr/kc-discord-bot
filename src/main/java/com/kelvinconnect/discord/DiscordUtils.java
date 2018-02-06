package com.kelvinconnect.discord;

import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageAuthor;

public class DiscordUtils {

    public static final String INVALID_ARGUMENTS_MESSAGE = "Invalid arguments, try '!help' to see instructions.";

    public static String getAuthorShortUserName(Message message) {
        return getShortUserName(message.getAuthor());
    }

    private static String getShortUserName(MessageAuthor user) {
        String name = user.getDisplayName();
        if(name.contains(" ")){
            return name.substring(0, name.indexOf(" "));
        }
        return name;
    }
}
