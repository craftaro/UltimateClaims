package com.songoda.ultimateclaims.member;

import com.songoda.ultimateclaims.claim.Claim;

import java.util.UUID;

public class ClaimMember {

    private final Claim claim;
    private final UUID uuid;
    private ClaimRole role;
    private boolean isPresent = false;
    private int playTime;
    private int memberSince;

    public ClaimMember(Claim claim, UUID uuid, ClaimRole role) {
        this.claim = claim;
        this.uuid = uuid;
        this.role = role;
    }

    public Claim getClaim() {
        return claim;
    }

    public UUID getUniqueId() {
        return uuid;
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

    public int getPlayTime() {
        return playTime;
    }

    public int getMemberSince() {
        return memberSince;
    }
}
