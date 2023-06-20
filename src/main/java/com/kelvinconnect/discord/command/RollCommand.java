package com.kelvinconnect.discord.command;

import com.kelvinconnect.discord.DiscordUtils;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RollCommand implements CommandExecutor {
    private static final Logger logger = LogManager.getLogger(RollCommand.class);

    private static final String ROLL_PATTERN = "d?(\\d+)(([d\\-])(\\d+))?";

    @Command(aliases = "!roll", description = "Rolls a dice.", usage = "!roll [<number>[(-|d)<number>]]")
    public String onRollCommand(String[] args) {

        if (args.length == 0) {
            return rollSingle(6);
        } else if (args.length > 1) {
            return DiscordUtils.INVALID_ARGUMENTS_MESSAGE;
        }

        if (!Pattern.matches(ROLL_PATTERN, args[0])) {
            return DiscordUtils.INVALID_ARGUMENTS_MESSAGE;
        }

        Pattern pattern = Pattern.compile(ROLL_PATTERN);
        Matcher matcher = pattern.matcher(args[0]);

        if (matcher.find()) {
            String num1 = matcher.group(1);
            String num2 = matcher.group(4);
            String rollType = matcher.group(3);
            try {
                return roll(Integer.parseInt(num1), num2 != null ? Integer.parseInt(num2) : null, rollType);
            } catch (IllegalArgumentException e) {
                if (logger.isErrorEnabled()) {
                    logger.error(
                            "Failed to parse roll command; num1=" + num1 + ", num2=" + num2 + ", rollType=" + rollType,
                            e);
                }
                return DiscordUtils.INVALID_ARGUMENTS_MESSAGE;
            }
        }
        return DiscordUtils.INVALID_ARGUMENTS_MESSAGE;
    }

    private String roll(Integer num1, Integer num2, String rollType) {
        if (null == rollType) {
            return rollSingle(num1);
        } else if (rollType.equals("d")) {
            return rollMultiple(num1, num2);
        } else if (rollType.equals("-")) {
            return rollRange(num1, num2);
        } else {
            throw new IllegalArgumentException("Do not recognize the rollType = " + rollType);
        }
    }

    private String rollMultiple(int count, int number) {
        int[] rolls = new Random().ints(count, 1, number + 1).toArray();
        int sum = IntStream.of(rolls).sum();
        String message = IntStream.of(rolls).mapToObj(String::valueOf)
                .collect(Collectors.joining(", ", sum + " (", ")"));
        if (message.length() < DiscordUtils.MAX_MESSAGE_LENGTH) {
            return message;
        }
        return message.substring(0, DiscordUtils.MAX_MESSAGE_LENGTH - 3) + "...";
    }

    private String rollRange(int num1, int num2) {
        if (num1 == num2)
            return String.valueOf(num1);
        int min = Math.min(num1, num2);
        int max = Math.max(num1, num2);
        return String.valueOf(new Random().nextInt(max - min + 1) + min);
    }

    private String rollSingle(int maxRoll) {
        return String.valueOf(new Random().nextInt(maxRoll) + 1);
    }
}
