package com.songoda.ultimateclaims.database;

import com.songoda.core.database.DataManagerAbstract;
import com.songoda.core.database.DatabaseConnector;
import com.songoda.core.utils.ItemSerializer;
import com.songoda.ultimateclaims.claim.Audit;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimSetting;
import com.songoda.ultimateclaims.claim.ClaimSettings;
import com.songoda.ultimateclaims.claim.region.ClaimedChunk;
import com.songoda.ultimateclaims.claim.region.ClaimedRegion;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimPerm;
import com.songoda.ultimateclaims.member.ClaimPermissions;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.settings.PluginSettings;
import com.songoda.ultimateclaims.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.*;
import java.util.function.Consumer;

public class DataManager extends DataManagerAbstract {

    public DataManager(DatabaseConnector databaseConnector, Plugin plugin) {
        super(databaseConnector, plugin);
    }

    public void createOrUpdatePluginSettings(PluginSettings pluginSettings) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            // first check to see if there is a data row for plugin settings
            String selectPluginSettings = "SELECT * FROM " + this.getTablePrefix() + "plugin_settings";
            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(selectPluginSettings);
                if (!result.next()) {
                    // no data, so let's make some!
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

            String updatePluginSettings = "UPDATE " + this.getTablePrefix() + "plugin_settings "
                    + "SET spawn_world = ?, spawn_x = ?, spawn_y = ?, spawn_z = ?, spawn_pitch = ?, spawn_yaw = ?";
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
            String createClaim = "INSERT INTO " + this.getTablePrefix() + "claim (name, power, eco_bal, locked) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createClaim)) {
                statement.setString(1, claim.getName());
                statement.setInt(2, claim.getPowerCell().getCurrentPower());
                statement.setDouble(3, claim.getPowerCell().getEconomyBalance());
                statement.setInt(4, claim.isLocked() ? 1 : 0);
                statement.executeUpdate();
            }

            int claimId = this.lastInsertedId(connection);

            this.sync(() -> claim.setId(claimId));

            String createMemberOwner = "INSERT INTO " + this.getTablePrefix() + "member (claim_id, player_uuid, role, play_time, member_since) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createMemberOwner)) {
                statement.setInt(1, claimId);
                statement.setString(2, claim.getOwner().getUniqueId().toString());
                statement.setInt(3, claim.getOwner().getRole().getIndex());
                statement.setLong(4, claim.getOwner().getPlayTime());
                statement.setLong(5, claim.getOwner().getMemberSince());
                statement.executeUpdate();
            }

            ClaimedChunk chunk = claim.getFirstClaimedChunk();

            String createChunk = "INSERT INTO " + this.getTablePrefix() + "chunk (claim_id, region_id, world, x, z) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createChunk)) {
                statement.setInt(1, claimId);
                statement.setString(2, chunk.getRegion().getUniqueId().toString());
                statement.setString(3, chunk.getChunk().getWorld().getName());
                statement.setInt(4, chunk.getChunk().getX());
                statement.setInt(5, chunk.getChunk().getZ());
                statement.executeUpdate();
            }

            String createSettings = "INSERT INTO " + this.getTablePrefix() + "settings (claim_id, hostile_mob_spawning, fire_spread, mob_griefing, leaf_decay, pvp, tnt, fly) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createSettings)) {
                statement.setInt(1, claimId);
                statement.setInt(2, claim.getClaimSettings().isEnabled(ClaimSetting.HOSTILE_MOB_SPAWNING) ? 1 : 0);
                statement.setInt(3, claim.getClaimSettings().isEnabled(ClaimSetting.FIRE_SPREAD) ? 1 : 0);
                statement.setInt(4, claim.getClaimSettings().isEnabled(ClaimSetting.MOB_GRIEFING) ? 1 : 0);
                statement.setInt(5, claim.getClaimSettings().isEnabled(ClaimSetting.LEAF_DECAY) ? 1 : 0);
                statement.setInt(6, claim.getClaimSettings().isEnabled(ClaimSetting.PVP) ? 1 : 0);
                statement.setInt(7, claim.getClaimSettings().isEnabled(ClaimSetting.TNT) ? 1 : 0);
                statement.setInt(8, claim.getClaimSettings().isEnabled(ClaimSetting.FLY) ? 1 : 0);
                statement.executeUpdate();
            }

            String createMemberPermissions = "INSERT INTO " + this.getTablePrefix() + "permissions (claim_id, type, interact, break, place, mob_kill, redstone, doors, trading) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createMemberPermissions)) {
                statement.setInt(1, claimId);
                statement.setString(2, "member");
                statement.setInt(3, claim.getMemberPermissions().hasPermission(ClaimPerm.INTERACT) ? 1 : 0);
                statement.setInt(4, claim.getMemberPermissions().hasPermission(ClaimPerm.BREAK) ? 1 : 0);
                statement.setInt(5, claim.getMemberPermissions().hasPermission(ClaimPerm.PLACE) ? 1 : 0);
                statement.setInt(6, claim.getMemberPermissions().hasPermission(ClaimPerm.MOB_KILLING) ? 1 : 0);
                statement.setInt(7, claim.getMemberPermissions().hasPermission(ClaimPerm.REDSTONE) ? 1 : 0);
                statement.setInt(8, claim.getMemberPermissions().hasPermission(ClaimPerm.DOORS) ? 1 : 0);
                statement.setInt(9, claim.getMemberPermissions().hasPermission(ClaimPerm.TRADING) ? 1 : 0);
                statement.executeUpdate();
            }

            String createVisitorPermissions = "INSERT INTO " + this.getTablePrefix() + "permissions (claim_id, type, interact, break, place, mob_kill, redstone, doors, trading) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createVisitorPermissions)) {
                statement.setInt(1, claimId);
                statement.setString(2, "visitor");
                statement.setInt(3, claim.getVisitorPermissions().hasPermission(ClaimPerm.INTERACT) ? 1 : 0);
                statement.setInt(4, claim.getVisitorPermissions().hasPermission(ClaimPerm.BREAK) ? 1 : 0);
                statement.setInt(5, claim.getVisitorPermissions().hasPermission(ClaimPerm.PLACE) ? 1 : 0);
                statement.setInt(6, claim.getVisitorPermissions().hasPermission(ClaimPerm.MOB_KILLING) ? 1 : 0);
                statement.setInt(7, claim.getVisitorPermissions().hasPermission(ClaimPerm.REDSTONE) ? 1 : 0);
                statement.setInt(8, claim.getVisitorPermissions().hasPermission(ClaimPerm.DOORS) ? 1 : 0);
                statement.setInt(9, claim.getMemberPermissions().hasPermission(ClaimPerm.TRADING) ? 1 : 0);
                statement.executeUpdate();
            }
        }));
    }

    public void updateClaim(Claim claim) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String updateClaim = "UPDATE " + this.getTablePrefix() + "claim SET name = ?, power = ?, eco_bal = ?, locked = ?, home_world = ?, home_x = ?, home_y = ?, home_z = ?, home_pitch = ?, home_yaw = ?, powercell_world = ?, powercell_x = ?, powercell_y = ?, powercell_z = ?, powercell_inventory = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateClaim)) {
                statement.setString(1, claim.getName());
                statement.setInt(2, claim.getPowerCell().getCurrentPower());
                statement.setDouble(3, claim.getPowerCell().getEconomyBalance());
                statement.setInt(4, claim.isLocked() ? 1 : 0);

                if (claim.getHome() != null) {
                    Location location = claim.getHome();
                    statement.setString(5, location.getWorld().getName());
                    statement.setDouble(6, location.getX());
                    statement.setDouble(7, location.getY());
                    statement.setDouble(8, location.getZ());
                    statement.setDouble(9, location.getPitch());
                    statement.setDouble(10, location.getYaw());
                } else {
                    statement.setString(5, null);
                    statement.setNull(6, Types.DOUBLE);
                    statement.setNull(7, Types.DOUBLE);
                    statement.setNull(8, Types.DOUBLE);
                    statement.setNull(9, Types.DOUBLE);
                    statement.setNull(10, Types.DOUBLE);
                }

                if (claim.getPowerCell().hasLocation()) {
                    Location location = claim.getPowerCell().getLocation();
                    statement.setString(11, location.getWorld().getName());
                    statement.setInt(12, location.getBlockX());
                    statement.setInt(13, location.getBlockY());
                    statement.setInt(14, location.getBlockZ());
                    statement.setString(15, ItemSerializer.toBase64(claim.getPowerCell().getItems()));
                } else {
                    statement.setString(11, null);
                    statement.setNull(12, Types.INTEGER);
                    statement.setNull(13, Types.INTEGER);
                    statement.setNull(14, Types.INTEGER);
                    statement.setString(15, null);
                }

                statement.setInt(16, claim.getId());
                statement.executeUpdate();
            }
        }));
    }

    public void bulkUpdateClaims(Collection<Claim> claims) {
        this.databaseConnector.connect(connection -> {
            String updateClaim = "UPDATE " + this.getTablePrefix() + "claim SET name = ?, power = ?, eco_bal = ?, locked = ?, home_world = ?, home_x = ?, home_y = ?, home_z = ?, home_pitch = ?, home_yaw = ?, powercell_world = ?, powercell_x = ?, powercell_y = ?, powercell_z = ?, powercell_inventory = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateClaim)) {
                for (Claim claim : claims) {
                    statement.setString(1, claim.getName());
                    statement.setInt(2, claim.getPowerCell().getCurrentPower());
                    statement.setDouble(3, claim.getPowerCell().getEconomyBalance());
                    statement.setInt(4, claim.isLocked() ? 1 : 0);

                    if (claim.getHome() != null) {
                        Location location = claim.getHome();
                        statement.setString(5, location.getWorld().getName());
                        statement.setDouble(6, location.getX());
                        statement.setDouble(7, location.getY());
                        statement.setDouble(8, location.getZ());
                        statement.setDouble(9, location.getPitch());
                        statement.setDouble(10, location.getYaw());
                    } else {
                        statement.setString(5, null);
                        statement.setNull(6, Types.DOUBLE);
                        statement.setNull(7, Types.DOUBLE);
                        statement.setNull(8, Types.DOUBLE);
                        statement.setNull(9, Types.DOUBLE);
                        statement.setNull(10, Types.DOUBLE);
                    }

                    if (claim.getPowerCell().hasLocation()) {
                        Location location = claim.getPowerCell().getLocation();
                        statement.setString(11, location.getWorld().getName());
                        statement.setInt(12, location.getBlockX());
                        statement.setInt(13, location.getBlockY());
                        statement.setInt(14, location.getBlockZ());
                        statement.setString(15, ItemSerializer.toBase64(claim.getPowerCell().getItems()));
                    } else {
                        statement.setString(11, null);
                        statement.setNull(12, Types.INTEGER);
                        statement.setNull(13, Types.INTEGER);
                        statement.setNull(14, Types.INTEGER);
                        statement.setString(15, null);
                    }

                    statement.setInt(16, claim.getId());
                    statement.addBatch();
                }

                statement.executeBatch();
            }

            String updateMember = "UPDATE " + this.getTablePrefix() + "member SET play_time = ?, player_name = ? WHERE claim_id = ? AND player_uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateMember)) {
                for (Claim claim : claims) {
                    for (ClaimMember member : claim.getOwnerAndMembers()) {
                        statement.setLong(1, member.getPlayTime());
                        if (member.getName() == null)
                            statement.setNull(2, Types.VARCHAR);
                        else
                            statement.setString(2, member.getName());
                        statement.setInt(3, claim.getId());
                        statement.setString(4, member.getUniqueId().toString());
                        statement.addBatch();
                    }
                }

                statement.executeBatch();
            }
        });
    }

    public void deleteClaim(Claim claim) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String deleteClaim = "DELETE FROM " + this.getTablePrefix() + "claim WHERE id = ?";
            String deleteMembers = "DELETE FROM " + this.getTablePrefix() + "member WHERE claim_id = ?";
            String deleteBans = "DELETE FROM " + this.getTablePrefix() + "ban WHERE claim_id = ?";
            String deleteRegions = "DELETE FROM " + this.getTablePrefix() + "claimed_regions WHERE claim_id = ?";
            String deleteChunks = "DELETE FROM " + this.getTablePrefix() + "chunk WHERE claim_id = ?";
            String deleteSettings = "DELETE FROM " + this.getTablePrefix() + "settings WHERE claim_id = ?";
            String deletePermissions = "DELETE FROM " + this.getTablePrefix() + "permissions WHERE claim_id = ?";
            String deleteAudits = "DELETE FROM " + this.getTablePrefix() + "audit_log WHERE claim_id = ?";

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

            try (PreparedStatement statement = connection.prepareStatement(deleteRegions)) {
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

            try (PreparedStatement statement = connection.prepareStatement(deleteAudits)) {
                statement.setInt(1, claim.getId());
                statement.executeUpdate();
            }
        }));
    }

    public void createMember(ClaimMember member) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String createMember = "INSERT INTO " + this.getTablePrefix() + "member (claim_id, player_uuid, player_name, role, play_time, member_since) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createMember)) {
                statement.setInt(1, member.getClaim().getId());
                statement.setString(2, member.getUniqueId().toString());
                if (member.getName() == null)
                    statement.setNull(3, Types.VARCHAR);
                else
                    statement.setString(3, member.getName());
                statement.setInt(4, member.getRole().getIndex());
                statement.setLong(5, member.getPlayTime());
                statement.setLong(6, member.getMemberSince());
                statement.executeUpdate();
            }
        }));
    }

    public void deleteMember(ClaimMember member) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String deleteMember = "DELETE FROM " + this.getTablePrefix() + "member WHERE player_uuid = ? AND claim_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteMember)) {
                statement.setString(1, member.getUniqueId().toString());
                statement.setInt(2, member.getClaim().getId());
                statement.executeUpdate();
            }
        }));
    }

    public void createBan(Claim claim, UUID playerUUID) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String createBan = "INSERT INTO " + this.getTablePrefix() + "ban (claim_id, player_uuid) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createBan)) {
                statement.setInt(1, claim.getId());
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
            }
        }));
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

    public void addAudit(Claim claim, Audit audit) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String createChunk = "INSERT INTO " + this.getTablePrefix() + "audit_log (claim_id, who, time) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createChunk)) {
                statement.setInt(1, claim.getId());
                statement.setString(2, audit.getWho().toString());
                statement.setLong(3, audit.getWhen());
                statement.executeUpdate();
            }
        }));
    }

    public void purgeAuditLog() {
        int purgeAfter = Settings.PURGE_AUDIT_LOG_AFTER.getInt();
        this.async(() -> this.databaseConnector.connect(connection -> {
            String createChunk = Settings.MYSQL_ENABLED.getBoolean() ? "DELETE FROM " + this.getTablePrefix() + "audit_log WHERE time < UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL " + purgeAfter + " DAY))" :
                    "DELETE FROM " + this.getTablePrefix() + "audit_log WHERE strftime('%Y-%m', time / 1000, 'unixepoch') <= date('now','-" + purgeAfter +" day')";
            try (PreparedStatement statement = connection.prepareStatement(createChunk)) {
                statement.executeUpdate();
            }
        }));
    }

    public void createClaimedRegion(ClaimedRegion region) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String createPermission = "INSERT INTO " + this.getTablePrefix() + "claimed_regions (claim_id, id) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createPermission)) {
                statement.setInt(1, region.getClaim().getId());
                statement.setString(2, region.getUniqueId().toString());
                statement.executeUpdate();
            }
        }));
    }

    public void deleteClaimedRegion(ClaimedRegion region) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String deleteRegion = "DELETE FROM " + this.getTablePrefix() + "claimed_regions WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteRegion)) {
                statement.setString(1, region.getUniqueId().toString());
                statement.executeUpdate();
            }
        }));
    }

    public void createClaimedChunk(ClaimedChunk claimedChunk) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String createPermission = "INSERT INTO " + this.getTablePrefix() + "chunk (claim_id, region_id, world, x, z) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createPermission)) {
                statement.setInt(1, claimedChunk.getRegion().getClaim().getId());
                statement.setString(2, claimedChunk.getRegion().getUniqueId().toString());
                statement.setString(3, claimedChunk.getWorld());
                statement.setInt(4, claimedChunk.getX());
                statement.setInt(5, claimedChunk.getZ());
                statement.executeUpdate();
            }
        }));
    }

    public void deleteClaimedChunk(ClaimedChunk claimedChunk) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String deletePermission = "DELETE FROM " + this.getTablePrefix() + "chunk WHERE world = ? AND x = ? AND z = ?";
            try (PreparedStatement statement = connection.prepareStatement(deletePermission)) {
                statement.setString(1, claimedChunk.getWorld());
                statement.setInt(2, claimedChunk.getX());
                statement.setInt(3, claimedChunk.getZ());
                statement.executeUpdate();
            }
        }));
    }

    public void updateClaimedChunks(Set<ClaimedChunk> chunks) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String createMember = "UPDATE " + this.getTablePrefix() + "chunk SET region_id = ? WHERE world = ? AND x = ? AND z = ?";
            try (PreparedStatement statement = connection.prepareStatement(createMember)) {
                for (ClaimedChunk claimedChunk : chunks) {
                    statement.setString(1, claimedChunk.getRegion().getUniqueId().toString());
                    statement.setString(2, claimedChunk.getWorld());
                    statement.setInt(3, claimedChunk.getX());
                    statement.setInt(4, claimedChunk.getZ());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        }));
    }

    public void updateSettings(Claim claim, ClaimSettings settings) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String updateClaim = "UPDATE " + this.getTablePrefix() + "settings SET hostile_mob_spawning = ?, fire_spread = ?, mob_griefing = ?, leaf_decay = ?, pvp = ?, tnt = ?, fly = ? WHERE claim_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateClaim)) {
                statement.setInt(1, settings.isEnabled(ClaimSetting.HOSTILE_MOB_SPAWNING) ? 1 : 0);
                statement.setInt(2, settings.isEnabled(ClaimSetting.FIRE_SPREAD) ? 1 : 0);
                statement.setInt(3, settings.isEnabled(ClaimSetting.MOB_GRIEFING) ? 1 : 0);
                statement.setInt(4, settings.isEnabled(ClaimSetting.LEAF_DECAY) ? 1 : 0);
                statement.setInt(5, settings.isEnabled(ClaimSetting.PVP) ? 1 : 0);
                statement.setInt(6, settings.isEnabled(ClaimSetting.TNT) ? 1 : 0);
                statement.setInt(7, settings.isEnabled(ClaimSetting.FLY) ? 1 : 0);
                statement.setInt(8, claim.getId());
                statement.executeUpdate();
            }
        }));
    }

    public void updatePermissions(Claim claim, ClaimPermissions permissions, ClaimRole role) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String updateClaim = "UPDATE " + this.getTablePrefix() + "permissions SET interact = ?, break = ?, place = ?, mob_kill = ?, redstone = ?, doors = ?, trading = ? WHERE claim_id = ? AND type = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateClaim)) {
                statement.setInt(1, permissions.hasPermission(ClaimPerm.INTERACT) ? 1 : 0);
                statement.setInt(2, permissions.hasPermission(ClaimPerm.BREAK) ? 1 : 0);
                statement.setInt(3, permissions.hasPermission(ClaimPerm.PLACE) ? 1 : 0);
                statement.setInt(4, permissions.hasPermission(ClaimPerm.MOB_KILLING) ? 1 : 0);
                statement.setInt(5, permissions.hasPermission(ClaimPerm.REDSTONE) ? 1 : 0);
                statement.setInt(6, permissions.hasPermission(ClaimPerm.DOORS) ? 1 : 0);
                statement.setInt(7, permissions.hasPermission(ClaimPerm.TRADING) ? 1 : 0);
                statement.setInt(8, claim.getId());
                statement.setString(9, role.name().toLowerCase());
                statement.executeUpdate();
            }
        }));
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

                this.sync(() -> callback.accept(pluginSettings));
            }
        }));
    }

    public void getAuditLog(Claim claim, Consumer<Deque<Audit>> callback) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String selectAudit = "SELECT * FROM " + this.getTablePrefix() + "audit_log WHERE claim_id = ?";
            Deque<Audit> audits = new ArrayDeque<>();
            try (PreparedStatement statement = connection.prepareStatement(selectAudit);){
                statement.setInt(1, claim.getId());
                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    UUID who = UUID.fromString(result.getString("who"));
                    long when = result.getLong("time");
                    audits.addFirst(new Audit(who, when));
                }
            }
            callback.accept(audits);
        }));
    }

    public void getClaims(Consumer<Map<UUID, Claim>> callback) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String selectClaims = "SELECT * FROM " + this.getTablePrefix() + "claim";
            String selectMembers = "SELECT * FROM " + this.getTablePrefix() + "member";
            String selectBans = "SELECT * FROM " + this.getTablePrefix() + "ban";
            String selectChunks = "SELECT * FROM " + this.getTablePrefix() + "chunk";
            String selectRegions = "SELECT * FROM " + this.getTablePrefix() + "claimed_regions";
            String selectSettings = "SELECT * FROM " + this.getTablePrefix() + "settings";
            String selectPermissions = "SELECT * FROM " + this.getTablePrefix() + "permissions";

            Map<Integer, Claim> claims = new HashMap<>();

            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(selectClaims);

                while (result.next()) {
                    Claim claim = new Claim();

                    int claimId = result.getInt("id");
                    claim.setId(claimId);
                    claim.setName(result.getString("name")
                            .replace(ChatColor.COLOR_CHAR + "r", ""));

                    String homeWorld = result.getString("home_world");
                    if (homeWorld != null) {
                        double x = result.getDouble("home_x");
                        double y = result.getDouble("home_y");
                        double z = result.getDouble("home_z");
                        double pitch = result.getDouble("home_pitch");
                        double yaw = result.getDouble("home_yaw");
                        Location location = new Location(Bukkit.getWorld(homeWorld), x, y, z, (float) yaw, (float) pitch);
                        claim.setHome(location);
                    }

                    String powercellWorld = result.getString("powercell_world");
                    if (powercellWorld != null) {
                        double x = result.getDouble("powercell_x");
                        double y = result.getDouble("powercell_y");
                        double z = result.getDouble("powercell_z");
                        Location location = new Location(Bukkit.getWorld(powercellWorld), x, y, z);
                        claim.getPowerCell().setLocation(location);

                        List<ItemStack> items = ItemSerializer.fromBase64(result.getString("powercell_inventory"));
                        claim.getPowerCell().setItems(items);
                    }

                    claim.getPowerCell().setCurrentPower(result.getInt("power"));
                    claim.getPowerCell().setEconomyBalance(result.getDouble("eco_bal"));
                    claim.setLocked(result.getInt("locked") == 1);

                    claims.put(claimId, claim);
                }
            }

            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(selectMembers);
                while (result.next()) {
                    int claimId = result.getInt("claim_id");
                    Claim claim = claims.get(claimId);
                    if (claim == null)
                        continue;

                    UUID playerUUID = UUID.fromString(result.getString("player_uuid"));
                    ClaimRole role = ClaimRole.fromIndex(result.getInt("role"));

                    ClaimMember claimMember = new ClaimMember(claim, playerUUID, result.getString("player_name"), role);
                    claimMember.setPlayTime(result.getLong("play_time"));
                    claimMember.setMemberSince(result.getLong("member_since"));

                    claim.addMember(claimMember);

                    if (claimMember.getRole() == ClaimRole.OWNER)
                        claim.setOwner(claimMember);
                }
            }

            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(selectBans);
                while (result.next()) {
                    int claimId = result.getInt("claim_id");
                    Claim claim = claims.get(claimId);
                    if (claim == null)
                        continue;

                    UUID playerUUID = UUID.fromString(result.getString("player_uuid"));
                    claim.banPlayer(playerUUID);
                }
            }

            Map<UUID, ClaimedRegion> claimedRegions = new HashMap<>();
            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(selectRegions);
                while (result.next()) {
                    int claimId = result.getInt("claim_id");
                    Claim claim = claims.get(claimId);

                    ClaimedRegion region = new ClaimedRegion(UUID.fromString(result.getString("id")), claim);
                    region.getClaim().addClaimedRegion(region);
                    claimedRegions.put(region.getUniqueId(), region);
                }
            }

            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(selectChunks);
                while (result.next()) {
                    String regionId = result.getString("region_id");
                    ClaimedRegion region = regionId == null ? null : claimedRegions.get(UUID.fromString(regionId));

                    String world = result.getString("world");
                    if (world == null) {
                        region.getClaim().removeClaimedRegion(region);
                        continue;
                    }
                    int x = result.getInt("x");
                    int z = result.getInt("z");

                    if (region == null) {
                        int claimId = result.getInt("claim_id");
                        Claim claim = claims.get(claimId);
                        if (claim == null)
                            continue;

                        claim.addClaimedChunk(world, x, z);
                        continue;
                    }
                    region.addChunk(new ClaimedChunk(world, x, z));
                }
            }

            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(selectSettings);
                while (result.next()) {
                    int claimId = result.getInt("claim_id");
                    Claim claim = claims.get(claimId);
                    if (claim == null)
                        continue;

                    claim.getClaimSettings()
                            .setEnabled(ClaimSetting.HOSTILE_MOB_SPAWNING, result.getInt("hostile_mob_spawning") == 1)
                            .setEnabled(ClaimSetting.FIRE_SPREAD, result.getInt("fire_spread") == 1)
                            .setEnabled(ClaimSetting.MOB_GRIEFING, result.getInt("mob_griefing") == 1)
                            .setEnabled(ClaimSetting.LEAF_DECAY, result.getInt("leaf_decay") == 1)
                            .setEnabled(ClaimSetting.PVP, result.getInt("pvp") == 1)
                            .setEnabled(ClaimSetting.TNT, result.getInt("tnt") == 1)
                            .setEnabled(ClaimSetting.FLY, result.getInt("fly") == 1);
                }
            }

            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(selectPermissions);
                while (result.next()) {
                    int claimId = result.getInt("claim_id");
                    Claim claim = claims.get(claimId);
                    if (claim == null)
                        continue;

                    ClaimPermissions permissions = new ClaimPermissions()
                            .setAllowed(ClaimPerm.INTERACT, result.getInt("interact") == 1)
                            .setAllowed(ClaimPerm.BREAK, result.getInt("break") == 1)
                            .setAllowed(ClaimPerm.PLACE, result.getInt("place") == 1)
                            .setAllowed(ClaimPerm.MOB_KILLING, result.getInt("mob_kill") == 1)
                            .setAllowed(ClaimPerm.REDSTONE, result.getInt("redstone") == 1)
                            .setAllowed(ClaimPerm.DOORS, result.getInt("doors") == 1)
                            .setAllowed(ClaimPerm.TRADING, result.getInt("trading") == 1);

                    String type = result.getString("type");
                    switch (type) {
                        case "member":
                            claim.setMemberPermissions(permissions);
                            break;
                        case "visitor":
                            claim.setVisitorPermissions(permissions);
                            break;
                    }
                }
            }

            Map<UUID, Claim> returnClaims = new HashMap<>();
            for (Claim claim : claims.values())
                returnClaims.put(claim.getOwner().getUniqueId(), claim);

            this.sync(() -> callback.accept(returnClaims));
        }));
    }
}
