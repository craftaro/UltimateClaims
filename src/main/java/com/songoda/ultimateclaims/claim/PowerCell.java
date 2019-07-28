package com.songoda.ultimateclaims.claim;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.utils.settings.Setting;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PowerCell {

    private Location location;

    private List<ItemStack> items = new ArrayList<>();

    private int currentPower = 10;

    private double economyBalance = 0;
    private Inventory opened = null;

    public int tick(Claim claim) {
        UltimateClaims plugin = UltimateClaims.getInstance();
        if (this.currentPower <= 0) {
            List<String> materials = Setting.ITEM_VALUES.getStringList();
            for (String value : materials) {
                Material material = Material.valueOf(value.split(":")[0]);
                if (getMaterialAmount(material) != 0) {
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

    private int getMaterialAmount(Material material) {
        int amount = 0;
        for (ItemStack item : getItems()) {
            if (item.getType() != material) continue;
            amount += item.getAmount();
        }
        return amount;
    }

    private void removeOneMaterial(Material material) {
        for (ItemStack item : getItems()) {
            if (item.getType() != material) continue;

            item.setAmount(item.getAmount() - 1);

            if (item.getAmount() <= 0)
                this.items.remove(item);
            updateInventory(opened);
            return;
        }
    }

    public void updateInventory(Inventory opened) {
        if (opened == null) return;
        int j = 0;
        for (int i = 10; i < 44; i++) {
            if (i == 17
                    || i == 18
                    || i == 26
                    || i == 27
                    || i == 35
                    || i == 36) continue;
            if (items.size() <= j) {
                opened.setItem(i, null);
                continue;
            }
            opened.setItem(i, this.items.get(j));
            j++;
        }
    }

    public void updateItems() {
        items.clear();
        for (int i = 10; i < 44; i++) {
            if (i == 17
                    || i == 18
                    || i == 26
                    || i == 27
                    || i == 35
                    || i == 36
                    || opened.getItem(i) == null) continue;
            addItem(opened.getItem(i));
        }
    }

    public int getCurrentPower() {
        return currentPower;
    }

    public int getTotalPower() {
        return getItemPower() + getEconomyPower() + currentPower;
    }

    public int getItemPower() {
        int total = 0;
        List<String> materials = Setting.ITEM_VALUES.getStringList();
        for (String value : materials) {
            Material material = Material.valueOf(value.split(":")[0]);
            if (getMaterialAmount(material) != 0)
                total += getMaterialAmount(material) * Integer.parseInt(value.split(":")[1]);
        }
        return total;
    }

    public int getEconomyPower() {
        return (int) Math.floor(economyBalance / Setting.ECONOMY_VALUE.getDouble());
    }

    public List<ItemStack> getItems() {
        if (opened != null)
            updateItems();
        return new ArrayList<>(this.items);
    }

    public void addItem(ItemStack item) {
        this.items.add(item);
    }

    public void addEconomy(double amount) {
        this.economyBalance += amount;
    }

    public void clearItems() {
        this.items.clear();
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

    public Inventory getOpened() {
        return opened;
    }

    public void setOpened(Inventory opened) {
        this.opened = opened;
    }

    public void destroy() {
        if (location != null) {
            for (ItemStack item : getItems()) {
                if (item == null) continue;
                location.getWorld().dropItemNaturally(location, item);
            }
            if (UltimateClaims.getInstance().getHologram() != null)
                UltimateClaims.getInstance().getHologram().remove(this);
        }
        if (opened != null)
            for (HumanEntity entity : opened.getViewers())
                entity.closeInventory();

        this.clearItems();
        this.location = null;
    }
}
