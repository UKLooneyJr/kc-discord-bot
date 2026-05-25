package com.kelvinconnect.discord.command;

import java.util.Random;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RobertCommand implements BotCommand {

    private static final String[] choices = {
        "...Terri?",
        "That's not the first time I've heard that",
        "Happy with that?",
        "From my point of view",
        "Come and read this email",
        "You got a second?",
        "You got a minute?"
    };

    @Override
    public String[] getAliases() {
        return new String[] {"!robert"};
    }

    @Override
    public String getDescription() {
        return "Ask for some help from robert.";
    }

    @Override
    public String getUsage() {
        return "!robert";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        String message = choices[new Random().nextInt(choices.length)];
        event.getChannel().sendMessage("**Robert Mackin** " + message).queue();
    }
}
