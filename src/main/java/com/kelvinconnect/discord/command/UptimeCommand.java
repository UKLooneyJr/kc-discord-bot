package com.kelvinconnect.discord.command;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

public class UptimeCommand implements CommandExecutor {
    private final Temporal startTime;

    public UptimeCommand(Temporal startTime) {
        this.startTime = startTime;
    }

    @Command(
            aliases = "!uptime",
            description = "Display how long the bot has been running for",
            usage = "!slack")
    public String onUptimeCommand(String[] args) {
        return timeSince(Instant.now());
    }

    String timeSince(Temporal endTime) {
        Duration uptime = Duration.between(startTime, endTime);

        StringBuilder sb = new StringBuilder();
        uptime = writeDuration(uptime, sb, uptime.toDays(), ChronoUnit.DAYS);
        uptime = writeDuration(uptime, sb, uptime.toHours(), ChronoUnit.HOURS);
        uptime = writeDuration(uptime, sb, uptime.toMinutes(), ChronoUnit.MINUTES);
        writeDuration(uptime, sb, uptime.getSeconds(), ChronoUnit.SECONDS);
        return sb.toString();
    }

    private Duration writeDuration(
            Duration uptime, StringBuilder sb, long duration, ChronoUnit unit) {
        if (duration > 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(duration);
            sb.append(" ");
            sb.append(unit.toString().toLowerCase());
            if (duration == 1) {
                sb.setLength(sb.length() - 1);
            }
            return uptime.minus(duration, unit);
        }
        return uptime;
    }
}
