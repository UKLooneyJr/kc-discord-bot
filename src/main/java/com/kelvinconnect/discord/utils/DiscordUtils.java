package com.kelvinconnect.discord.utils;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;

public class DiscordUtils {

    public static final Pattern SPLIT_STRING_WITH_QUOTES_PATTERN =
            Pattern.compile("([^\"]\\S*|\".+?\")\\s*");

    private DiscordUtils() {
        throw new UnsupportedOperationException("do not instantiate");
    }

    public static final long KC_SERVER_ID = 239013363387072514L;

    public static final String INVALID_ARGUMENTS_MESSAGE =
            "Invalid arguments, try '!help' to see instructions.";
    public static final int MAX_MESSAGE_LENGTH = 2000;

    public static String getAuthorShortUserName(Message message) {
        return getShortUserName(message.getAuthor());
    }

    private static String getShortUserName(MessageAuthor user) {
        return user.getDisplayName().split(" ")[0];
    }

    public static Optional<CompletableFuture<BufferedImage>> getEmojiImage(
            Server server, String emojiName) {
        return server.getCustomEmojisByName(emojiName).stream()
                .findFirst()
                .map(e -> e.getImage().asBufferedImage());
    }

    public static List<String> parseArgsFromMessage(Message message) {
        return parseArgsFromMessage(message, true);
    }

    public static List<String> parseArgsFromMessage(Message message, boolean removeFirstArg) {
        String str = message.getContent();
        if (null == str || str.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        Matcher m = SPLIT_STRING_WITH_QUOTES_PATTERN.matcher(str);
        while (m.find()) list.add(m.group(1).replace("\"", ""));
        if (removeFirstArg) {
            list.remove(0);
        }
        return list;
    }
}
