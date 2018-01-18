package com.kelvinconnect.discord;

import de.btobastian.javacord.DiscordApi;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Randomises the game the bot is playing every two minutes
 *
 * Created by Adam on 15/03/2017.
 */
public class GameRandomiser {

    private final Timer timer;

    public GameRandomiser() {
        timer = new Timer();
    }

    public void start(final DiscordApi api) {

        TimerTask gameRandomiserTask = new TimerTask() {
            @Override
            public void run() {
                api.updateActivity(getRandomActivity());
            }
        };

        timer.schedule(gameRandomiserTask, 0, 120000);
    }

    public void stop() {
        timer.cancel();
        timer.purge();
    }

    public static String getRandomActivity() {
        String[] randomGames = {
                "Builder 19.14.14.4a",
                "Builder 17.44.37.3.1d",
                "Netbeans",
                "Oxygen",
                "Cygwin",
                "Tortoise SVN",
                "Jenkins"
        };
        int rnd = new Random().nextInt(randomGames.length);
        return randomGames[rnd];
    }
}
