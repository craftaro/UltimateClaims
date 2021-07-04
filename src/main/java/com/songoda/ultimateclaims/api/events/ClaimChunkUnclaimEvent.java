package com.songoda.ultimateclaims.api.events;

import com.songoda.ultimateclaims.claim.Claim;
import org.bukkit.Chunk;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a chunk is unclaimed by a claim.
 */
public class ClaimChunkUnclaimEvent extends ClaimEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Chunk chunk;
    private boolean cancel = false;

    public ClaimChunkUnclaimEvent(Claim claim, Chunk chunk) {
        super(claim);
        this.chunk = chunk;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public Chunk getChunk() {
        return chunk;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
