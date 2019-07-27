package com.songoda.ultimateclaims.claim;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.utils.settings.Setting;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PowerCell {

    private Location location;

    private Inventory inventory;

    private int currentPower = 10;

    private double economyBalance = 0;

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

    public int tick(Claim claim) {
        UltimateClaims plugin = UltimateClaims.getInstance();
        if (this.currentPower <= 0) {
            List<String> materials = Setting.ITEM_VALUES.getStringList();
            for (String value : materials) {
                Material material = Material.valueOf(value.split(":")[0]);
                if (getItems().stream().anyMatch(item -> item.getType() == material)) {
                    this.removeOneMaterial(material);
                    this.currentPower += Integer.parseInt(value.split(":")[1]);
                    if (plugin.getHologram() != null)
                        plugin.getHologram().update(this);
                    return this.currentPower;
                }
            }
            double economyValue = Setting.ECONOMY_VALUE.getDouble();
            if (economyBalance >= economyValue) {
                this.economyBalance -= economyValue;
                this.currentPower += 1;
            }

        }
        if (location != null && plugin.getHologram() != null)
            plugin.getHologram().update(this);
        return this.currentPower -= claim == null ? 0 : claim.getClaimedChunks().size();
    }

    private List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 10; i < 44; i++) {
            ItemStack item = this.inventory.getItem(i);
            if (item == null
                    || (item.hasItemMeta() && item.getItemMeta().hasDisplayName())) continue;
            items.add(item);
        }
        return items;
    }

    private int getMaterialAmount(Material material) {
        int amount = 0;
        for (ItemStack item : getItems()) {
            if (item.getType() != material) continue;
            amount = item.getAmount();
        }
        return amount;
    }

    private void removeOneMaterial(Material material) {
        for (int i = 10; i < 44; i++) {
            ItemStack item = this.inventory.getItem(i);
            if (item == null || item.getType() != material) continue;

            item.setAmount(item.getAmount() - 1);

            if (item.getAmount() <= 0)
                inventory.setItem(i, null);
            return;
        }
    }

    public int getCurrentPower() {
        return currentPower;
    }

    public int getTotalPower() {
        return getItemPower() + getEconomyPower();
    }

    public int getItemPower() {
        int total = currentPower;
        List<String> materials = Setting.ITEM_VALUES.getStringList();
        for (String value : materials) {
            Material material = Material.valueOf(value.split(":")[0]);
            if (getItems().stream().anyMatch(item -> item.getType() == material)) {
                total += getMaterialAmount(material) + Integer.parseInt(value.split(":")[1]);
            }
        }
        return total;
    }

    public int getEconomyPower() {
        return (int) Math.floor(economyBalance / Setting.ECONOMY_VALUE.getDouble());
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
        tick(null);
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
        this.inventory.clear();
        this.location = null;
    }
}
