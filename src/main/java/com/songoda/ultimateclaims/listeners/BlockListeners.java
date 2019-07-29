package com.songoda.ultimateclaims.listeners;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimManager;
import com.songoda.ultimateclaims.claim.PowerCell;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimRole;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

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

        ClaimMember member = claim.getMember(event.getPlayer());

        if ((member == null
                ||member.getRole() == ClaimRole.VISITOR && !claim.getVisitorPermissions().canPlace()
                || member.getRole() == ClaimRole.MEMBER && !claim.getMemberPermissions().canPlace())
                && !event.getPlayer().hasPermission("ultimateclaims.bypass")) {
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

        ClaimMember member = claim.getMember(event.getPlayer());

        if ((member == null
                || member.getRole() == ClaimRole.VISITOR && !claim.getVisitorPermissions().canBreak()
                || member.getRole() == ClaimRole.MEMBER && !claim.getMemberPermissions().canBreak())
                && !event.getPlayer().hasPermission("ultimateclaims.bypass")) {
            plugin.getLocale().getMessage("event.general.nopermission").sendPrefixedMessage(event.getPlayer());
            event.setCancelled(true);
            return;
        }

        if (powerCell.hasLocation() && powerCell.getLocation().equals(block.getLocation())) {
            if (member.getRole() == ClaimRole.OWNER || event.getPlayer().hasPermission("ultimateclaims.bypass")) {
                powerCell.destroy();
            } else {
                plugin.getLocale().getMessage("event.general.nopermission").sendPrefixedMessage(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }
}