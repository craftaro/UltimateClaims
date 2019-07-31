package com.songoda.ultimateclaims.claim;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.*;

public class ClaimManager {

    // Owner, Claim
    private final Map<UUID, Claim> registeredClaims = new HashMap<>();

    public Claim addClaim(UUID owner, Claim claim) {
        return this.registeredClaims.put(owner, claim);
    }

    public Claim addClaim(Player owner, Claim claim) {
        return addClaim(owner.getUniqueId(), claim);
    }

    public void addClaims(Map<UUID, Claim> claims) {
        this.registeredClaims.putAll(claims);
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

    public void removeClaim(Claim claim) {
        this.registeredClaims.remove(claim.getOwner().getUniqueId());
    }

    public Collection<Claim> getRegisteredClaims() {
        return registeredClaims.values();
    }
}
