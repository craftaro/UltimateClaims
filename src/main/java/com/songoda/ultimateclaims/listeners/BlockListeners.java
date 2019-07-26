package com.songoda.ultimateclaims.listeners;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimManager;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.utils.Methods;
import org.bukkit.Chunk;
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

        Chunk chunk = event.getBlock().getChunk();

        if (!claimManager.hasClaim(chunk)) return;

        Claim claim = claimManager.getClaim(chunk);

        ClaimMember member = claim.getMember(event.getPlayer());

        if (member == null) {
            event.getPlayer().sendMessage("nope cant place.");
            event.setCancelled(true);
            return;
        }

        if (member.getRole() == ClaimRole.OWNER) return;
        else if (member.getRole() == ClaimRole.MEMBER
                && claim.getMemberPermissions().canPlace()) return;
        else if (member.getRole() == ClaimRole.VISITOR
                && claim.getMemberPermissions().canPlace()) return;

        event.getPlayer().sendMessage("nope cant build.");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        ClaimManager claimManager = UltimateClaims.getInstance().getClaimManager();

        Chunk chunk = event.getBlock().getChunk();

        if (!claimManager.hasClaim(chunk)) return;

        Claim claim = claimManager.getClaim(chunk);

        ClaimMember member = claim.getMember(event.getPlayer());

        if (member == null) {
            event.getPlayer().sendMessage("nope cant build.");
            event.setCancelled(true);
            return;
        }

        if (member.getRole() == ClaimRole.OWNER) return;
        else if (member.getRole() == ClaimRole.MEMBER
                && claim.getMemberPermissions().canBreak()) return;
        else if (member.getRole() == ClaimRole.VISITOR
                && claim.getMemberPermissions().canBreak()) return;

        event.getPlayer().sendMessage("nope cant build.");
        event.setCancelled(true);
    }
}