package com.kelvinconnect.discord.persistence;

import com.kelvinconnect.discord.command.stando.StandoStatementTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KCBotDatabase {
    private static final Logger logger = LogManager.getLogger(KCBotDatabase.class);
    private static KCBotDatabase instance;
    private final String url;
    private List<Table> tables;

    private KCBotDatabase(String url) {
        this.url = url;
        initialiseDaos();
        createDatabase();
    }

    public static KCBotDatabase getInstance() {
        if (instance == null) {
            instance = new KCBotDatabase(DBUtils.databasePath());
        }
        return instance;
    }

    public Optional<Connection> connect() {
        try {
            return Optional.of(DriverManager.getConnection(url));
        } catch (SQLException e) {
            logger.error(() -> "Failed to connect to database: url=" + url, e);
            return Optional.empty();
        }
    }

    public Optional<Table> getTable(String tableName) {
        return tables.stream().filter(table -> tableName.equals(table.getTableName())).findFirst();
    }

    private void initialiseDaos() {
        tables = new ArrayList<>();
        tables.add(new StandoStatementTable(this));
    }

    private void createDatabase() {
        connect().ifPresent((Connection c) -> tables.forEach((Table table) -> createTable(c, table)));
    }

    private void createTable(Connection conn, Table table) {
        String sql = table.createSql();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            logger.error(() -> "Failed to create table: " + table.getTableName(), e);
        }
    }
}
