package com.kelvinconnect.discord.command;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Captain Steve Rodger on 21/04/2017.
 */
public class StandoCommand implements CommandExecutor {

    private static final String LEARN_LOW = "New Scientist said ";
    private static final String LEARN_MEDIUM = "The Times said ";
    private static final String LEARN_HIGH = "The Daily Mail said ";

    private static class StandoStatement {
        enum Severity {
            LOW, MEDIUM, HIGH
        }

        String statement;
        Severity severity;

        StandoStatement(String statement, Severity severity) {
            this.statement = statement;
            this.severity = severity;
        }
    }

    private List<StandoStatement> standoStatements;

    public StandoCommand() {
        standoStatements = new ArrayList<>();
        addStatement("That's what she said.", "LOW");
        addStatement("I have ate four of Mr Kiplings cakes. He does make exceedingly good cakes.", "LOW");
        addStatement("I chucked it in the Clyde.", "LOW");
        addStatement("I thew half away.", "LOW");
        addStatement("The world is flat.", "MEDIUM");
        addStatement("I sleep with a tin foil hat on.", "MEDIUM");
        addStatement("Have you not seen The Shining!?", "MEDIUM");
        addStatement("The moon landings WERE faked.", "HIGH");
        addStatement("Global warming is made up by the USA government.", "HIGH");
    }

    private void addStatement(String statement, String severity) {
        try {
            addStatement(statement, StandoStatement.Severity.valueOf(severity));
        } catch (IllegalArgumentException e) {
            // TODO: Log message?
        }
    }

    private void addStatement(String statement, StandoStatement.Severity severity) {
        standoStatements.add(new StandoStatement(statement, severity));
    }

    @Command(aliases = "!stando", description = "Have a chat with Stando. Get him a beer or two for some fun facts.", usage = "!stando [<beverages>]")
    public String onStandoCommand(String[] args, DiscordApi api) {

        String response;

        if (isLearnMessage(args, LEARN_LOW)) {
            response = learnFromLowSource(args);
        } else if (isLearnMessage(args, LEARN_MEDIUM)) {
            response = learnFromMediumSource(args);
        } else if (isLearnMessage(args, LEARN_HIGH)) {
            response = learnFromHighSource(args);
        } else {
            response = giveFunFact(args);
        }

        return "**Steven Standaloft** " + response;
    }

    private String learnFromLowSource(String[] args) {
        String message = String.join(" ", args);
        String fact = message.substring(LEARN_LOW.length());
        addStatement(fact, "LOW");
        return "That's very interesting.";
    }

    private String learnFromMediumSource(String[] args) {
        String message = String.join(" ", args);
        String fact = message.substring(LEARN_MEDIUM.length());
        addStatement(fact, "MEDIUM");
        return "I never knew that.";
    }

    private String learnFromHighSource(String[] args) {
        String message = String.join(" ", args);
        String fact = message.substring(LEARN_HIGH.length());
        addStatement(fact, "HIGH");
        return "Sounds plausible.";
    }

    private boolean isLearnMessage(String[] args, String learnMessagePrefix) {
        String message = String.join(" ", args);
        return message.toLowerCase().startsWith(learnMessagePrefix.toLowerCase()) &&
                message.length() > learnMessagePrefix.length();
    }

    private String giveFunFact(String[] args) {
        int beers = getBeerCount(args);

        String fact;
        if (beers >= 4) {
            fact = getRandomStandoStatement(StandoStatement.Severity.HIGH);
        } else if (beers >= 2) {
            fact = getRandomStandoStatement(StandoStatement.Severity.MEDIUM);
        } else {
            fact = getRandomStandoStatement(StandoStatement.Severity.LOW);
        }

        if (beers >= 6) {
            fact = slur(fact);
        }

        return fact;
    }

    private static int getBeerCount(String[] args) {
        final String[] beerEmojis = { "\uD83C\uDF7A", "\uD83C\uDF7B", "\uD83C\uDF77",
                "\uD83C\uDF78", "\uD83C\uDF79", "\uD83C\uDF7E", "\uD83C\uDF76" };
        int beers = 0;
        for (String arg : args) {
            if(Arrays.stream(beerEmojis).anyMatch(x -> x.equals(arg))) {
                beers++;
            }
        }
        return beers;
    }

    private String getRandomStandoStatement(StandoStatement.Severity maxSeverity) {
        List<StandoStatement> possibleStatements = standoStatements.stream()
                        .filter(s -> s.severity.ordinal() <= maxSeverity.ordinal()).collect(Collectors.toList());
        return possibleStatements.get(new Random().nextInt(possibleStatements.size())).statement;
    }

    private String slur(String message) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (char c : message.toCharArray()) {
            if (c == ' ') {
                sb.append(r.nextInt(5) == 0 ? " ...hic! " : " ");
            } else if (c == 's') {
                sb.append(r.nextBoolean() ? "sh" : "s");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}