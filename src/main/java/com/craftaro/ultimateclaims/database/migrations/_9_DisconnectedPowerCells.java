package com.craftaro.ultimateclaims.database.migrations;

import com.craftaro.core.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _9_DisconnectedPowerCells extends DataMigration {
    public _9_DisconnectedPowerCells() {
        super(9);
    }

    @Override
    public void migrate(Connection connection, String tablePrefix) throws SQLException {
        String autoIncrement = " AUTO_INCREMENT";

        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE " + tablePrefix + "powercell (" +
                    "id INTEGER PRIMARY KEY" + autoIncrement + ", " +
                    "claim_id INTEGER NOT NULL, " +
                    "world TEXT, " +
                    "x INTEGER, " +
                    "y INTEGER, " +
                    "z INTEGER, " +
                    "inventory TEXT, " +
                    "power INTEGER NOT NULL, " +
                    "eco_bal DOUBLE NOT NULL" +
                    ")");
        }
    }
}
