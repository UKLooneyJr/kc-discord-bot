package com.kelvinconnect.discord.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SlackCommand implements BotCommand {

    @Override
    public String[] getAliases() {
        return new String[] {"!slack"};
    }

    @Override
    public String getDescription() {
        return "Gets a link to the Motorola Slack instance.";
    }

    @Override
    public String getUsage() {
        return "!slack";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        event.getChannel().sendMessage("⚰️R.I.P").queue();
    }
}
