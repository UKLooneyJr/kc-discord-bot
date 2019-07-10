package com.kelvinconnect.discord.command.stando.filter;

import org.javacord.api.entity.message.Message;

public class EveryoneFilter implements StandoFilter {

    @Override
    public String filter(String input) {
        input = replaceEveryone(input, "everyone");
        return replaceHere(input, "here");
    }

    @Override
    public String filterWithMessage(String input, Message message) {
        String authorId = "<@!" + message.getAuthor().getIdAsString() + ">";
        input = replaceUser(input, authorId);
        input = replaceEveryone(input, authorId);
        return replaceHere(input, authorId);
    }

    private String replaceUser(String input, String replace) {
        return input.replaceAll("<@!\\d{18}>", replace);
    }

    private String replaceEveryone(String input, String replace) {
        return input.replaceAll("@everyone", replace);
    }

    private String replaceHere(String input, String replace) {
        return input.replaceAll("@here", replace);
    }
}
