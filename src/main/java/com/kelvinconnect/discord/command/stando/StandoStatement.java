package com.kelvinconnect.discord.command.stando;

import java.time.Instant;

public class StandoStatement {
    private int id;
    private final String statement;
    private final Severity severity;
    private final String author;
    private final Instant timeAdded;
    private boolean deleted;

    public StandoStatement(
            int id,
            String statement,
            Severity severity,
            String author,
            Instant timeAdded,
            boolean deleted) {
        this.id = id;
        this.statement = statement;
        this.severity = severity;
        this.author = author;
        this.timeAdded = timeAdded;
        this.deleted = deleted;
    }

    public StandoStatement(String statement, Severity severity, String author) {
        this.statement = statement;
        this.severity = severity;
        this.author = author;
        this.timeAdded = Instant.now();
        this.deleted = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatement() {
        return statement;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getAuthor() {
        return author;
    }

    public Instant getTimeAdded() {
        return timeAdded;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public enum Severity {
        LOW,
        MEDIUM,
        HIGH
    }

    @Override
    public String toString() {
        return "StandoStatement{"
                + "id="
                + id
                + ", statement='"
                + statement
                + '\''
                + ", severity="
                + severity
                + ", author='"
                + author
                + '\''
                + ", timeAdded="
                + timeAdded
                + ", deleted="
                + deleted
                + '}';
    }
}
