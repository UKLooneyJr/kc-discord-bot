package com.kelvinconnect.discord.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpCommand implements BotCommand {

    private final CommandDispatcher dispatcher;

    public HelpCommand(CommandDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public String[] getAliases() {
        return new String[] {"!help", "!commands"};
    }

    @Override
    public String getDescription() {
        return "Shows this page.";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Commands");
        embed.setDescription(buildDescription());
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private String buildDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("```xml");
        for (BotCommand cmd : dispatcher.getCommands()) {
            if (!cmd.showInHelpPage()) {
                continue;
            }
            builder.append("\n");
            String usage = cmd.getUsage();
            if (usage.isEmpty()) {
                usage = cmd.getAliases()[0];
            }
            builder.append(usage);
            String description = cmd.getDescription();
            if (!description.isEmpty()) {
                builder.append(" | ").append(description);
            }
        }
        builder.append("\n```");
        return builder.toString();
    }
}
