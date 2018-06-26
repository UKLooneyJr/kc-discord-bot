package com.kelvinconnect.discord.command.stando.filter;

import de.btobastian.javacord.entities.message.Message;

public class EveryoneFilter implements StandoFilter {

    @Override
    public String filter(String input) {
        return replaceEveryone(input, "everyone");
    }

    @Override
    public String filterWithMessage(String input, Message message) {
        return replaceEveryone(input, "<@!" + message.getAuthor().getIdAsString() + ">");
    }

    private String replaceEveryone(String input, String replace) {
        return input.replaceAll("@everyone", replace);
    }
}
