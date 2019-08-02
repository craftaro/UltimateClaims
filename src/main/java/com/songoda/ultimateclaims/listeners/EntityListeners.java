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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.ArrayList;
import org.bukkit.event.block.BlockExplodeEvent;

public class EntityListeners implements Listener {

    UltimateClaims plugin;

    public EntityListeners(UltimateClaims plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        if (playerMove(event.getFrom().getChunk(), event.getTo().getChunk(), event.getPlayer())) {
            if (event.getPlayer().isInsideVehicle())
                event.getPlayer().leaveVehicle();
            event.setCancelled(true);
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(VehicleMoveEvent event) {
        for (Entity entity : event.getVehicle().getPassengers()) {
            if (!(entity instanceof Player)) continue;
            if (playerMove(event.getFrom().getChunk(), event.getTo().getChunk(), (Player) entity)) {
                entity.leaveVehicle();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (playerMove(event.getFrom().getChunk(), event.getTo().getChunk(), event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void ongetIn(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;
        if (playerMove(event.getEntered().getLocation().getChunk(),
                event.getVehicle().getLocation().getChunk(), (Player) event.getEntered()))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(EntitySpawnEvent event) {
        ClaimManager claimManager = plugin.getClaimManager();

        if (claimManager.hasClaim(event.getLocation().getChunk())) {
            Claim claim = claimManager.getClaim(event.getLocation().getChunk());
            if (!claim.getClaimSettings().isHostileMobSpawning() && event.getEntity() instanceof Monster) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChange(EntityChangeBlockEvent event) {
        ClaimManager claimManager = plugin.getClaimManager();
        if (event.getEntity() instanceof Player) return;

        if (claimManager.hasClaim(event.getBlock().getLocation().getChunk())) {
            Claim claim = claimManager.getClaim(event.getBlock().getLocation().getChunk());
            if (!claim.getClaimSettings().isMobGriefing()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        ClaimManager claimManager = plugin.getClaimManager();
        Chunk chunk = event.getEntity().getLocation().getChunk();

        if (claimManager.hasClaim(chunk)) {
            Claim claim = claimManager.getClaim(chunk);
            if (!(event.getEntity() instanceof Player)) {
                if (!claim.playerHasPerms((Player) event.getDamager(), ClaimPerm.MOB_KILLING))
                    event.setCancelled(true);
                return;
            }

            if (!claim.getClaimSettings().isPvp()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlowUp(EntityExplodeEvent event) {
        ClaimManager claimManager = plugin.getClaimManager();
        for (Block block : new ArrayList<>(event.blockList())) {
            if (!claimManager.hasClaim(block.getChunk())) continue;

            if (event.getEntity().getType() == EntityType.CREEPER) {
                Claim claim = claimManager.getClaim(block.getChunk());
                PowerCell powerCell = claim.getPowerCell();
                if (claim.getClaimSettings().isMobGriefing()
                        || (powerCell.hasLocation() && powerCell.getLocation().equals(block.getLocation()))) {
                    event.blockList().remove(block);
                }
            } else {
                event.blockList().remove(block);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        ClaimManager claimManager = plugin.getClaimManager();
        for (Block block : new ArrayList<>(event.blockList())) {
            if (claimManager.hasClaim(block.getChunk())) {
                // todo? setting to allow/disallow these in a claim?
                event.blockList().remove(block);
            }
        }
    }

    private boolean playerMove(Chunk from, Chunk to, Player player) {
        if (from == to) return false;

        ClaimManager claimManager = plugin.getClaimManager();

        if (claimManager.hasClaim(from)) {
            Claim claim = claimManager.getClaim(from);
            if (claimManager.getClaim(to) != claim) {
                ClaimMember member = claim.getMember(player);
                if (member != null) {
                    if (member.getRole() == ClaimRole.VISITOR)
                        claim.removeMember(member);
                    else
                        member.setPresent(false);
                }
                plugin.getLocale().getMessage("event.claim.exit")
                        .processPlaceholder("claim", claim.getName())
                        .sendTitle(player);
            }
        }

        if (claimManager.hasClaim(to)) {
            Claim claim = claimManager.getClaim(to);
            if (claimManager.getClaim(from) != claim) {
                ClaimMember member = claim.getMember(player);
                if (member == null) {
                    if (claim.isLocked() && !player.hasPermission("ultimateclaims.bypass")) {
                        plugin.getLocale().getMessage("event.claim.locked")
                                .sendTitle(player);
                        return true;
                    }

                    if (!player.hasPermission("ultimateclaims.bypass")) {
                        claim.addMember(player, ClaimRole.VISITOR);
                        member = claim.getMember(player);
                    }
                }
                if (member != null)
                    member.setPresent(true);

                if (member != null && claim.isBanned(member.getUniqueId())) {
                    plugin.getLocale().getMessage("event.claim.locked")
                            .sendTitle(player);
                    return true;
                }

                plugin.getLocale().getMessage("event.claim.enter")
                        .processPlaceholder("claim", claim.getName())
                        .sendTitle(player);
            }
        }
        return false;
    }
}
