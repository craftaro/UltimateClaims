package com.songoda.ultimateclaims.database.migrations;

import com.songoda.ultimateclaims.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _1_InitialMigration extends DataMigration {

    public _1_InitialMigration() {
        super(1);
    }

    @Override
    public void migrate(Connection connection, String tablePrefix) throws SQLException {
        // Create claim table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE " + tablePrefix + "claim (" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "home_world TEXT NULLABLE, " +
                    "home_x DOUBLE NULLABLE, " +
                    "home_y DOUBLE NULLABLE, " +
                    "home_z DOUBLE NULLABLE, " +
                    "home_pitch DOUBLE NULLABLE, " +
                    "home_yaw DOUBLE NULLABLE, " +
                    "powercell_world TEXT NULLABLE, " +
                    "powercell_x INTEGER NULLABLE, " +
                    "powercell_y INTEGER NULLABLE, " +
                    "powercell_z INTEGER NULLABLE, " +
                    "powercell_inventory TEXT NULLABLE, " +
                    "power INTEGER NOT NULL, " +
                    "eco_bal DOUBLE NOT NULL, " +
                    "locked TINYINT NOT NULL" +
                    ")");
        }

        // Create member table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE " + tablePrefix + "member (" +
                    "id INTEGER PRIMARY KEY, " +
                    "claim_id INTEGER NOT NULL, " +
                    "player_uuid VARCHAR(36) NOT NULL, " +
                    "role TINYINT NOT NULL, " +
                    "play_time BIGINT NOT NULL, " +
                    "member_since BIGINT NOT NULL" +
                    ")");
        }

        // Create ban table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE " + tablePrefix + "ban (" +
                    "id INTEGER PRIMARY KEY, " +
                    "claim_id INTEGER NOT NULL, " +
                    "player_uuid VARCHAR(36) NOT NULL" +
                    ")");
        }

        // Create chunk table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE " + tablePrefix + "chunk (" +
                    "id INTEGER PRIMARY KEY, " +
                    "claim_id INTEGER NOT NULL, " +
                    "world TEXT NULLABLE, " +
                    "x INTEGER NULLABLE, " +
                    "z INTEGER NULLABLE" +
                    ")");
        }

        // Create settings table
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE " + tablePrefix + "settings (" +
                    "id INTEGER PRIMARY KEY, " +
                    "claim_id INTEGER NOT NULL, " +
                    "hostile_mob_spawning TINYINT NOT NULL, " +
                    "fire_spread TINYINT NOT NULL, " +
                    "mob_griefing TINYINT NOT NULL" +
                    "leaf_decay TINYINT NOT NULL" +
                    "pvp TINYINT NOT NULL" +
                    ")");
        }
    }

}
