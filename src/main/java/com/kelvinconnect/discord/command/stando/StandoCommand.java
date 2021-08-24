package com.kelvinconnect.discord.command.stando;

import com.kelvinconnect.discord.command.stando.filter.EveryoneFilter;
import com.kelvinconnect.discord.command.stando.filter.SlurFilter;
import com.kelvinconnect.discord.command.stando.filter.StandoFilter;
import com.kelvinconnect.discord.persistence.KCBotDatabase;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.entity.message.Message;

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

    private static final String QUERY_COUNT = "How much do you know?";
    private static final String QUERY_DRINK = "What do you drink?";


    private static final String[] DRINK_EMOJIS = {"\uD83C\uDF7A", "\uD83C\uDF7B", "\uD83C\uDF77", "\uD83C\uDF78", "\uD83C\uDF79",
            "\uD83C\uDF7E", "\uD83C\uDF76", "\uD83E\uDD42", "\uD83E\uDD43"};

    private List<StandoStatement> standoStatements;
    private final List<StandoFilter> inputFilters = new ArrayList<>();
    private final List<StandoFilter> outputFilters = new ArrayList<>();

    public StandoCommand() {
        loadFilters();
        loadStatements();
    }

    private static int getDrinkCount(String[] args) {
        return (int) Arrays.stream(args).filter(arg -> Arrays.asList(DRINK_EMOJIS).contains(arg)).count();
    }

    private void loadFilters() {
        inputFilters.add(new EveryoneFilter());

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
        } else if (isQueryMessage(args, QUERY_COUNT)) {
            response = countStatements();
        } else if (isQueryMessage(args, QUERY_DRINK)) {
            response = listDrinks();
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

    private String countStatements() {
        int count = standoStatements.size();
        return "I know " + count + " facts.";
    }

    private String listDrinks() {
        return "I drink " + Arrays.toString(DRINK_EMOJIS);
    }

    private boolean isLearnMessage(String[] args, String learnMessagePrefix) {
        String message = String.join(" ", args);
        return message.toLowerCase().startsWith(learnMessagePrefix.toLowerCase())
                && message.length() > learnMessagePrefix.length();
    }

    private boolean isQueryMessage(String[] args, String query) {
        String message = String.join(" ", args);
        return message.equalsIgnoreCase(query);
    }

    private String giveFunFact(String[] args, Message message) {
        int drinks = getDrinkCount(args);

        String fact;
        if (drinks >= 4) {
            fact = getRandomStandoStatement(StandoStatement.Severity.HIGH);
        } else if (drinks >= 2) {
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

    private String getRandomStandoStatement(StandoStatement.Severity maxSeverity) {
        List<StandoStatement> possibleStatements = standoStatements.stream()
                .filter(s -> s.severity.ordinal() <= maxSeverity.ordinal()).collect(Collectors.toList());
        return possibleStatements.get(new Random().nextInt(possibleStatements.size())).statement;
    }
}