package com.kelvinconnect.discord.scheduler;

import de.btobastian.javacord.utils.logging.LoggerUtil;
import org.slf4j.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Schedules "tasks" (i.e. Runnables) to be run at a set time weekly.
 *
 * Created by Adam on 22/03/2017.
 */
public class TaskScheduler {
    private static final Logger logger = LoggerUtil.getLogger(TaskScheduler.class);

    private static final int WEEK_PERIOD = 60 * 24 * 7; // every week

    private final Map<String, ScheduledFuture> tasks;

    public TaskScheduler() {
        tasks = new HashMap<>();
    }

    public void runWeekly(String name, Runnable runnable, int day, int hour, int minute) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        int delayInMinutes;
        try {
            delayInMinutes = calculateDelay(day, hour, minute);
        } catch (IllegalArgumentException e) {
            logger.error("Error calculating delay.", e);
            return;
        }

        logger.info("Task '" + name + "' will next run in " + delayInMinutes + " minutes.");
        ScheduledFuture future = scheduler.scheduleAtFixedRate(runnable, delayInMinutes, WEEK_PERIOD, TimeUnit.MINUTES);
        tasks.put(name, future);
    }

    public void cancel(String name) {
        ScheduledFuture scheduled = tasks.get(name);
        if (null == scheduled) {
            logger.debug("No tasks exists with the name '" + name + "'.");
            return;
        }
        scheduled.cancel(true);
    }

    public void cancelAll() {
        for (Map.Entry<String, ScheduledFuture> entry : tasks.entrySet()) {
            logger.info("Cancelling task '" + entry.getKey() + "'.");
            ScheduledFuture scheduled = entry.getValue();
            scheduled.cancel(true);
        }
    }

    int calculateDelay(int day, int hour, int minute) {

        if (day > 7) {
            throw new IllegalArgumentException("day cannot be greater than 7 (Calendar.SATURDAY)");
        }
        if (hour > 24) {
            throw new IllegalArgumentException("hour cannot be greater than 24");
        }
        if (minute > 60) {
            throw new IllegalArgumentException("minute cannot be greater than 60");
        }

        Date date = getCurrentTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int delay = 0;

        int currentMinute = calendar.get(Calendar.MINUTE);
        int delayInMinutes = currentMinute <= minute ? minute - currentMinute : 60 - (currentMinute - minute);
        delay += delayInMinutes;
        calendar.add(Calendar.MINUTE, delayInMinutes);

        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int delayInHours = currentHour <= hour ? hour - currentHour : 24 - (currentHour - hour);
        delay += delayInHours * 60;
        calendar.add(Calendar.HOUR_OF_DAY, delayInHours);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int delayInDays = dayOfWeek <= day ? day - dayOfWeek : 7 - (dayOfWeek - day);
        delay += delayInDays * 24 * 60;

        return delay;
    }

    protected Date getCurrentTime() {
        return new Date();
    }
}
