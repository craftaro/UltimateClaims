package com.songoda.ultimateclaims.listeners;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimManager;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.utils.Methods;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class InteractListeners implements Listener {

    private UltimateClaims plugin;

    public InteractListeners(UltimateClaims plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(BlockPlaceEvent event) {
        ClaimManager claimManager = UltimateClaims.getInstance().getClaimManager();

        Chunk chunk = event.getBlock().getChunk();

        if (!claimManager.hasClaim(chunk)) return;

        Claim claim = claimManager.getClaim(chunk);

        ClaimMember member = claim.getMember(event.getPlayer());

        if (member == null) {
            event.getPlayer().sendMessage("nope cant interact.");
            event.setCancelled(true);
            return;
        }

        if (member.getRole() == ClaimRole.OWNER) return;
        else if (member.getRole() == ClaimRole.MEMBER
                && claim.getMemberPermissions().canInteract()) return;
        else if (member.getRole() == ClaimRole.VISITOR
                && claim.getMemberPermissions().canInteract()) return;

        event.getPlayer().sendMessage("nope cant interact.");
        event.setCancelled(true);
    }
}