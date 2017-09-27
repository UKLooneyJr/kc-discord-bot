package com.kelvinconnect.discord.command;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

import java.util.Random;

/**
 * Created by Adam on 21/04/2017.
 */
public class StandoCommand implements CommandExecutor {

    private final static String[] choices = { "Thats what she said!", "I have ate 4 Mr Kiplings exceedingly good cakes.",
            "The world is flat", "Global warming is made up by the USA government", "I sleep with a tin foil hat on", "The moon landings WERE faked" , "I threw it in the Clyde",
            "I threw half away" };

    @Command(aliases = "!stando", description = "Ask for some help from stando.", usage = "!stando")
    public String onRobertCommand(String[] args, DiscordAPI api) {
        String message = choices[new Random().nextInt(choices.length)];
        return "**Steven Standaloft** " +  message;
    }
}