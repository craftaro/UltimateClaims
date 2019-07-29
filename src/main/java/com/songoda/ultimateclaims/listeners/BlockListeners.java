package com.songoda.ultimateclaims.listeners;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimManager;
import com.songoda.ultimateclaims.claim.PowerCell;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimPerm;
import com.songoda.ultimateclaims.member.ClaimRole;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
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
}