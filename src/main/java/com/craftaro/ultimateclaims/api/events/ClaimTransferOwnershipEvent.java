package com.craftaro.ultimateclaims.api.events;

import com.craftaro.ultimateclaims.claim.Claim;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a claim's ownership is transferred.
 */
public class ClaimTransferOwnershipEvent extends ClaimEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final OfflinePlayer oldOwner;
    private final OfflinePlayer newOwner;

    public ClaimTransferOwnershipEvent(Claim claim, OfflinePlayer oldOwner, OfflinePlayer newOwner) {
        super(claim);
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public OfflinePlayer getNewOwner() {
        return newOwner;
    }

    public OfflinePlayer getOldOwner() {
        return oldOwner;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}