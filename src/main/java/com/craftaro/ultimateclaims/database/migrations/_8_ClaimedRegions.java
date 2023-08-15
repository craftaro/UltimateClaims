package com.craftaro.ultimateclaims.database.migrations;

import com.craftaro.core.database.DataMigration;
import com.craftaro.core.database.DatabaseConnector;

import java.sql.SQLException;
import java.sql.Statement;

public class _8_ClaimedRegions extends DataMigration {
    public _8_ClaimedRegions() {
        super(8);
    }

    @Override
    public void migrate(DatabaseConnector connector, String tablePrefix) throws SQLException {
        // Create claimed_regions table
        try (Statement statement = connector.getConnection().createStatement()) {
            statement.execute("CREATE TABLE " + tablePrefix + "claimed_regions (" +
                    "claim_id INTEGER NOT NULL, " +
                    "id VARCHAR(36) NOT NULL " +
                    ")");
        }

        // Create claimed_chunk table
        try (Statement statement = connector.getConnection().createStatement()) {
            statement.execute("ALTER TABLE " + tablePrefix + "chunk ADD COLUMN region_id VARCHAR (36)");
        }
    }
}
