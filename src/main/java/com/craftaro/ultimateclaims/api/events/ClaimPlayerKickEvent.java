package com.craftaro.ultimateclaims.api.events;

import com.craftaro.ultimateclaims.claim.Claim;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a player gets kicked out of a claim.
 */
public class ClaimPlayerKickEvent extends ClaimEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final OfflinePlayer player;

    public ClaimPlayerKickEvent(Claim claim, OfflinePlayer player) {
        super(claim);
        this.player = player;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
