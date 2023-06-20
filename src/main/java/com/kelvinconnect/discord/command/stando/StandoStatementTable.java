package com.kelvinconnect.discord.command.stando;

import com.kelvinconnect.discord.persistence.KCBotDatabase;
import com.kelvinconnect.discord.persistence.Table;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
                + " severity integer\n"
                + ");";
    }

    public void insert(StandoStatement statement) {
        String sql = "INSERT INTO " + tableName + "(statement,severity) VALUES(?,?)";

        db.connect()
                .ifPresent(
                        conn -> {
                            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                                pstmt.setString(1, statement.statement);
                                pstmt.setInt(2, statement.severity.ordinal());
                                pstmt.executeUpdate();
                                logger.debug(
                                        () ->
                                                "Inserted stando_statement: "
                                                        + statement.statement
                                                        + " into database.");
                            } catch (SQLException e) {
                                logger.error("Failed to insert statement", e);
                            }
                        });
    }

    public int count() {
        String sql = "SELECT COUNT(id) as count FROM " + tableName;
        AtomicInteger count = new AtomicInteger(-1);
        db.connect()
                .ifPresent(
                        conn -> {
                            try (Statement stmt = conn.createStatement();
                                    ResultSet rs = stmt.executeQuery(sql)) {
                                while (rs.next()) {
                                    count.set(rs.getInt("count"));
                                }
                            } catch (SQLException e) {
                                logger.error("Error running SQL " + sql, e);
                            }
                        });

        return count.get();
    }

    public List<StandoStatement> selectAll(StandoStatement.Severity maxSeverity) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT id, statement, severity FROM ").append(tableName);
        if (maxSeverity != null) {
            sql.append(" WHERE severity <= ").append(maxSeverity.ordinal());
        }

        List<StandoStatement> standoStatements = new ArrayList<>();
        db.connect()
                .ifPresent(
                        conn -> {
                            try (Statement stmt = conn.createStatement();
                                    ResultSet rs = stmt.executeQuery(sql.toString())) {
                                while (rs.next()) {
                                    int id = rs.getInt("id");
                                    String statement = rs.getString("statement");
                                    int severity = rs.getInt("severity");
                                    standoStatements.add(
                                            new StandoStatement(
                                                    id,
                                                    statement,
                                                    StandoStatement.Severity.values()[severity]));
                                }
                            } catch (SQLException e) {
                                logger.error("Error running SQL " + sql, e);
                            }
                        });

        return standoStatements;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM " + tableName + " WHERE id = (?)";
        AtomicInteger result = new AtomicInteger(-1);

        db.connect()
                .ifPresent(
                        conn -> {
                            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                                pstmt.setInt(1, id);
                                result.set(pstmt.executeUpdate());
                                logger.debug(() -> "Deleted statement with id " + id);
                            } catch (SQLException e) {
                                logger.error("Failed to delete statement with id " + id, e);
                            }
                        });

        if (result.get() < 0) {
            // there was an error deleting, this should be logged above in the catch block
            return false;
        } else if (result.get() == 0) {
            logger.warn("Tried to delete statement with id " + id + " but it could not be found");
            return true; // it's already been deleted, so I guess return true?
        } else {
            return true;
        }
    }
}
