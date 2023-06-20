package com.kelvinconnect.discord.command;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

public class SlackCommand implements CommandExecutor {

    private final String url;

    public SlackCommand(String url) {
        this.url = url;
    }

    @Command(
            aliases = "!slack",
            description = "Gets a link to the Motorola Slack instance.",
            usage = "!slack")
    public String onSlackCommand(String[] args) {
        return url;
    }
}
