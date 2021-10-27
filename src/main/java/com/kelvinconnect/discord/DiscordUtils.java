package com.kelvinconnect.discord;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;

public class DiscordUtils {

    private DiscordUtils() {
        throw new UnsupportedOperationException("do not instantiate");
    }

    public static final long KC_SERVER_ID = 239013363387072514L;

    public static final String INVALID_ARGUMENTS_MESSAGE = "Invalid arguments, try '!help' to see instructions.";
    public static final int MAX_MESSAGE_LENGTH = 2000;

    public static String getAuthorShortUserName(Message message) {
        return getShortUserName(message.getAuthor());
    }

    private static String getShortUserName(MessageAuthor user) {
        String name = user.getDisplayName();
        if (name.contains(" ")) {
            return name.substring(0, name.indexOf(" "));
        }
        return name;
    }
}
