package com.kelvinconnect.discord.persistence;

import com.kelvinconnect.discord.Parameters;

public class DBUtil {

    public static String databasePath() {
        String databasePath = Parameters.getInstance().getDatabasePath();
        if (null != databasePath) {
            return "jdbc:sqlite:" + databasePath + "/kcbot.db";
        } else {
            return "jdbc:sqlite:" + System.getenv("APPDATA") + "/KCBot/kcbot.db";
        }
    }

}
