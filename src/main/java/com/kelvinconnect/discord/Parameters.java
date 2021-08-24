package com.kelvinconnect.discord;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Parameters {
    private static final Logger logger = LogManager.getLogger(Parameters.class);

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

    private Parameters() {

    }

    public void parseCommandLine(String[] args) {
        Options options = new Options();

        Option token = new Option("t", "token", true, "Discord login token");
        options.addOption(token);

        Option dbPath = new Option("d", "database", true, "Path to the database");
        options.addOption(dbPath);

        Option channelListLocation = new Option("c", "channel-list", true, "Location of the channel list xml");
        options.addOption(channelListLocation);

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

        this.token = cmd.getOptionValue("token");
        this.databasePath = cmd.getOptionValue("database");
        this.channelListLocation = cmd.getOptionValue("channel-list");
    }

    public String getToken() {
        return token;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public String getChannelListLocation() {
        return channelListLocation;
    }

}
