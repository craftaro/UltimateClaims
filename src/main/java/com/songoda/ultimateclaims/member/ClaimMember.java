package com.songoda.ultimateclaims.member;

import java.util.UUID;

public class ClaimMember {

    private final UUID uuid;
    private final ClaimRole role;
    private boolean isPresent = false;

    public ClaimMember(UUID uuid, ClaimRole role) {
        this.uuid = uuid;
        this.role = role;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public ClaimRole getRole() {
        return role;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        this.isPresent = present;
    }
}
