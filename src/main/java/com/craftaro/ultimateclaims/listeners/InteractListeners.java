package com.craftaro.ultimateclaims.listeners;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimateclaims.UltimateClaims;
import com.craftaro.ultimateclaims.claim.Claim;
import com.craftaro.ultimateclaims.claim.ClaimManager;
import com.craftaro.ultimateclaims.member.ClaimMember;
import com.craftaro.ultimateclaims.member.ClaimPerm;
import com.craftaro.ultimateclaims.member.ClaimRole;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

public class InteractListeners implements Listener {
    private final UltimateClaims plugin;

    public InteractListeners(UltimateClaims plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        ClaimManager claimManager = UltimateClaims.getInstance().getClaimManager();

        Chunk chunk = event.getClickedBlock().getChunk();

        boolean hasClaim = claimManager.hasClaim(chunk);
        if (event.getAction() == Action.PHYSICAL && hasClaim) {
            Claim claim = claimManager.getClaim(chunk);

            boolean canRedstone = isRedstone(event.getClickedBlock()) && claim.playerHasPerms(event.getPlayer(), ClaimPerm.REDSTONE);
            if (canRedstone) {
                return;
            } else if (isRedstone(event.getClickedBlock()) && !claim.playerHasPerms(event.getPlayer(), ClaimPerm.REDSTONE)) {
                this.plugin.getLocale().getMessage("event.general.nopermission").sendPrefixedMessage(event.getPlayer());
                event.setCancelled(true);
                return;
            }

            if (!claim.playerHasPerms(event.getPlayer(), ClaimPerm.PLACE)) {
                this.plugin.getLocale().getMessage("event.general.nopermission").sendPrefixedMessage((Player) event.getPlayer());
                event.setCancelled(true);
            }
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !hasClaim) {
            return;
        }

        Claim claim = claimManager.getClaim(chunk);

        boolean canDoors = isDoor(event.getClickedBlock()) && claim.playerHasPerms(event.getPlayer(), ClaimPerm.DOORS);
        boolean canRedstone = isRedstone(event.getClickedBlock()) && claim.playerHasPerms(event.getPlayer(), ClaimPerm.REDSTONE);

        if (canRedstone || canDoors) {
            return;
        } else if (isRedstone(event.getClickedBlock()) && !claim.playerHasPerms(event.getPlayer(), ClaimPerm.REDSTONE)
                || isDoor(event.getClickedBlock()) && !claim.playerHasPerms(event.getPlayer(), ClaimPerm.DOORS)) {
            this.plugin.getLocale().getMessage("event.general.nopermission").sendPrefixedMessage(event.getPlayer());
            event.setCancelled(true);
            return;
        }

        ClaimMember member = claim.getMember(event.getPlayer());

        if (claim.getPowerCell().hasLocation()
                && claim.getPowerCell().getLocation().equals(event.getClickedBlock().getLocation())
                && event.getAction() == Action.RIGHT_CLICK_BLOCK
                && !event.getPlayer().isSneaking()) {

            // Make sure all items in the powercell are stacked.
            claim.getPowerCell().stackItems();
            if (member != null && member.getRole() != ClaimRole.VISITOR || event.getPlayer().hasPermission("ultimateclaims.powercell.view")) {
                this.plugin.getGuiManager().showGUI(event.getPlayer(), claim.getPowerCell().getGui(event.getPlayer()));
            } else {
                this.plugin.getLocale().getMessage("event.powercell.failopen").sendPrefixedMessage(event.getPlayer());
            }
            event.setCancelled(true);
            return;
        }

        if (!claim.playerHasPerms(event.getPlayer(), ClaimPerm.INTERACT)) {
            this.plugin.getLocale().getMessage("event.general.nopermission").sendPrefixedMessage(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Chunk chunk = event.getBlockClicked().getRelative(event.getBlockFace()).getChunk();

        onBucket(chunk, event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketFillEvent event) {
        Chunk chunk = event.getBlockClicked().getRelative(event.getBlockFace()).getChunk();

        onBucket(chunk, event.getPlayer(), event);
    }

    private void onBucket(Chunk chunk, Player player, Cancellable event) {
        ClaimManager claimManager = this.plugin.getClaimManager();

        if (!claimManager.hasClaim(chunk)) {
            return;
        }

        Claim claim = claimManager.getClaim(chunk);

        if (!claim.playerHasPerms(player, ClaimPerm.PLACE)) {
            this.plugin.getLocale().getMessage("event.general.nopermission").sendPrefixedMessage(player);
            event.setCancelled(true);
        }
    }

    private boolean isDoor(Block block) {
        if (block == null) {
            return false;
        }

        switch (block.getType().name()) {
            case "DARK_OAK_DOOR":
            case "ACACIA_DOOR":
            case "BIRCH_DOOR":
            case "JUNGLE_DOOR":
            case "OAK_DOOR":
            case "SPRUCE_DOOR":
            case "ACACIA_TRAPDOOR":
            case "BIRCH_TRAPDOOR":
            case "DARK_OAK_TRAPDOOR":
            case "IRON_TRAPDOOR":
            case "JUNGLE_TRAPDOOR":
            case "OAK_TRAPDOOR":
            case "SPRUCE_TRAPDOOR":
            case "OAK_FENCE_GATE":
            case "ACACIA_FENCE_GATE":
            case "BIRCH_FENCE_GATE":
            case "DARK_OAK_FENCE_GATE":
            case "JUNGLE_FENCE_GATE":
            case "SPRUCE_FENCE_GATE":
            case "WOODEN_DOOR":
            case "WOOD_DOOR":
            case "TRAP_DOOR":
            case "FENCE_GATE":
                return true;
            default:
                return false;
        }
    }

    private boolean isRedstone(Block block) {
        if (block == null) {
            return false;
        }

        Optional<XMaterial> material = CompatibleMaterial.getMaterial(block.getType());
        if (!material.isPresent()) {
            return false;
        }
        switch (material.get()) {
            case LEVER:
            case BIRCH_BUTTON:
            case ACACIA_BUTTON:
            case DARK_OAK_BUTTON:
            case JUNGLE_BUTTON:
            case OAK_BUTTON:
            case SPRUCE_BUTTON:
            case STONE_BUTTON:
            case ACACIA_PRESSURE_PLATE:
            case BIRCH_PRESSURE_PLATE:
            case DARK_OAK_PRESSURE_PLATE:
            case HEAVY_WEIGHTED_PRESSURE_PLATE:
            case JUNGLE_PRESSURE_PLATE:
            case LIGHT_WEIGHTED_PRESSURE_PLATE:
            case OAK_PRESSURE_PLATE:
            case SPRUCE_PRESSURE_PLATE:
            case STONE_PRESSURE_PLATE:
                return true;
            default:
                return false;
        }
    }
}
