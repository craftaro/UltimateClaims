package com.songoda.ultimateclaims.database;

import java.sql.Connection;

public abstract class DataMigration {

    private final int revision;

    public DataMigration(int revision) {
        this.revision = revision;
    }

    public abstract void migrate(Connection connection, String tablePrefix);

    /**
     * @return the revision number of this migration
     */
    public int getRevision() {
        return this.revision;
    }

}
