package com.songoda.ultimateclaims.database;

import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimSettings;
import com.songoda.ultimateclaims.claim.ClaimedChunk;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimPermissions;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

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

    public void createClaim(Claim claim) {

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

    public List<Claim> getClaims() {
        return null;
    }

    public void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }

    public void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(this.plugin, runnable);
    }

}
