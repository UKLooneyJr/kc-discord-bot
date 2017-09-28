package com.kelvinconnect.discord.command;

import de.btobastian.javacord.DiscordAPI;
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
        addStatement("I threw it in the Clyde", "LOW");
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

    @Command(aliases = "!stando", description = "Ask for some help from stando.", usage = "!stando")
    public String onStandoCommand(String[] args, DiscordAPI api) {
        int beers = getBeerCount(args);

        String message;
        if (beers >= 4) {
            message = getRandomStandoStatement(StandoStatement.Severity.HIGH);
        } else if (beers >= 2) {
            message = getRandomStandoStatement(StandoStatement.Severity.MEDIUM);
        } else {
            message = getRandomStandoStatement(StandoStatement.Severity.LOW);
        }

        if (beers >= 6) {
            message = slur(message);
        }

        return "**Steven Standaloft** " + message;
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