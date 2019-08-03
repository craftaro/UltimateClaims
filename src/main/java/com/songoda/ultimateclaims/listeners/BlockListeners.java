package com.songoda.ultimateclaims.listeners;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimManager;
import com.songoda.ultimateclaims.claim.PowerCell;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimPerm;
import com.songoda.ultimateclaims.member.ClaimRole;
import java.util.List;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;

public class BlockListeners implements Listener {

    private UltimateClaims plugin;

    public BlockListeners(UltimateClaims plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        ClaimManager claimManager = UltimateClaims.getInstance().getClaimManager();

        Block block = event.getBlock();

        Chunk chunk = block.getChunk();

        if (!claimManager.hasClaim(chunk)) return;

        Claim claim = claimManager.getClaim(chunk);

        if (!claim.playerHasPerms(event.getPlayer(), ClaimPerm.PLACE)) {
            plugin.getLocale().getMessage("event.general.nopermission").sendPrefixedMessage(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        ClaimManager claimManager = UltimateClaims.getInstance().getClaimManager();

        Block block = event.getBlock();

        Chunk chunk = event.getBlock().getChunk();

        if (!claimManager.hasClaim(chunk)) return;

        Claim claim = claimManager.getClaim(chunk);
        PowerCell powerCell = claim.getPowerCell();


        if (!claim.playerHasPerms(event.getPlayer(), ClaimPerm.BREAK)) {
            plugin.getLocale().getMessage("event.general.nopermission").sendPrefixedMessage(event.getPlayer());
            event.setCancelled(true);
            return;
        }

        ClaimMember member = claim.getMember(event.getPlayer());


        if (powerCell.hasLocation() && powerCell.getLocation().equals(block.getLocation())) {
            if ((member != null && member.getRole() == ClaimRole.OWNER) || event.getPlayer().hasPermission("ultimateclaims.bypass")) {
                powerCell.destroy();
            } else {
                plugin.getLocale().getMessage("event.general.nopermission").sendPrefixedMessage(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void ignite(BlockIgniteEvent event) {
        ClaimManager claimManager = plugin.getClaimManager();

        if (claimManager.hasClaim(event.getBlock().getChunk())) {
            Claim claim = claimManager.getClaim(event.getBlock().getChunk());
            if (!claim.getClaimSettings().isFireSpread()
                    && event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void decay(LeavesDecayEvent event) {
        ClaimManager claimManager = plugin.getClaimManager();

        if (claimManager.hasClaim(event.getBlock().getChunk())) {
            Claim claim = claimManager.getClaim(event.getBlock().getChunk());
            if (!claim.getClaimSettings().isLeafDecay()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockFromToEventMonitor(BlockFromToEvent event) {
        // prevent water/lava/egg griefs
        ClaimManager claimManager = plugin.getClaimManager();
        Claim fromClaim = claimManager.getClaim(event.getBlock().getChunk());
        Claim toClaim = claimManager.getClaim(event.getToBlock().getChunk());
        // if we're moving across a claim boundary, cancel the event
        if (fromClaim != null && toClaim != null) {
            if(!fromClaim.equals(toClaim)) {
                event.setCancelled(true);
            }
        } else if(toClaim != null) {
            // moving from unclaimed to a claim
            event.setCancelled(true);
        }
	}

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        // don't push into a protected region
        pistonCheck(event, event.getBlocks());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        // don't pull from a protected region
        pistonCheck(event, event.getBlocks());
    }

    void pistonCheck(BlockPistonEvent event, List<Block> blocks) {
        ClaimManager claimManager = plugin.getClaimManager();
        Block piston = event.getBlock();
        final BlockFace dir = event.getDirection();
        final int chunkX = piston.getX() >> 4, chunkZ = piston.getZ() >> 4;
        Claim fromClaim = claimManager.getClaim(piston.getChunk());
        for (Block block : blocks) {
            // only check if this block is in a different chunk, or going into another chunk
            if (block.getX() >> 4 != chunkX || block.getZ() >> 4 != chunkZ) {
                Claim toClaim = claimManager.getClaim(block.getChunk());
                // if we're moving across a claim boundary, cancel the event
                if (fromClaim != null && toClaim != null) {
                    if(!fromClaim.equals(toClaim)) {
                        // different claims!
                        event.setCancelled(true);
                        return;
                    }
                } else if (toClaim != null) {
                    // trying to alter another claim
                    event.setCancelled(true);
                    return;
                }
            } else if ((block.getX() + dir.getModX()) >> 4 != chunkX || (block.getZ() + dir.getModZ()) >> 4 != chunkZ) {
                Claim toClaim = claimManager.getClaim(block.getRelative(dir).getChunk());
                // if we're moving across a claim boundary, cancel the event
                if (fromClaim != null && toClaim != null) {
                    if(!fromClaim.equals(toClaim)) {
                        // different claims!
                        event.setCancelled(true);
                        return;
                    }
                } else if (toClaim != null) {
                    // trying to alter another claim
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}