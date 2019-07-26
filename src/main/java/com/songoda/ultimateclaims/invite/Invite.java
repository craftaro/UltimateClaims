package com.songoda.ultimateclaims.invite;

import com.songoda.ultimateclaims.claim.Claim;

import java.util.UUID;

public class Invite {

    private final UUID inviter;
    private final UUID invited;

    private final Claim claim;

    private final long created;

    private boolean accepted = false;

    public Invite(UUID inviter, UUID invited, Claim claim) {
        this.inviter = inviter;
        this.invited = invited;
        this.claim = claim;
        this.created = System.currentTimeMillis();
    }

    public UUID getInviter() {
        return inviter;
    }

    public UUID getInvited() {
        return invited;
    }

    public Claim getClaim() {
        return claim;
    }

    public long getCreated() {
        return created;
    }

    public void accepted() {
        this.accepted = true;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
