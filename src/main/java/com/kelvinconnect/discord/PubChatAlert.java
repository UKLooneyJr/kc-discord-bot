package com.kelvinconnect.discord;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.message.MessageBuilder;
import de.btobastian.javacord.utils.LoggerUtil;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Sends an alert to the pub chat channel every friday at 4.
 *
 * Created by Adam on 22/03/2017.
 */
public class PubChatAlert implements Runnable {
    private static final Logger logger = LoggerUtil.getLogger(PubChatAlert.class);

    private final DiscordAPI api;

    private static final int PERIOD = 60 * 24 * 7; // every week

    private static final int DAY = Calendar.FRIDAY;
    private static final int HOUR = 16;
    private static final int MINUTE = 0;

    public PubChatAlert(DiscordAPI api) {
        this.api = api;
    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        int delayInMinutes = calculateDelay();

        logger.info("Timer set for " + delayInMinutes + " minutes.");
        scheduler.scheduleAtFixedRate(this, delayInMinutes, PERIOD, TimeUnit.MINUTES);
    }

    private int calculateDelay() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int delayInMinutes;
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int delayInDays = dayOfWeek <= DAY ? DAY - dayOfWeek : 7 - (dayOfWeek - DAY);

        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int delayInHours = currentHour <= HOUR ? HOUR - currentHour : 24 - (currentHour - HOUR);
        delayInHours += delayInDays * 24;

        int currentMinute = calendar.get(Calendar.MINUTE);
        delayInMinutes = currentMinute < MINUTE ? MINUTE - currentMinute : 60 - (currentMinute - MINUTE);
        delayInMinutes += delayInHours * 60;


        if (delayInMinutes < 0) {
            delayInMinutes += PERIOD;
        }

        return delayInMinutes;
    }

    @Override
    public void run() {
        // bottest = 291689291090886656
        // pubchat = 276318041443270657
        Channel channel = api.getChannelById("276318041443270657");

        MessageBuilder builder = new MessageBuilder();
        builder.append("PUB TIME \uD83C\uDF7B");

        channel.sendMessage(builder.build());
    }
}
