package com.kelvinconnect.discord.persistence;

import com.kelvinconnect.discord.Parameters;

public class DBUtils {

    private DBUtils() {
        throw new UnsupportedOperationException("do not instantiate");
    }

    public static String databasePath() {
        Parameters p = Parameters.getInstance();
        String dir = p.getDatabasePath().orElse(System.getenv("APPDATA") + "/KCBot");
        return "jdbc:sqlite:" + dir + "/kcbot.db";
    }

}
