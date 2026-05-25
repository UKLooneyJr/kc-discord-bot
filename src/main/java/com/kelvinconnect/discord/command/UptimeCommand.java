package com.kelvinconnect.discord.command;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UptimeCommand implements BotCommand {
    private final Temporal startTime;

    public UptimeCommand(Temporal startTime) {
        this.startTime = startTime;
    }

    @Override
    public String[] getAliases() {
        return new String[] {"!uptime"};
    }

    @Override
    public String getDescription() {
        return "Display how long the bot has been running for";
    }

    @Override
    public String getUsage() {
        return "!uptime";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        event.getChannel().sendMessage(timeSince(Instant.now())).queue();
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
