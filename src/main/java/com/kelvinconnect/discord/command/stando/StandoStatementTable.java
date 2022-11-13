package com.kelvinconnect.discord.command.stando;

import com.kelvinconnect.discord.persistence.KCBotDatabase;
import com.kelvinconnect.discord.persistence.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class StandoStatementTable extends Table {
    public static final String TABLE_NAME = "stando_statements";
    private static final Logger logger = LogManager.getLogger(StandoStatementTable.class);

    public StandoStatementTable(KCBotDatabase db) {
        super(db, TABLE_NAME);
    }

    @Override
    public String createSql() {
        return "CREATE TABLE IF NOT EXISTS "
                + tableName
                + " (\n"
                + " id integer PRIMARY KEY,\n"
                + " statement text,\n"
                + " severity integer,\n"
                + " author text,\n"
                + " time_added timestamp DEFAULT CURRENT_TIMESTAMP,\n"
                + " deleted bool DEFAULT FALSE\n"
                + ");";
    }

    public void insert(StandoStatement statement) {
        // time_added and deleted will be set automatically with their default values
        String sql = "INSERT INTO " + tableName + "(statement,severity,author) VALUES(?,?,?)";

        db.connect()
                .ifPresent(
                        conn -> {
                            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                                pstmt.setString(1, statement.getStatement());
                                pstmt.setInt(2, statement.getSeverity().ordinal());
                                pstmt.setString(3, statement.getAuthor());
                                pstmt.executeUpdate();

                                ResultSet rs = pstmt.getGeneratedKeys();
                                if (rs.next()) {
                                    int id = rs.getInt(1);
                                    statement.setId(id);
                                }
                                logger.debug(
                                        () ->
                                                "Inserted stando_statement: "
                                                        + statement
                                                        + " into database.");
                            } catch (SQLException e) {
                                logger.error("Failed to insert statement", e);
                            }
                        });
    }

    public List<StandoStatement> selectAll() {
        String sql = "SELECT * FROM " + tableName;

        List<StandoStatement> standoStatements = new ArrayList<>();

        db.connect()
                .ifPresent(
                        conn -> {
                            try (Statement stmt = conn.createStatement();
                                    ResultSet rs = stmt.executeQuery(sql)) {

                                while (rs.next()) {
                                    int id = rs.getInt("id");
                                    String statement = rs.getString("statement");
                                    int severity = rs.getInt("severity");
                                    String author = rs.getString("author");
                                    Instant timeAdded =
                                            Instant.ofEpochMilli(
                                                    rs.getDate("time_added").getTime());
                                    boolean deleted = rs.getBoolean("deleted");
                                    standoStatements.add(
                                            new StandoStatement(
                                                    id,
                                                    statement,
                                                    StandoStatement.Severity.values()[severity],
                                                    author,
                                                    timeAdded,
                                                    deleted));
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });

        return standoStatements;
    }

    public void delete(StandoStatement statement) {
        String sql = "UPDATE " + tableName + " SET deleted=? WHERE id=?";

        db.connect()
                .ifPresent(
                        conn -> {
                            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                                pstmt.setBoolean(1, true);
                                pstmt.setInt(2, statement.getId());
                                pstmt.executeUpdate();

                                logger.debug(() -> "Deleted stando_statement: " + statement);
                            } catch (SQLException e) {
                                logger.error("Failed to delete statement", e);
                            }
                        });
    }
}
