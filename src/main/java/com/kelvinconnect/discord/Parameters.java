package com.kelvinconnect.discord;

import java.util.Optional;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Parameters {
    private static final Logger logger = LogManager.getLogger(Parameters.class);

    private static final String TOKEN_OPTION = "token";
    private static final String DATABASE_PATH_OPTION = "database";
    private static final String CHANNEL_LIST_LOCATION_OPTION = "channel-list";

    private static Parameters instance;

    public static Parameters getInstance() {
        if (null == instance) {
            instance = new Parameters();
        }
        return instance;
    }

    private String token;
    private String databasePath;
    private String channelListLocation;

    private Parameters() {}

    public void parseCommandLine(String[] args) {
        Options options = new Options();

        options.addOption(new Option("t", TOKEN_OPTION, true, "Discord login token"));
        options.addOption(new Option("d", DATABASE_PATH_OPTION, true, "Path to the database"));
        options.addOption(
                new Option(
                        "c",
                        CHANNEL_LIST_LOCATION_OPTION,
                        true,
                        "Location of the channel list xml"));

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            logger.error("Failed to pass command line arguments.", e);
            formatter.printHelp("kc-bot", options);
            System.exit(1);
            return;
        }

        this.token = cmd.getOptionValue(TOKEN_OPTION);
        this.databasePath = cmd.getOptionValue(DATABASE_PATH_OPTION);
        this.channelListLocation = cmd.getOptionValue(CHANNEL_LIST_LOCATION_OPTION);
    }

    public Optional<String> getToken() {
        return Optional.ofNullable(token);
    }

    public Optional<String> getDatabasePath() {
        return Optional.ofNullable(databasePath);
    }

    public Optional<String> getChannelListLocation() {
        return Optional.ofNullable(channelListLocation);
    }
}
