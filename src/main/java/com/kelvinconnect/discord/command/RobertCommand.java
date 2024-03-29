package com.kelvinconnect.discord.command;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import java.util.Random;
import org.javacord.api.DiscordApi;

/** Created by Adam on 21/04/2017. */
public class RobertCommand implements CommandExecutor {

    private static final String[] choices = {
        "...Terri?",
        "That's not the first time I've heard that",
        "Happy with that?",
        "From my point of view",
        "Come and read this email",
        "You got a second?",
        "You got a minute?"
    };

    @Command(aliases = "!robert", description = "Ask for some help from robert.", usage = "!robert")
    public String onRobertCommand(String[] args, DiscordApi api) {
        String message = choices[new Random().nextInt(choices.length)];
        return "**Robert Mackin** " + message;
    }
}
