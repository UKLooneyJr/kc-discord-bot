package com.kelvinconnect.discord;

import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;

public class DiscordUtils {

    public static String getAuthorShortUserName(Message message) {
        return getShortUserName(message.getAuthor(),
                message.getChannelReceiver().getServer());
    }

    public static String getShortUserName(User user, Server server) {
        String fullName = user.getNickname(server);
        if (fullName == null) {
            fullName = user.getName();
        }
        String shortName = fullName;
        if(shortName.contains(" ")){
            shortName = shortName.substring(0, shortName.indexOf(" "));
        }
        return shortName;
    }
}
