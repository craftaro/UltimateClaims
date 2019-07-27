package com.songoda.ultimateclaims.listeners;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimManager;
import com.songoda.ultimateclaims.utils.settings.Setting;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryListeners implements Listener {

    private UltimateClaims plugin;

    public InventoryListeners(UltimateClaims plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        ClaimManager claimManager = plugin.getClaimManager();
        Player player = (Player) event.getPlayer();
        if (!claimManager.hasClaim(player)) return;
        Claim claim = claimManager.getClaim(player);
        if (claim.getPowerCell().hasLocation()) return;
        List<String> recipe = Setting.POWERCELL_RECIPE.getStringList();

        int size = 0;
        for (int i = 0; i < 27; i++) {
            if (event.getInventory().getItem(i) == null) continue;
            String line = i + ":" + event.getInventory().getItem(i).getType().name();
            if (!recipe.contains(line)) return;
            size++;
        }
        if (size != recipe.size()) return;

        for (ItemStack item : event.getInventory().getContents()) {
            if (item == null) continue;
            claim.getPowerCell().getInventory().addItem(item);
        }
        event.getInventory().clear();
        Location location = event.getInventory().getLocation();
        claim.getPowerCell().setLocation(location.clone());

        float xx = (float) (0 + (Math.random() * 1));
        float yy = (float) (0 + (Math.random() * 2));
        float zz = (float) (0 + (Math.random() * 1));
        location.getWorld().spawnParticle(Particle.LAVA, location.add(.5,.5,.5), 25, xx, yy, zz, 0);

        player.sendMessage("Powercell set");
    }
}
