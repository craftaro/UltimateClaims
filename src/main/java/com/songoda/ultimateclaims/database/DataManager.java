package com.songoda.ultimateclaims.database;

import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimSettings;
import com.songoda.ultimateclaims.claim.ClaimedChunk;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimPermissions;
import com.songoda.ultimateclaims.settings.PluginSettings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class DataManager {

    private final DatabaseConnector databaseConnector;
    private final Plugin plugin;

    public DataManager(DatabaseConnector databaseConnector, Plugin plugin) {
        this.databaseConnector = databaseConnector;
        this.plugin = plugin;
    }

    /**
     * @return the prefix to be used by all table names
     */
    public String getTablePrefix() {
        return this.plugin.getDescription().getName().toLowerCase() + '_';
    }

    public void createOrUpdatePluginSettings(PluginSettings pluginSettings) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String selectPluginSettings = "SELECT * FROM " + this.getTablePrefix() + "plugin_settings";
            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(selectPluginSettings);
                if (!result.next()) {
                    String createPluginSettings = "INSERT INTO " + this.getTablePrefix() + "plugin_settings (spawn_world, spawn_x, spawn_y, spawn_z, spawn_pitch, spawn_yaw) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement createStatement = connection.prepareStatement(createPluginSettings)) {
                        createStatement.setString(1, null);
                        createStatement.setNull(2, Types.DOUBLE);
                        createStatement.setNull(3, Types.DOUBLE);
                        createStatement.setNull(4, Types.DOUBLE);
                        createStatement.setNull(5, Types.DOUBLE);
                        createStatement.setNull(6, Types.DOUBLE);
                        createStatement.executeUpdate();
                    }
                }
            }

            String updatePluginSettings = "UPDATE " + this.getTablePrefix() + "plugin_settings SET spawn_world = ?, spawn_x = ?, spawn_y = ?, spawn_z = ?, spawn_pitch = ?, spawn_yaw = ?";
            try (PreparedStatement statement = connection.prepareStatement(updatePluginSettings)) {
                if (pluginSettings.getSpawnPoint() != null) {
                    statement.setString(1, pluginSettings.getSpawnPoint().getWorld().getName());
                    statement.setDouble(2, pluginSettings.getSpawnPoint().getX());
                    statement.setDouble(3, pluginSettings.getSpawnPoint().getY());
                    statement.setDouble(4, pluginSettings.getSpawnPoint().getZ());
                    statement.setDouble(5, pluginSettings.getSpawnPoint().getPitch());
                    statement.setDouble(6, pluginSettings.getSpawnPoint().getYaw());
                } else {
                    statement.setString(1, null);
                    statement.setNull(2, Types.DOUBLE);
                    statement.setNull(3, Types.DOUBLE);
                    statement.setNull(4, Types.DOUBLE);
                    statement.setNull(5, Types.DOUBLE);
                    statement.setNull(6, Types.DOUBLE);
                }
                statement.executeUpdate();
            }
        }));
    }

    public void createClaim(Claim claim) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String createClaim = "INSERT INTO " + this.getTablePrefix() + "claim () VALUES ()";
            try (PreparedStatement statement = connection.prepareStatement(createClaim)) {
                statement.setInt(1, claim.getId());
                statement.executeUpdate();
            }

            this.sync(() -> claim.setId(this.lastInsertedId(connection)));
        }));
    }

    public void updateClaim(Claim claim) {

    }

    public void deleteClaim(Claim claim) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String deleteClaim = "DELETE FROM " + this.getTablePrefix() + "claim WHERE id = ?";
            String deleteMembers = "DELETE FROM " + this.getTablePrefix() + "member WHERE claim_id = ?";
            String deleteBans = "DELETE FROM " + this.getTablePrefix() + "ban WHERE claim_id = ?";
            String deleteChunks = "DELETE FROM " + this.getTablePrefix() + "chunk WHERE claim_id = ?";
            String deleteSettings = "DELETE FROM " + this.getTablePrefix() + "settings WHERE claim_id = ?";
            String deletePermissions = "DELETE FROM " + this.getTablePrefix() + "permissions WHERE claim_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(deleteClaim)) {
                statement.setInt(1, claim.getId());
                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement(deleteMembers)) {
                statement.setInt(1, claim.getId());
                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement(deleteBans)) {
                statement.setInt(1, claim.getId());
                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement(deleteChunks)) {
                statement.setInt(1, claim.getId());
                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement(deleteSettings)) {
                statement.setInt(1, claim.getId());
                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement(deletePermissions)) {
                statement.setInt(1, claim.getId());
                statement.executeUpdate();
            }
        }));
    }

    public void createMember(ClaimMember member) {

    }

    public void deleteMember(ClaimMember member) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String deleteMember = "DELETE FROM " + this.getTablePrefix() + "member WHERE id = ?";

            try (PreparedStatement statement = connection.prepareStatement(deleteMember)) {
                statement.setInt(1, member.getId());
                statement.executeUpdate();
            }
        }));
    }

    public void createBan(Claim claim, UUID playerUUID) {

    }

    public void deleteBan(Claim claim, UUID playerUUID) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String deleteBan = "DELETE FROM " + this.getTablePrefix() + "ban WHERE claim_id = ? AND player_uuid = ?";

            try (PreparedStatement statement = connection.prepareStatement(deleteBan)) {
                statement.setInt(1, claim.getId());
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
        }));
    }

    public void createChunk(ClaimedChunk chunk) {

    }

    public void deleteChunk(ClaimedChunk chunk) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String deleteChunk = "DELETE FROM " + this.getTablePrefix() + "chunk WHERE id = ?";

            try (PreparedStatement statement = connection.prepareStatement(deleteChunk)) {
                statement.setInt(1, chunk.getId());
                statement.executeUpdate();
            }
        }));
    }

    public void updateSettings(ClaimSettings settings) {

    }

    public void updatePermissions(ClaimPermissions permissions) {

    }

    public void getPluginSettings(Consumer<PluginSettings> callback) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String selectPluginSettings = "SELECT * FROM " + this.getTablePrefix() + "plugin_settings";

            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(selectPluginSettings);

                PluginSettings pluginSettings = new PluginSettings();
                if (result.next()) {
                    String world = result.getString("spawn_world");
                    if (world != null) {
                        double x = result.getDouble("spawn_x");
                        double y = result.getDouble("spawn_y");
                        double z = result.getDouble("spawn_z");
                        double pitch = result.getDouble("spawn_pitch");
                        double yaw = result.getDouble("spawn_yaw");
                        Location spawnPoint = new Location(Bukkit.getWorld(world), x, y, z, (float) pitch, (float) yaw);

                        pluginSettings.setSpawnPoint(spawnPoint);
                    }
                }

                callback.accept(pluginSettings);
            }
        }));
    }

    public void getClaims(Consumer<List<Claim>> callback) {

    }

    private int lastInsertedId(Connection connection) {
        String query;
        if (this.databaseConnector instanceof SQLiteConnector) {
            query = "SELECT last_insert_rowid()";
        } else {
            query = "SELECT LAST_INSERT_ID()";
        }

        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }

    public void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(this.plugin, runnable);
    }

}
