package com.craftaro.ultimateclaims.database.migrations;

import com.craftaro.core.database.DataMigration;
import com.craftaro.core.database.DatabaseConnector;

import java.sql.SQLException;
import java.sql.Statement;

public class _2_NewPermissions extends DataMigration {
    public _2_NewPermissions() {
        super(2);
    }

    @Override
    public void migrate(DatabaseConnector connector, String tablePrefix) throws SQLException {
        // Create permissions table
        try (Statement statement = connector.getConnection().createStatement()) {
            statement.execute("ALTER TABLE " + tablePrefix + "permissions ADD COLUMN redstone TINYINT NOT NULL DEFAULT 0");
            statement.execute("ALTER TABLE " + tablePrefix + "permissions ADD COLUMN doors TINYINT NOT NULL DEFAULT 0");
        }
    }
}
