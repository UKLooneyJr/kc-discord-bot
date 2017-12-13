package com.kelvinconnect.discord.command;

import com.kelvinconnect.discord.DiscordUtils;
import com.kelvinconnect.discord.VotingBooth;
import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

/**
 * Created by Adam on 21/04/2017.
 */
public class VoteCommand implements CommandExecutor {

    private VotingBooth votingBooth;

    public VoteCommand(VotingBooth votingBooth) {
        this.votingBooth = votingBooth;
    }

    @Command(aliases = "!vote", description = "Vote for what pub you want to go to.", usage = "!vote [name]")
    public String onVoteCommand(String[] args, Message message, DiscordApi api) {

        if (args.length < 1) {
            return "You need to vote for something.";
        }

        String name = String.join(" ", args);

        boolean voteChanged = votingBooth.vote(name, message.getAuthor().getIdAsString());

        String voterName = DiscordUtils.getAuthorShortUserName(message);

        if (voteChanged) {
            return "You have changed your vote " + voterName + ".";
        } else {
            return "Thanks for voting " + voterName + ".";
        }
    }
}
