package com.kelvinconnect.discord.persistence;

public abstract class Table {
    protected final KCBotDatabase db;
    protected final String tableName;

    public Table(KCBotDatabase db, String tableName) {
        this.db = db;
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public abstract String createSql();

}
