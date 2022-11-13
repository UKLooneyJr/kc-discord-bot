package com.kelvinconnect.discord;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;

import java.util.List;
import java.util.Optional;

public class DiscordUtils {
    private static final Logger logger = LogManager.getLogger(DiscordUtils.class);

    private DiscordUtils() {
        throw new UnsupportedOperationException("do not instantiate");
    }

    public static final long KC_SERVER_ID = 239013363387072514L;
    private static final long KC_SERVER_MOD_ROLE_ID = 692322626512617542L;

    public static final String INVALID_ARGUMENTS_MESSAGE =
            "Invalid arguments, try '!help' to see instructions.";
    public static final int MAX_MESSAGE_LENGTH = 2000;

    public static String getAuthorShortUserName(Message message) {
        return getShortUserName(message.getAuthor());
    }

    private static String getShortUserName(MessageAuthor user) {
        return user.getDisplayName().split(" ")[0];
    }

    public static boolean isMod(MessageAuthor messageAuthor) {
        return messageAuthor
                .asUser()
                .map(
                        user -> {
                            Optional<Server> server = user.getApi().getServerById(KC_SERVER_ID);
                            if (!server.isPresent()) {
                                logger.warn(
                                        "Error finding KC server, cannot determine if user {} is a mod",
                                        user.getMentionTag());
                                return false;
                            }

                            List<Role> roles = server.get().getRoles(user);
                            return roles.stream()
                                    .anyMatch(role -> role.getId() == KC_SERVER_MOD_ROLE_ID);
                        })
                .orElse(false);
    }
}
