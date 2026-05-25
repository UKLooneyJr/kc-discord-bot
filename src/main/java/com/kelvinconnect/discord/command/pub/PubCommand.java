package com.kelvinconnect.discord.command.pub;

import com.kelvinconnect.discord.DiscordUtils;
import com.kelvinconnect.discord.command.BotCommand;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Random;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PubCommand implements BotCommand {

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

    @Override
    public String[] getAliases() {
        return new String[] {"!pub", "!vote"};
    }

    @Override
    public String getDescription() {
        return "Ask for some random pub. Or get the results of the pub election.";
    }

    @Override
    public String getUsage() {
        return "!pub [results | reset | time | vote]";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        String command = event.getMessage().getContentRaw().trim().split("\\s+")[0].toLowerCase();
        String reply;

        if ("!vote".equals(command)) {
            reply = submitVote(args, event);
        } else {
            reply = handlePubCommand(args, event);
        }

        event.getChannel().sendMessage(reply).queue();
    }

    private String handlePubCommand(String[] args, MessageReceivedEvent event) {
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
            return submitVote(nameArgs, event);
        }

        String suggestion = choices[random.nextInt(choices.length)];
        return "What about " + suggestion + "?";
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

    private String submitVote(String[] args, MessageReceivedEvent event) {
        if (args.length < 1) {
            return "You need to vote for something.";
        }

        String name = String.join(" ", args);
        boolean voteChanged = votingBooth.vote(name, event.getAuthor().getId());
        String voterName = DiscordUtils.getAuthorShortUserName(event.getMessage());

        if (voteChanged) {
            return "You have changed your vote " + voterName + ".";
        } else {
            return "Thanks for voting " + voterName + ".";
        }
    }
}
