package com.kelvinconnect.discord.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BangCommand implements BotCommand {

    private int line = 0;

    @Override
    public String[] getAliases() {
        return new String[] {"!bang"};
    }

    @Override
    public String getDescription() {
        return "Sings a line of a song.";
    }

    @Override
    public String getUsage() {
        return "!bang";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        String message = getNextLine();
        if (!message.isEmpty()) {
            event.getChannel().sendMessage(message).queue();
        }
    }

    String getNextLine() {
        String message;
        switch (line) {
            case 0:
                message = "He shot me down";
                break;
            case 1:
                message = "I hit the ground";
                break;
            case 2:
            case 6:
                message = "That awful sound";
                break;
            case 3:
                message = "My baby shot me down";
                break;
            case 4:
                message = "I shot you down";
                break;
            case 5:
                message = "You hit the ground";
                break;
            case 7:
                message = "I used to shoot you down";
                break;
            default:
                line = 0;
                return "";
        }
        ++line;
        return message;
    }
}
