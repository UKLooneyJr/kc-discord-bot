package com.kelvinconnect.discord.command;

import com.kelvinconnect.discord.VotingBooth;
import de.btobastian.javacord.DiscordAPI;
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

    @Command(aliases = "!vote", description = "Vote for what pub you want to go to.", usage = "!vote [o]")
    public String onVoteCommand(String[] args, Message message, DiscordAPI api) {

        if (args.length < 1) {
            return "You need to vote for something.";
        }

        String name = String.join(" ", args);

        votingBooth.vote(name);

        String voterName;
        voterName = message.getAuthor().getNickname(message.getChannelReceiver().getServer());
        if (voterName == null) {
            voterName = message.getAuthor().getName();
        }

        return "Thanks for voting " + voterName + ".";
    }
}
