package com.kelvinconnect.discord.command.stando.filter;

import de.btobastian.javacord.entities.message.Message;

public interface StandoFilter {

    public String filter(String input);

    public String filterWithMessage(String input, Message message);
}
