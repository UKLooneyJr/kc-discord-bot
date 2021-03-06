package com.kelvinconnect.discord.command.stando;

public class StandoStatement {
    public final String statement;
    public final Severity severity;

    public StandoStatement(String statement, Severity severity) {
        this.statement = statement;
        this.severity = severity;
    }

    public enum Severity {
        LOW, MEDIUM, HIGH
    }
}
