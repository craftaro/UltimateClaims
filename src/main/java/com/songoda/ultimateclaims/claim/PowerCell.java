package com.songoda.ultimateclaims.claim;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.utils.settings.Setting;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PowerCell {

    private Location location;

    private Inventory inventory;

    private int currentPower = 10;

    public PowerCell() {
        this.inventory = Bukkit.createInventory(null, 54, "test");
    }

    public PowerCell(List<ItemStack> items) {
        this.inventory = Bukkit.createInventory(null, 54, "test");
        int i = 10;
        for (ItemStack item : items) {
            inventory.setItem(i++, item);
        }
    }

    public int tick() {
        UltimateClaims plugin = UltimateClaims.getInstance();
        if (this.currentPower <= 0) {
            List<String> materials = Setting.ITEM_VALUES.getStringList();
            for (String value : materials) {
                Material material = Material.valueOf(value.split(":")[0]);
                if (inventory.contains(material)) {
                    inventory.removeItem(new ItemStack(material, 1));
                    this.currentPower += Integer.parseInt(value.split(":")[1]);
                    if (plugin.getHologram() != null)
                        plugin.getHologram().update(this);
                    return this.currentPower;
                }
            }
        }
        if (location != null && plugin.getHologram() != null)
            plugin.getHologram().update(this);
        return this.currentPower--;
    }

    public int getCurrentPower() {
        return currentPower;
    }

    public int getTotalPower() {
        int total = currentPower;
        List<String> materials = Setting.ITEM_VALUES.getStringList();
        for (String value : materials) {
            Material material = Material.valueOf(value.split(":")[0]);
            if (inventory.contains(material)) {
                total += Integer.parseInt(value.split(":")[1]);
            }
        }
        return total;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Location getLocation() {
        return location.clone();
    }

    public boolean hasLocation() {
        return location != null;
    }

    public void setLocation(Location location) {
        this.location = location;
        tick();
    }

    public void destroy() {
        if (location != null) {
            for (ItemStack item : inventory.getContents()) {
                if (item == null) continue;
                location.getWorld().dropItemNaturally(location, item);
            }
            if (UltimateClaims.getInstance().getHologram() != null)
                UltimateClaims.getInstance().getHologram().remove(this);
        }
        this.currentPower = 1;
        this.inventory.clear();
        this.location = null;
    }
}
