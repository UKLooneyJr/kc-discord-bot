package com.kelvinconnect.discord;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class DiscordUtils {

    private DiscordUtils() {
        throw new UnsupportedOperationException("do not instantiate");
    }

    public static final long KC_SERVER_ID = 239013363387072514L;

    public static final String INVALID_ARGUMENTS_MESSAGE =
            "Invalid arguments, try '!help' to see instructions.";
    public static final int MAX_MESSAGE_LENGTH = 2000;

    public static String getAuthorShortUserName(Message message) {
        Member member = message.getMember();
        String name = (member != null) ? member.getEffectiveName() : message.getAuthor().getName();
        return name.split(" ")[0];
    }
}
