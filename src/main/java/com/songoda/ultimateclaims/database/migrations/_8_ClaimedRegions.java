package com.songoda.ultimateclaims.database.migrations;

import com.songoda.core.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _8_ClaimedRegions extends DataMigration {

    public _8_ClaimedRegions() {
        super(8);
    }

    @Override
    public void migrate(Connection connection, String tablePrefix) throws SQLException {

        // Create claimed_regions table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE " + tablePrefix + "claimed_regions (" +
                    "claim_id VARCHAR(36) NOT NULL, " +
                    "id VARCHAR(36) NOT NULL " +
                    ")");
        }

        - //WE MUST MIGRATE THE CHUNKS AND CREATE REGIONS FOR THEM.

        // Create claimed_chunk table
        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE " + tablePrefix + "chunk DROP COLUMN claim_id");
            statement.execute("ALTER TABLE " + tablePrefix + "chunk ADD COLUMN region_id TINYINT FIRST NOT NULL");
        }

    }

}
