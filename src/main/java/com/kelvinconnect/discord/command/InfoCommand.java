package com.kelvinconnect.discord.command;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Displays info about the bot
 *
 * <p>
 * Created by Adam on 14/03/2017.
 */
public class InfoCommand implements CommandExecutor {
    private static final Logger logger = LogManager.getLogger(InfoCommand.class);

    private String projectVersion;

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

    @Command(aliases = "!info", description = "Shows some information about the bot.", usage = "!info")
    public String onInfoCommand(String[] args) {
        return "- **Version:** " + getProjectVersion() + "\n" + "- **Author:** Adam Docherty\n"
                + "- **Language:** Java\n" + "- **Source:** https://github.com/UKLooneyJr/kc-discord-bot/";
    }
}
