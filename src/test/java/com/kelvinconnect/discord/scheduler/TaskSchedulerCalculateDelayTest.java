package com.kelvinconnect.discord.scheduler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.Date;
import org.junit.Test;

/**
 * Unit tests for the calculateDelay method(s) of the TaskScheduler class
 *
 * <p>Created by Adam on 22/03/2017.
 */
public class TaskSchedulerCalculateDelayTest {

    private static class TaskSchedulerFixedDate extends TaskScheduler {

        private final Date date;

        TaskSchedulerFixedDate(Date date) {
            this.date = date;
        }

        @Override
        protected Date getCurrentTime() {
            return date; // Wednesday
        }
    }

    private TaskScheduler scheduler;

    public void defaultScheduler() {
        scheduler =
                new TaskSchedulerFixedDate(
                        new Date(Timestamp.valueOf("2017-03-22 17:00:00").getTime()));
    }

    @Test
    public void halfHourDelay() throws Exception {
        defaultScheduler();
        int delayInMinutes = scheduler.calculateDelay(4, 17, 30);
        assertEquals(30, delayInMinutes);
    }

    @Test
    public void hourAndAHalfDelay() throws Exception {
        defaultScheduler();
        int delayInMinutes = scheduler.calculateDelay(4, 18, 30);
        assertEquals(90, delayInMinutes);
    }

    @Test
    public void dayDelay() throws Exception {
        defaultScheduler();
        int delayInMinutes = scheduler.calculateDelay(5, 17, 0);
        assertEquals(60 * 24, delayInMinutes);
    }

    @Test
    public void noDelay() throws Exception {
        defaultScheduler();
        int delayInMinutes = scheduler.calculateDelay(4, 17, 0);
        assertEquals(0, delayInMinutes);
    }

    @Test
    public void halfHourAgoDelay() throws Exception {
        defaultScheduler();
        int delayInMinutes = scheduler.calculateDelay(4, 16, 30);
        assertEquals((7 * 60 * 24) - 30, delayInMinutes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalDays() {
        defaultScheduler();
        scheduler.calculateDelay(8, 17, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalHours() {
        defaultScheduler();
        scheduler.calculateDelay(7, 25, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalMinutes() {
        defaultScheduler();
        scheduler.calculateDelay(7, 17, 61);
    }

    @Test
    public void delayNeverLessThanZero() {
        defaultScheduler();
        for (int day = 1; day <= 7; ++day) {
            for (int hour = 0; hour <= 24; ++hour) {
                for (int minute = 0; minute <= 60; ++minute) {
                    int delay = scheduler.calculateDelay(day, hour, minute);
                    assertThat(
                            "delay should not be less than 0 {day="
                                    + day
                                    + ",hour="
                                    + hour
                                    + ",minute="
                                    + minute
                                    + "}",
                            delay,
                            greaterThanOrEqualTo(0));
                }
            }
        }
    }

    @Test
    public void halfAnHourFromHalf() {
        scheduler =
                new TaskSchedulerFixedDate(
                        new Date(Timestamp.valueOf("2017-03-22 16:30:00").getTime()));
        int delayInMinutes = scheduler.calculateDelay(4, 17, 0);
        assertEquals(30, delayInMinutes);
    }
}
