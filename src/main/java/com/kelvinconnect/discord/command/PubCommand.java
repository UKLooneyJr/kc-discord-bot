package com.kelvinconnect.discord.command;

import com.kelvinconnect.discord.VotingBooth;
import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Adam on 21/04/2017.
 */
public class PubCommand implements CommandExecutor {

    private final static String[] choices = { "Brass Monkey", "Brewdog",
            "The Islay", "Strip Joint", "Big Slope", "Three Judges", "Sparkle Horse",
            "Brass Monkey", "Brass Monkey", "Brass Monkey", "Snaffle Bit", "The Park Bar",
            "Grove Bar", "Lebowskis", "The Bon Accord", "O'Neill's", "The Pub",
            "The Hengler's Circus", "Yates's", "Slouch", "Malones", "Bunker Bar",
            "The Pot Still", "The Horseshoe Bar", "The Drum & Monkey", "The Counting House",
            "Sloans", "Waxy O'Connors", "Blue Dog", "Gallus", "The Tap House"};


    private VotingBooth votingBooth;
    private final VoteCommand voteCommand;

    public PubCommand(VotingBooth votingBooth, VoteCommand voteCommand) {
        this.votingBooth = votingBooth;
        this.voteCommand = voteCommand;
    }

    @Command(aliases = "!pub", description = "Ask for some random pub. Or get the results of the pub election.", usage = "!pub [results|reset]")
    public String onPubCommand(String[] args, Message message, DiscordApi api) {

        if (args.length == 1) {
            if  (args[0].equals("time")) {
                return timeUntilPub();
            }
            if (args[0].equals("reset")) {
                return resetVoting();
            }
            if (args[0].equals("results")) {
                return voteResults();
            }
        }
        if(args.length >= 2)
        {
            if (args[0].equals("vote")) {
                return submitVote(args, message, api);
            }
        }

        String suggestion = choices[new Random().nextInt(choices.length)];
        return "What about " +  suggestion + "?";
    }

    private String timeUntilPub() {
        LocalDate date = LocalDate.now();
        if (DayOfWeek.FRIDAY != date.getDayOfWeek()) {
            return "It's not Friday, no pub.";
        }
        LocalTime pubTime = LocalTime.parse("16:00:00");
        LocalTime now = LocalTime.now();
        return (now.until(pubTime, ChronoUnit.MINUTES) + 1) + " minute(s) until the pub!";
    }

    private String resetVoting() {
        String winner = votingBooth.getWinner();
        votingBooth.reset();
        return "Voting reset. " + winner + " was the winner!";
    }

    private String voteResults() {
        return "The results are in!\n\n" + votingBooth.getResults();
    }

    private String submitVote(String[] args, Message message, DiscordApi api) {
        String[] nameArgs = Arrays.copyOfRange(args, 1, args.length);
        return voteCommand.onVoteCommand(nameArgs, message, api);
    }
}
