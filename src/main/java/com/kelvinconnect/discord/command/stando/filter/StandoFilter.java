package com.kelvinconnect.discord.command.stando.filter;

import de.btobastian.javacord.entities.message.Message;

import java.util.Optional;

public interface StandoFilter {

    public String filter(String input);

    public String filterWithMessage(String input, Message message);
}
