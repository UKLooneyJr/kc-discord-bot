package com.kelvinconnect.discord.persistence;

public class DBUtil {

    public static String databasePath() {
        return "jdbc:sqlite:" + System.getenv("APPDATA") + "/KCBot/kcbot.db";
    }

}
