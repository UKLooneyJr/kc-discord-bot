package com.kelvinconnect.discord.persistence;

import com.kelvinconnect.discord.Parameters;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DBUtils {

    private DBUtils() {
        throw new UnsupportedOperationException("do not instantiate");
    }

    public static void createDatabaseDir() throws IOException {
        String dir = databaseDir();
        Path path = Paths.get(dir);
        if (Files.notExists(path)) {
            Files.createDirectory(path);
        }
    }

    public static String databasePath() {
        String dir = databaseDir();
        return "jdbc:sqlite:" + dir + "/kcbot.db";
    }

    private static String databaseDir() {
        Parameters p = Parameters.getInstance();
        return p.getDatabasePath().orElse(System.getenv("APPDATA") + "/KCBot");
    }
}
