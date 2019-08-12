package com.songoda.ultimateclaims.listeners;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimManager;
import com.songoda.ultimateclaims.claim.PowerCell;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimPerm;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.tasks.VisualizeTask;
import com.songoda.ultimateclaims.utils.settings.Setting;
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
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EntityListeners implements Listener {

    UltimateClaims plugin;

    public EntityListeners(UltimateClaims plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        // update cached username on login
        final Player player = event.getPlayer();
        plugin.getClaimManager().getRegisteredClaims().stream()
                .map(claim -> claim.getMember(player))
                .filter(member -> member != null)
                .forEach(member -> member.setName(player.getName()));
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        VisualizeTask.removePlayer(event.getPlayer());
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
            if (!claim.getClaimSettings().isMobGriefingAllowed()) {
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

        // Who is responsible for this?
        Entity entity = event.getEntity();
        if(entity instanceof Projectile && ((Projectile) entity).getShooter() instanceof Entity) {
            entity = (Entity) ((Projectile) entity).getShooter();
        }

        // Does this concern us?
        for (Block block : new ArrayList<>(event.blockList())) {
            if (!claimManager.hasClaim(block.getChunk()))
                continue; // nope - you're not important

            // Pay special attention to mobs
            switch (entity.getType()) {
                case CREEPER:
                case GHAST:
                case FIREBALL:
                case WITHER:
                    // For explosions caused by mobs, check if allowed
                    Claim claim = claimManager.getClaim(block.getChunk());
                    PowerCell powerCell = claim.getPowerCell();
                    if (!claim.getClaimSettings().isMobGriefingAllowed()
                            || (powerCell.hasLocation() && powerCell.getLocation().equals(block.getLocation()))) {
                        event.blockList().remove(block);
                    }
                    break;
                default:
                    // Cancel block damage from all other explosions
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
                if(Setting.CLAIMS_BOSSBAR.getBoolean()) {
                    claim.getVisitorBossBar().removePlayer(player);
                    claim.getMemberBossBar().removePlayer(player);
                } else {
                    plugin.getLocale().getMessage("event.claim.exit")
                            .processPlaceholder("claim", claim.getName())
                            .sendTitle(player);
                }
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

                if(Setting.CLAIMS_BOSSBAR.getBoolean()) {
                    if(member == null || member.getRole() == ClaimRole.VISITOR) {
                        claim.getVisitorBossBar().addPlayer(player);
                    } else {
                        claim.getMemberBossBar().addPlayer(player);
                    }
                } else {
                    plugin.getLocale().getMessage("event.claim.enter")
                            .processPlaceholder("claim", claim.getName())
                            .sendTitle(player);
                }
            }
        }
        return false;
    }
}
