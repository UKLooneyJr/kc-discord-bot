package com.kelvinconnect.discord.command.stando.filter;

import net.dv8tion.jda.api.entities.Message;

public interface StandoFilter {

    String filter(String input);

    String filterWithMessage(String input, Message message);
}
