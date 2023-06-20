package com.kelvinconnect.discord.command.stando;

public class StandoStatement {
    public final Integer id;
    public final String statement;
    public final Severity severity;

    public StandoStatement(Integer id, String statement, Severity severity) {
        this.id = id;
        this.statement = statement;
        this.severity = severity;
    }

    public StandoStatement(String statement, Severity severity) {
        this(null, statement, severity);
    }

    public enum Severity {
        LOW,
        MEDIUM,
        HIGH
    }
}
