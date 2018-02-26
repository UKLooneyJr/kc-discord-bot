package com.kelvinconnect.discord.command.stando;

import com.kelvinconnect.discord.persistence.KCBotDatabase;
import com.kelvinconnect.discord.persistence.Table;
import de.btobastian.javacord.utils.logging.LoggerUtil;
import org.slf4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StandoStatementTable extends Table {
    private static final Logger logger = LoggerUtil.getLogger(StandoStatementTable.class);

    public static final String TABLE_NAME = "stando_statements";

    public StandoStatementTable(KCBotDatabase db) {
        super(db, TABLE_NAME);
    }

    @Override
    public String createSql() {
        return "CREATE TABLE IF NOT EXISTS " + tableName + " (\n"
                + " id integer PRIMARY KEY,\n"
                + " statement text,\n"
                + " severity integer\n"
                + ");";
    }

    public void insert(StandoStatement statement) {
        String sql = "INSERT INTO " + tableName + "(statement,severity) VALUES(?,?)";

        db.connect().ifPresent(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, statement.statement);
                pstmt.setInt(2, statement.severity.ordinal());
                pstmt.executeUpdate();
                logger.debug("Inserted stando_statement: " + statement.statement + " into database.");
            } catch (SQLException e) {
                logger.error("Failed to insert statement", e);
            }
        });
    }

    public List<StandoStatement> selectAll() {
        String sql = "SELECT statement, severity FROM " + tableName;

        List<StandoStatement> standoStatements = new ArrayList<>();

        db.connect().ifPresent(conn -> {
            try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

                while(rs.next()) {
                    String statement = rs.getString("statement");
                    Integer severity = rs.getInt("severity");
                    standoStatements.add(new StandoStatement(statement,
                            StandoStatement.Severity.values()[severity]));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return standoStatements;
    }
}
