package com.songoda.ultimateclaims.listeners;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimManager;
import com.songoda.ultimateclaims.claim.PowerCell;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.utils.Methods;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

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

        Block block = event.getBlock();

        Chunk chunk = event.getBlock().getChunk();

        if (!claimManager.hasClaim(chunk)) return;

        Claim claim = claimManager.getClaim(chunk);
        PowerCell powerCell = claim.getPowerCell();

        ClaimMember member = claim.getMember(event.getPlayer());

        if (member == null) {
            event.getPlayer().sendMessage("nope cant build.");
            event.setCancelled(true);
            return;
        }

        if (powerCell.hasLocation() && powerCell.getLocation().equals(block.getLocation())) {
            if (member.getRole() == ClaimRole.OWNER) {
                powerCell.destroy();
            } else {
                event.getPlayer().sendMessage("no");
                event.setCancelled(true);
            }
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