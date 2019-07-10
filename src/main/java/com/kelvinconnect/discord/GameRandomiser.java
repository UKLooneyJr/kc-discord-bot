package com.kelvinconnect.discord;

import org.javacord.api.DiscordApi;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Randomises the game the bot is playing every two minutes
 * <p>
 * Created by Adam on 15/03/2017.
 */
public class GameRandomiser {

    private final Timer timer;
    private final String[] randomGames;

    public GameRandomiser() {
        timer = new Timer();

        randomGames = new String[]{
                "Builder 19.14.14.4a",
                "Builder 17.44.37.3.1d",
                "Netbeans",
                "Oxygen",
                "Cygwin",
                "Tortoise SVN",
                "Jenkins",
                "Command Central App",
                "SourceTree",
                "Android Studio",
                "Builder 21",
                "osu!"
        };
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

    private String getRandomActivity() {
        int rnd = new Random().nextInt(randomGames.length);
        return randomGames[rnd];
    }
}
