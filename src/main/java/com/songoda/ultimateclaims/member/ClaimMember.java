package com.songoda.ultimateclaims.member;

import com.songoda.ultimateclaims.claim.Claim;

import java.util.UUID;

public class ClaimMember {

    private final Claim claim;
    private final UUID uuid;
    private ClaimRole role;
    private boolean isPresent = false;
    private long playTime;
    private long memberSince = System.currentTimeMillis();;

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
}
