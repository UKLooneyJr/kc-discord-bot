package com.kelvinconnect.discord.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface BotCommand {

    String[] getAliases();

    String getDescription();

    default String getUsage() {
        return "";
    }

    default boolean showInHelpPage() {
        return true;
    }

    void execute(MessageReceivedEvent event, String[] args);
}
