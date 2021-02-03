package com.songoda.ultimateclaims.api.events;

import com.songoda.ultimateclaims.claim.Claim;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a player is banned from a claim.
 */
public class ClaimPlayerBanEvent extends ClaimEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final Player executor;
    private final OfflinePlayer bannedPlayer;

    public ClaimPlayerBanEvent(Claim claim, Player executor, OfflinePlayer bannedPlayer) {
        super(claim);
        this.executor = executor;
        this.bannedPlayer = bannedPlayer;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public OfflinePlayer getBannedPlayer() {
        return bannedPlayer;
    }

    public Player getExecutor() {
        return executor;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
