package com.kelvinconnect.discord.command.stando.filter;

import org.javacord.api.entity.message.Message;

public interface StandoFilter {

    public String filter(String input);

    public String filterWithMessage(String input, Message message);
}
