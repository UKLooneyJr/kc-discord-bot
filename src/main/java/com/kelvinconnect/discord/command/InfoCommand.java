package com.kelvinconnect.discord.command;

import java.io.IOException;
import java.util.Properties;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Displays info about the bot
 *
 * <p>Created by Adam on 14/03/2017.
 */
public class InfoCommand implements BotCommand {
    private static final Logger logger = LogManager.getLogger(InfoCommand.class);

    private String projectVersion;

    @Override
    public String[] getAliases() {
        return new String[] {"!info"};
    }

    @Override
    public String getDescription() {
        return "Shows some information about the bot.";
    }

    @Override
    public String getUsage() {
        return "!info";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        String reply =
                "- **Version:** "
                        + getProjectVersion()
                        + "\n"
                        + "- **Author:** Adam Docherty\n"
                        + "- **Language:** Java\n"
                        + "- **Source:** https://github.com/UKLooneyJr/kc-discord-bot/";
        event.getChannel().sendMessage(reply).queue();
    }

    public String getProjectVersion() {
        if (null == projectVersion) {
            final Properties properties = new Properties();
            try {
                properties.load(getClass().getClassLoader().getResourceAsStream(".properties"));
            } catch (IOException e) {
                logger.warn("Error loading .properties file from class loader", e);
            }
            projectVersion = properties.getProperty("version");
            if (null == projectVersion) {
                projectVersion = "Unknown";
            }
        }

        return projectVersion;
    }
}
