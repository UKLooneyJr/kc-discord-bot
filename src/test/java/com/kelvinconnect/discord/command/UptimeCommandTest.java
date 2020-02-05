package com.kelvinconnect.discord.command;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.Temporal;

import static org.junit.Assert.*;
import static java.time.temporal.ChronoUnit.*;

public class UptimeCommandTest {

    @Test
    public void test() {
        // Wednesday, 1 January 2020 12:00:00 GMT+00:
        Temporal startTime = Instant.ofEpochSecond(1577880000L);
        UptimeCommand uptimeCommand = new UptimeCommand(startTime);

        // single unit
        assertEquals("1 second", uptimeCommand.timeSince(startTime.plus(1L, SECONDS)));
        assertEquals("2 seconds", uptimeCommand.timeSince(startTime.plus(2L, SECONDS)));
        assertEquals("1 minute", uptimeCommand.timeSince(startTime.plus(1L, MINUTES)));
        assertEquals("2 minutes", uptimeCommand.timeSince(startTime.plus(2L, MINUTES)));
        assertEquals("1 hour", uptimeCommand.timeSince(startTime.plus(1L, HOURS)));
        assertEquals("2 hours", uptimeCommand.timeSince(startTime.plus(2L, HOURS)));
        assertEquals("1 day", uptimeCommand.timeSince(startTime.plus(1L, DAYS)));
        assertEquals("2 days", uptimeCommand.timeSince(startTime.plus(2L, DAYS)));

        // multi units
        assertEquals("1 minute, 1 second", uptimeCommand.timeSince(startTime.plus(1L, SECONDS).plus(1L, MINUTES)));
        assertEquals("1 minute, 2 seconds", uptimeCommand.timeSince(startTime.plus(2L, SECONDS).plus(1L, MINUTES)));
        assertEquals("2 minutes, 1 second", uptimeCommand.timeSince(startTime.plus(1L, SECONDS).plus(2L, MINUTES)));
        assertEquals("1 hour, 1 minute, 1 second",
                uptimeCommand.timeSince(startTime.plus(1L, SECONDS).plus(1L, MINUTES).plus(1L, HOURS)));
        assertEquals("1 hour, 1 minute, 2 seconds",
                uptimeCommand.timeSince(startTime.plus(2L, SECONDS).plus(1L, MINUTES).plus(1L, HOURS)));
        assertEquals("1 hour, 2 minutes, 1 second",
                uptimeCommand.timeSince(startTime.plus(1L, SECONDS).plus(2L, MINUTES).plus(1L, HOURS)));
        assertEquals("2 hours, 1 minute, 1 second",
                uptimeCommand.timeSince(startTime.plus(1L, SECONDS).plus(1L, MINUTES).plus(2L, HOURS)));
        assertEquals("1 hour, 2 minutes, 2 seconds",
                uptimeCommand.timeSince(startTime.plus(2L, SECONDS).plus(2L, MINUTES).plus(1L, HOURS)));
        assertEquals("2 hours, 1 minute, 2 seconds",
                uptimeCommand.timeSince(startTime.plus(2L, SECONDS).plus(1L, MINUTES).plus(2L, HOURS)));
        assertEquals("2 hours, 2 minutes, 1 second",
                uptimeCommand.timeSince(startTime.plus(1L, SECONDS).plus(2L, MINUTES).plus(2L, HOURS)));
        assertEquals("2 hours, 2 minutes, 2 seconds",
                uptimeCommand.timeSince(startTime.plus(2L, SECONDS).plus(2L, MINUTES).plus(2L, HOURS)));
        assertEquals("1 day, 1 hour, 1 minute, 1 second",
                uptimeCommand.timeSince(startTime.plus(1L, SECONDS).plus(1L, MINUTES).plus(1L, HOURS).plus(1L, DAYS)));
        assertEquals("1 day, 1 hour, 1 minute, 2 seconds",
                uptimeCommand.timeSince(startTime.plus(2L, SECONDS).plus(1L, MINUTES).plus(1L, HOURS).plus(1L, DAYS)));
        assertEquals("1 day, 1 hour, 2 minutes, 1 second",
                uptimeCommand.timeSince(startTime.plus(1L, SECONDS).plus(2L, MINUTES).plus(1L, HOURS).plus(1L, DAYS)));
        assertEquals("1 day, 2 hours, 1 minute, 1 second",
                uptimeCommand.timeSince(startTime.plus(1L, SECONDS).plus(1L, MINUTES).plus(2L, HOURS).plus(1L, DAYS)));
        assertEquals("2 days, 1 hour, 1 minute, 1 second",
                uptimeCommand.timeSince(startTime.plus(1L, SECONDS).plus(1L, MINUTES).plus(1L, HOURS).plus(2L, DAYS)));
        assertEquals("1 day, 1 hour, 2 minutes, 2 seconds",
                uptimeCommand.timeSince(startTime.plus(2L, SECONDS).plus(2L, MINUTES).plus(1L, HOURS).plus(1L, DAYS)));
        assertEquals("1 day, 2 hours, 1 minute, 2 seconds",
                uptimeCommand.timeSince(startTime.plus(2L, SECONDS).plus(1L, MINUTES).plus(2L, HOURS).plus(1L, DAYS)));
        assertEquals("2 days, 1 hour, 1 minute, 2 seconds",
                uptimeCommand.timeSince(startTime.plus(2L, SECONDS).plus(1L, MINUTES).plus(1L, HOURS).plus(2L, DAYS)));
        assertEquals("1 day, 2 hours, 2 minutes, 1 second",
                uptimeCommand.timeSince(startTime.plus(1L, SECONDS).plus(2L, MINUTES).plus(2L, HOURS).plus(1L, DAYS)));
        assertEquals("2 days, 1 hour, 2 minutes, 1 second",
                uptimeCommand.timeSince(startTime.plus(1L, SECONDS).plus(2L, MINUTES).plus(1L, HOURS).plus(2L, DAYS)));
        assertEquals("2 days, 2 hours, 1 minute, 1 second",
                uptimeCommand.timeSince(startTime.plus(1L, SECONDS).plus(1L, MINUTES).plus(2L, HOURS).plus(2L, DAYS)));
        assertEquals("1 day, 2 hours, 2 minutes, 2 seconds",
                uptimeCommand.timeSince(startTime.plus(2L, SECONDS).plus(2L, MINUTES).plus(2L, HOURS).plus(1L, DAYS)));
        assertEquals("2 days, 1 hour, 2 minutes, 2 seconds",
                uptimeCommand.timeSince(startTime.plus(2L, SECONDS).plus(2L, MINUTES).plus(1L, HOURS).plus(2L, DAYS)));
        assertEquals("2 days, 2 hours, 1 minute, 2 seconds",
                uptimeCommand.timeSince(startTime.plus(2L, SECONDS).plus(1L, MINUTES).plus(2L, HOURS).plus(2L, DAYS)));
        assertEquals("2 days, 2 hours, 2 minutes, 1 second",
                uptimeCommand.timeSince(startTime.plus(1L, SECONDS).plus(2L, MINUTES).plus(2L, HOURS).plus(2L, DAYS)));
        assertEquals("2 days, 2 hours, 2 minutes, 2 seconds",
                uptimeCommand.timeSince(startTime.plus(2L, SECONDS).plus(2L, MINUTES).plus(2L, HOURS).plus(2L, DAYS)));
    }

}