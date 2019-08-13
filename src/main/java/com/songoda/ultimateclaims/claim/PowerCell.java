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
import java.util.stream.Collectors;

public class PowerCell {

    private final Claim claim;
    private final UltimateClaims plugin = UltimateClaims.getInstance();

    private Location location = null;

    private List<ItemStack> items = new ArrayList<>();

    private int currentPower = Setting.STARTING_POWER.getInt();

    private double economyBalance = 0;
    private Inventory opened = null;

    public PowerCell(Claim claim) {
        this.claim = claim;
    }

    public int tick() {

        boolean loaded = false;
        if (location != null) {
            int x = location.getBlockX() >> 4;
            int z = location.getBlockZ() >> 4;

            loaded = location.getWorld().isChunkLoaded(x, z);
        }

        if (this.currentPower <= 0 && location != null) {
            List<String> materials = Setting.ITEM_VALUES.getStringList();
            for (String value : materials) {
                Material material = Material.valueOf(value.split(":")[0]);
                if (getMaterialAmount(material) == 0) continue;
                double itemValue = getItemValue(material);
                if (itemValue < 1) { // Remove items based on number of claimed chunks
                    int itemsToRemove = (int) Math.ceil(1 / itemValue);
                    for (int i = 0; i < itemsToRemove; i++)
                        this.removeOneMaterial(material);
                    this.currentPower += getItemValue(material) * itemsToRemove;
                } else { // Remove only one item
                    this.removeOneMaterial(material);
                    this.currentPower += getItemValue(material);
                }

                if (loaded && plugin.getHologram() != null)
                    plugin.getHologram().update(this);
                return this.currentPower;
            }
            double economyValue = getEconomyValue();
            if (economyBalance >= economyValue) {
                this.economyBalance -= economyValue;
                this.currentPower += 1;
                if (loaded && plugin.getHologram() != null)
                    plugin.getHologram().update(this);
                return this.currentPower;
            }
        }
        if (loaded && location != null && plugin.getHologram() != null)
            plugin.getHologram().update(this);
        return this.currentPower--;
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

    public void rejectUnusable() {
        if (location == null)
            return;
        // list of all valid materials with positive value
        List<Material> materials = Setting.ITEM_VALUES.getStringList().stream()
                .filter(value -> value.indexOf(':') != -1 && Double.parseDouble(value.split(":")[1]) > 0)
                .map(value -> Material.valueOf(value.split(":")[0]))
                .filter(value -> value != null)
                .collect(Collectors.toList());

        // list of items in the inventory that are worthless and removed from our inventory
        List<ItemStack> rejects = new ArrayList();
        for (int i = items.size() - 1; i >= 0; i--) {
            final ItemStack item = items.get(i);
            if (item != null && !materials.stream().anyMatch(m -> m == item.getType()))
                rejects.add(items.remove(i));
        }

        if(!rejects.isEmpty()) {
            // YEET
            updateInventory(opened);
            rejects.stream().forEach(item -> location.getWorld().dropItemNaturally(location, item));
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

    public void setCurrentPower(int currentPower) {
        this.currentPower = currentPower;
    }

    public void setEconomyBalance(double economyBalance) {
        this.economyBalance = economyBalance;
    }

    public long getTotalPower() {
        return getItemPower() + (long) getEconomyPower() + currentPower;
    }

    public long getItemPower() {
        double total = 0;
        List<String> materials = Setting.ITEM_VALUES.getStringList();
        for (String value : materials) {
            Material material = Material.valueOf(value.split(":")[0]);
            if (getMaterialAmount(material) == 0) continue;

            total += getMaterialAmount(material) * getItemValue(material);
        }
        return (int) total;
    }

    public double getEconomyBalance() {
        return this.economyBalance;
    }

    public double getEconomyPower() {
        return economyBalance / getEconomyValue();
    }

    private double getItemValue(Material material) {
        List<String> materials = Setting.ITEM_VALUES.getStringList();
        for (String value : materials) {
            if (material == Material.valueOf(value.split(":")[0]))
                return Double.parseDouble(value.split(":")[1]) / claim.getClaimSize();
        }
        return 0;
    }

    public double getEconomyValue() {
        return Setting.ECONOMY_VALUE.getDouble() * claim.getClaimSize();
    }

    public List<ItemStack> getItems() {
        if (opened != null)
            updateItems();
        return new ArrayList<>(this.items);
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
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
        return location == null ? null : location.clone();
    }

    public boolean hasLocation() {
        return location != null;
    }

    public void setLocation(Location location) {
        this.location = location;
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
        this.items.clear();
        if (opened != null)
            for (HumanEntity entity : opened.getViewers())
                entity.closeInventory();
        this.opened = null;
        this.clearItems();
        this.location = null;
    }
}
