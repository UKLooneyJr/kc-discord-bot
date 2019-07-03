package com.kelvinconnect.discord.command.stando;

import com.kelvinconnect.discord.command.stando.filter.EveryoneFilter;
import com.kelvinconnect.discord.command.stando.filter.SlurFilter;
import com.kelvinconnect.discord.command.stando.filter.StandoFilter;
import com.kelvinconnect.discord.persistence.KCBotDatabase;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.kelvinconnect.discord.command.stando.StandoStatement.Severity.*;

/**
 * Created by Captain Steve Rodger on 21/04/2017.
 */
public class StandoCommand implements CommandExecutor {

    private static final String LEARN_LOW = "New Scientist said ";
    private static final String LEARN_MEDIUM = "The Times said ";
    private static final String LEARN_HIGH = "The Daily Mail said ";

    private List<StandoStatement> standoStatements;
    private List<StandoFilter> inputFilters;
    private List<StandoFilter> outputFilters;

    public StandoCommand() {
        loadFilters();
        loadStatements();
    }

    private void loadFilters() {
        inputFilters = new ArrayList<>();
        inputFilters.add(new EveryoneFilter());
        outputFilters = new ArrayList<>();
        outputFilters.add(new SlurFilter());
        outputFilters.add(new EveryoneFilter());
    }

    private void loadStatements() {
        standoStatements = getDatabaseTable().selectAll();
        if (standoStatements.isEmpty()) {
            populateWithDefaultStandoStatements();
        }
    }

    private StandoStatementTable getDatabaseTable() {
        KCBotDatabase db = KCBotDatabase.getInstance();
        return (StandoStatementTable) db.getTable(StandoStatementTable.TABLE_NAME)
                .orElseThrow(() -> new IllegalStateException("Could not find StandoStatementTable in database"));
    }

    private void populateWithDefaultStandoStatements() {
        addStatement("That's what she said.", LOW);
        addStatement("I have ate four of Mr Kiplings cakes. He does make exceedingly good cakes.", LOW);
        addStatement("I chucked it in the Clyde.", LOW);
        addStatement("I thew half away.", LOW);
        addStatement("The world is flat.", MEDIUM);
        addStatement("I sleep with a tin foil hat on.", MEDIUM);
        addStatement("Have you not seen The Shining!?", MEDIUM);
        addStatement("The moon landings WERE faked.", HIGH);
        addStatement("Global warming is made up by the USA government.", HIGH);
    }

    private void addStatement(String statement, StandoStatement.Severity severity) {
        addStatement(statement, severity, null);
    }

    private void addStatement(String statement, StandoStatement.Severity severity, Message message) {
        for (StandoFilter filter : inputFilters) {
            statement = message != null ? filter.filterWithMessage(statement, message) : filter.filter(statement);
        }
        StandoStatement s = new StandoStatement(statement, severity);
        standoStatements.add(s);
        getDatabaseTable().insert(s);
    }

    @Command(aliases = "!stando", description = "Have a chat with Stando. Get him a beer or two for some fun facts.", usage = "!stando [<beverages>]")
    public String onStandoCommand(String[] args, Message message) {

        String fullFact = String.join(" ", args);
        String response;

        if (isLearnMessage(args, LEARN_LOW)) {
            response = learnFromLowSource(fullFact, message);
        } else if (isLearnMessage(args, LEARN_MEDIUM)) {
            response = learnFromMediumSource(fullFact, message);
        } else if (isLearnMessage(args, LEARN_HIGH)) {
            response = learnFromHighSource(fullFact, message);
        } else {
            response = giveFunFact(args, message);
        }

        return "**Steven Standaloft** " + response;
    }

    private String learnFromLowSource(String fullFact, Message message) {
        String fact = fullFact.substring(LEARN_LOW.length());
        addStatement(fact, LOW, message);
        return "That's very interesting.";
    }

    private String learnFromMediumSource(String fullFact, Message message) {
        String fact = fullFact.substring(LEARN_MEDIUM.length());
        addStatement(fact, MEDIUM, message);
        return "I never knew that.";
    }

    private String learnFromHighSource(String fullFact, Message message) {
        String fact = fullFact.substring(LEARN_HIGH.length());
        addStatement(fact, HIGH, message);
        return "Sounds plausible.";
    }

    private boolean isLearnMessage(String[] args, String learnMessagePrefix) {
        String message = String.join(" ", args);
        return message.toLowerCase().startsWith(learnMessagePrefix.toLowerCase()) &&
                message.length() > learnMessagePrefix.length();
    }

    private String giveFunFact(String[] args, Message message) {
        int beers = getBeerCount(args);

        String fact;
        if (beers >= 4) {
            fact = getRandomStandoStatement(StandoStatement.Severity.HIGH);
        } else if (beers >= 2) {
            fact = getRandomStandoStatement(StandoStatement.Severity.MEDIUM);
        } else {
            fact = getRandomStandoStatement(LOW);
        }

        fact = filterFact(fact, message);

        return fact;
    }

    private String filterFact(String fact, Message message) {
        for (StandoFilter filter : outputFilters) {
            fact = filter.filterWithMessage(fact, message);
        }
        return fact;
    }

    private static int getBeerCount(String[] args) {
        final String[] beerEmojis = {"\uD83C\uDF7A", "\uD83C\uDF7B", "\uD83C\uDF77",
                "\uD83C\uDF78", "\uD83C\uDF79", "\uD83C\uDF7E", "\uD83C\uDF76"};
        int beers = 0;
        for (String arg : args) {
            if (Arrays.stream(beerEmojis).anyMatch(x -> x.equals(arg))) {
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
}