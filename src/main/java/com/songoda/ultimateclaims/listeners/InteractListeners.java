package com.songoda.ultimateclaims.listeners;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimManager;
import com.songoda.ultimateclaims.gui.GUIPowerCell;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimRole;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListeners implements Listener {

    private UltimateClaims plugin;

    public InteractListeners(UltimateClaims plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        ClaimManager claimManager = UltimateClaims.getInstance().getClaimManager();

        Chunk chunk = event.getClickedBlock().getChunk();

        if (!claimManager.hasClaim(chunk)) return;

        Claim claim = claimManager.getClaim(chunk);

        ClaimMember member = claim.getMember(event.getPlayer());

        if (member == null) {
            plugin.getLocale().getMessage("event.general.nopermission").sendPrefixedMessage(event.getPlayer());
            event.setCancelled(true);
            return;
        }
        if (claim.getPowerCell().hasLocation()
                && claim.getPowerCell().getLocation().equals(event.getClickedBlock().getLocation())
                && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (member.getRole() == ClaimRole.OWNER) {
                new GUIPowerCell(event.getPlayer(), claim);
            } else {
                plugin.getLocale().getMessage("event.powercell.failopen").sendPrefixedMessage(event.getPlayer());
            }
            event.setCancelled(true);
        }

        if (member.getRole() == ClaimRole.OWNER) {
            return;
        } else if (member.getRole() == ClaimRole.MEMBER
                && claim.getMemberPermissions().canInteract()) return;
        else if (member.getRole() == ClaimRole.VISITOR
                && claim.getMemberPermissions().canInteract()) return;

        plugin.getLocale().getMessage("event.general.nopermission").sendPrefixedMessage(event.getPlayer());
        event.setCancelled(true);
    }
}