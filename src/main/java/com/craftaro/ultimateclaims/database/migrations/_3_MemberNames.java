package com.craftaro.ultimateclaims.database.migrations;

import com.craftaro.core.database.DataMigration;
import com.craftaro.core.database.DatabaseConnector;

import java.sql.SQLException;
import java.sql.Statement;

public class _3_MemberNames extends DataMigration {
    public _3_MemberNames() {
        super(3);
    }

    @Override
    public void migrate(DatabaseConnector connector, String tablePrefix) throws SQLException {
        // Add player name to database
        try (Statement statement = connector.getConnection().createStatement()) {
            statement.execute("ALTER TABLE " + tablePrefix + "member ADD COLUMN player_name VARCHAR(16) DEFAULT NULL");
        }
    }
}
