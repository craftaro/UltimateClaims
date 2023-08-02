package com.craftaro.ultimateclaims.listeners;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.ultimateclaims.settings.Settings;
import com.craftaro.core.compatibility.CompatibleParticleHandler;
import com.craftaro.ultimateclaims.UltimateClaims;
import com.craftaro.ultimateclaims.claim.Claim;
import com.craftaro.ultimateclaims.claim.ClaimManager;
import com.craftaro.ultimateclaims.items.PowerCellItem;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class InventoryListeners implements Listener {

    private final UltimateClaims plugin;

    public InventoryListeners(UltimateClaims plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        ClaimManager claimManager = plugin.getClaimManager();
        Player player = (Player) event.getPlayer();

        if (!(event.getInventory().getHolder() instanceof Chest)) return;

        Chest chest = (Chest) event.getInventory().getHolder();

        if (!claimManager.hasClaim(player)
                || chest.getLocation() == null) return;

        Chunk chunk = chest.getLocation().getChunk();

        if (!claimManager.hasClaim(chunk)) return;

        Claim claim = claimManager.getClaim(chunk);

        if (!claim.getOwner().getUniqueId().equals(player.getUniqueId())
                || claim.getPowerCell().hasLocation()) return;

        Map<Integer, PowerCellItem> recipe = plugin.getItemManager().getRecipe();

        boolean failed = false;
        for (int i = 0; i < 27; i++) {
            PowerCellItem item = recipe.get(i);
            if (item == null) continue;
            if (!item.isSimilar(event.getInventory().getItem(i))) {
                failed = true;
                break;
            }
        }

        if (failed) {
            return;
        }

        for (ItemStack item : event.getInventory().getContents()) {
            if (item == null) continue;
            claim.getPowerCell().addItem(item);
        }
        event.getInventory().clear();
        Location location = chest.getLocation();
        claim.getPowerCell().setLocation(location.clone());

        plugin.getDataHelper().updateClaim(claim);

        if (Settings.POWERCELL_HOLOGRAMS.getBoolean())
            claim.getPowerCell().createHologram();

        if (plugin.getDynmapManager() != null)
            plugin.getDynmapManager().refresh();

        float xx = (float) (0 + (Math.random() * 1));
        float yy = (float) (0 + (Math.random() * 2));
        float zz = (float) (0 + (Math.random() * 1));

        CompatibleParticleHandler.spawnParticles(CompatibleParticleHandler.ParticleType.LAVA, location.add(.5, .5, .5), 25, xx, yy, zz);
        player.playSound(location, XSound.ENTITY_BLAZE_DEATH.parseSound(), 1F, .4F);
        player.playSound(location, XSound.ENTITY_PLAYER_LEVELUP.parseSound(), 1F, .1F);

        plugin.getLocale().getMessage("event.powercell.success").sendPrefixedMessage(player);
    }
}
