package com.kelvinconnect.discord.command.pub;

import com.kelvinconnect.discord.DiscordUtils;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Random;
import org.javacord.api.entity.message.Message;

/** Created by Adam on 21/04/2017. */
public class PubCommand implements CommandExecutor {

    private static final String[] choices = {
        "Brass Monkey",
        "Brewdog",
        "The Islay",
        "Strip Joint",
        "Big Slope",
        "Three Judges",
        "Sparkle Horse",
        "Brass Monkey",
        "Brass Monkey",
        "Brass Monkey",
        "Snaffle Bit",
        "The Park Bar",
        "Grove Bar",
        "Lebowskis",
        "The Bon Accord",
        "O'Neill's",
        "The Pub",
        "The Hengler's Circus",
        "Yates's",
        "Slouch",
        "Malones",
        "Bunker Bar",
        "The Pot Still",
        "The Horseshoe Bar",
        "The Drum & Monkey",
        "The Counting House",
        "Sloans",
        "Waxy O'Connors",
        "Blue Dog",
        "Gallus",
        "The Tap House"
    };

    private final Random random = new Random();
    private final VotingBooth votingBooth;

    public PubCommand() {
        this.votingBooth = new VotingBooth();
    }

    @Command(
            aliases = "!pub",
            description = "Ask for some random pub. Or get the results of the pub election.",
            usage = "!pub [results | reset | time | vote]")
    public String onPubCommand(String[] args, Message message) {

        if (args.length == 1) {
            if (args[0].equals("time")) {
                return timeUntilPub();
            }
            if (args[0].equals("reset")) {
                return resetVoting();
            }
            if (args[0].equals("results")) {
                return voteResults();
            }
        }
        if (args.length >= 2 && args[0].equals("vote")) {
            String[] nameArgs = Arrays.copyOfRange(args, 1, args.length);
            return submitVote(nameArgs, message);
        }

        String suggestion = choices[random.nextInt(choices.length)];
        return "What about " + suggestion + "?";
    }

    @Command(
            aliases = "!vote",
            description = "Vote for what pub you want to go to.",
            usage = "!vote [name]")
    public String onVoteCommand(String[] args, Message message) {
        return submitVote(args, message);
    }

    private String timeUntilPub() {
        LocalDate date = LocalDate.now();
        LocalTime pubTime;
        if (DayOfWeek.FRIDAY == date.getDayOfWeek()) {
            pubTime = LocalTime.parse("16:00:00");
        } else {
            pubTime = LocalTime.parse("17:00:00");
        }
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

    private String submitVote(String[] args, Message message) {
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
