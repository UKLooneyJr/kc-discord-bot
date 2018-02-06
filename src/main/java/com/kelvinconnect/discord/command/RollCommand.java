package com.kelvinconnect.discord.command;

import com.kelvinconnect.discord.DiscordUtils;
import de.btobastian.javacord.utils.logging.LoggerUtil;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RollCommand implements CommandExecutor {
    private static final Logger logger = LoggerUtil.getLogger(RollCommand.class);

    @Command(aliases = "!roll", description = "Rolls a dice.", usage = "!roll [<number>[(-|d)<number>]]")
    public String onRollCommand(String[] args) {

        if (args.length == 0) {
            return rollSingle("6");
        } else
        if (args.length > 1) {
            return DiscordUtils.INVALID_ARGUMENTS_MESSAGE;
        }

        Pattern pattern = Pattern.compile("((\\d+)(([d\\-])(\\d+))?)?");
        Matcher matcher = pattern.matcher(args[0]);

        if (matcher.find()) {
            String num1 = matcher.group(2);
            String rollType = matcher.group(4);
            String num2 = matcher.group(5);
            try {
                return roll(num1, num2, rollType);
            } catch (IllegalArgumentException e) {
                return DiscordUtils.INVALID_ARGUMENTS_MESSAGE;
            }
        }
        return DiscordUtils.INVALID_ARGUMENTS_MESSAGE;
    }

    private String roll(String num1, String num2, String rollType) {
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

    private String rollMultiple(String count, String number) {
        int n = Integer.parseInt(number);
        int c = Integer.parseInt(count);
        List<Integer> rolls = new ArrayList<>();
        int sum = 0;
        for (int i = 0; i < c; ++i) {
            int roll = new Random().nextInt(n) + 1;
            rolls.add(roll);
            sum += roll;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(sum);
        sb.append(" (");
        for (int i = 0; i < c; ++i) {
            if (i > 0) sb.append(", ");
            sb.append(String.valueOf(rolls.get(i)));
        }
        sb.append(")");
        return sb.toString();
    }

    private String rollRange(String num1, String num2) {
        int min = Integer.parseInt(num1);
        int max = Integer.parseInt(num2);
        if (min == max) return String.valueOf(min);
        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }
        return String.valueOf(new Random().nextInt(max - min + 1) + min);
    }

    private String rollSingle(String number) {
        int n = Integer.parseInt(number);
        return String.valueOf(new Random().nextInt(n) + 1);
    }
}
