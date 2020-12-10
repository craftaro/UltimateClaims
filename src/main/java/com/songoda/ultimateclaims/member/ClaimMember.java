package com.songoda.ultimateclaims.member;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class ClaimMember {

    private int id;
    private final Claim claim;
    private final UUID uuid;
    private String lastName;
    private ClaimRole role;
    private boolean isPresent = false;
    private long playTime;
    private long memberSince = System.currentTimeMillis();

    public ClaimMember(Claim claim, UUID uuid, String name, ClaimRole role) {
        this.claim = claim;
        this.uuid = uuid;
        this.lastName = name;
        this.role = role;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public Claim getClaim() {
        return claim;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return lastName;
    }

    public void setName(String name) {
        this.lastName = name;
    }

    public ClaimRole getRole() {
        return role;
    }

    public void setRole(ClaimRole role) {
        this.role = role;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        this.isPresent = present;
    }

    public long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public long getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(long memberSince) {
        this.memberSince = memberSince;
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public void eject(Location location) {
        OfflinePlayer player;
        if (!isPresent || !(player = getPlayer()).isOnline()) return;
        Location spawn = UltimateClaims.getInstance().getPluginSettings().getSpawnPoint();
        if (spawn == null && location == null) return;
        player.getPlayer().teleport(location == null ? spawn : location);
        this.isPresent = false;
    }
}
