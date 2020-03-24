package com.songoda.ultimateclaims.claim;

import com.songoda.ultimateclaims.UltimateClaims;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ClaimManager {

    // Owner, Claim
    private final Map<UUID, Claim> registeredClaims = new HashMap<>();

    public Claim addClaim(UUID owner, Claim claim) {
        Claim result = this.registeredClaims.put(owner, claim);

        UltimateClaims.getInstance().getDynmapManager().refresh(claim);

        return result;
    }

    public Claim addClaim(Player owner, Claim claim) {
        return addClaim(owner.getUniqueId(), claim);
    }

    public void addClaims(Map<UUID, Claim> claims) {
        this.registeredClaims.putAll(claims);

        claims.values().forEach(UltimateClaims.getInstance().getDynmapManager()::refresh);
    }

    public boolean hasClaim(UUID owner) {
        return this.registeredClaims.containsKey(owner);
    }

    public boolean hasClaim(Player owner) {
        return hasClaim(owner.getUniqueId());
    }

    public boolean hasClaim(Chunk chunk) {
        return this.registeredClaims.values().stream()
                .anyMatch(claim -> claim.containsChunk(chunk));
    }

    public Claim getClaim(UUID owner) {
        return this.registeredClaims.get(owner);
    }

    public Claim getClaim(Player owner) {
        return getClaim(owner.getUniqueId());
    }

    public Claim getClaim(Chunk chunk) {
        return this.registeredClaims.values().stream()
                .filter(claim -> claim.containsChunk(chunk)).findFirst().orElse(null);
    }

    public Claim getClaim(String world, int chunkX, int chunkZ) {
        return this.registeredClaims.values().stream()
                .filter(claim -> claim.containsChunk(world, chunkX, chunkZ)).findFirst().orElse(null);
    }

    public List<Claim> getClaims(OfflinePlayer player) {
        return registeredClaims.values().stream().filter(c -> c.isOwnerOrMember(player)).collect(Collectors.toList());
    }

    public void removeClaim(Claim claim) {
        this.registeredClaims.remove(claim.getOwner().getUniqueId());
    }

    public Collection<Claim> getRegisteredClaims() {
        return registeredClaims.values();
    }
}
