package com.songoda.ultimateclaims.database;

import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimedChunk;
import com.songoda.ultimateclaims.member.ClaimMember;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

public class DataManager {

    private final Plugin plugin;

    public DataManager(Plugin plugin) {
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

    }

    public void createMember(ClaimMember member) {

    }

    public void updateMember(ClaimMember member) {

    }

    public void deleteMember(ClaimMember member) {

    }

    public void createBan(Claim claim, UUID playerUUID) {

    }

    public void updateBan(Claim claim, UUID playerUUID) {

    }

    public void deleteBan(Claim claim, UUID playerUUID) {

    }

    public void createChunk(ClaimedChunk chunk) {

    }

    public void updateChunk(ClaimedChunk chunk) {

    }

    public void deleteChunk(ClaimedChunk chunk) {

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
