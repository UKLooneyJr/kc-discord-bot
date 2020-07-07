package com.kelvinconnect.discord.command.stando.filter;

import org.javacord.api.entity.message.Message;

public interface StandoFilter {

    String filter(String input);

    String filterWithMessage(String input, Message message);
}
