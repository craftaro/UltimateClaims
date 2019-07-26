package com.songoda.ultimateclaims.invite;

import com.songoda.ultimateclaims.claim.Claim;

import java.util.UUID;

public class Invite {

    private final UUID inviter;
    private final UUID invited;

    private final Claim claim;

    private final long created;

    public Invite(UUID inviter, UUID invited, Claim claim, long created) {
        this.inviter = inviter;
        this.invited = invited;
        this.claim = claim;
        this.created = created;
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
}
