package com.craftaro.ultimateclaims.api.events;

import com.craftaro.ultimateclaims.claim.Claim;
import org.bukkit.event.Event;

public abstract class ClaimEvent extends Event {

    protected Claim claim;

    public ClaimEvent(Claim claim) {
        this.claim = claim;
    }

    /**
     * @return claim that fired the event
     */
    public Claim getClaim() {
        return claim;
    }
}
