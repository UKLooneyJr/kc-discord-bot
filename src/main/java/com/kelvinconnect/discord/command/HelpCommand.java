package com.kelvinconnect.discord.command;

import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;

import java.awt.*;

/**
 * Created by Adam on 15/03/2017.
 */
public class HelpCommand implements CommandExecutor {

    private final CommandHandler commandHandler;

    public HelpCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Command(aliases = {"!help", "!commands"}, description = "Shows this page.")
    public String onHelpCommand(Message message) {

        StringBuilder builder = buildDescription();
        String description = builder.toString();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription(description);
        embed.setTitle("Commands");

        message.getChannel().sendMessage(embed);

        return null;
    }

    private StringBuilder buildDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("```xml"); // a xml code block looks fancy
        for (CommandHandler.SimpleCommand simpleCommand : commandHandler.getCommands()) {
            if (!simpleCommand.getCommandAnnotation().showInHelpPage()) {
                continue; // skip command
            }
            builder.append("\n");
            if (!simpleCommand.getCommandAnnotation().requiresMention()) {
                // the default prefix only works if the command does not require a mention
                builder.append(commandHandler.getDefaultPrefix());
            }
            String usage = simpleCommand.getCommandAnnotation().usage();
            if (usage.isEmpty()) { // no usage provided, using the first alias
                usage = simpleCommand.getCommandAnnotation().aliases()[0];
            }
            builder.append(usage);
            String description = simpleCommand.getCommandAnnotation().description();
            if (!description.equals("none")) {
                builder.append(" | ").append(description);
            }
        }
        builder.append("\n```"); // end of xml code block
        return builder;
    }
}
