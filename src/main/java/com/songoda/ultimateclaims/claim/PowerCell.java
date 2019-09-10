package com.songoda.ultimateclaims.claim;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.hooks.HologramManager;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.gui.PowerCellGui;
import com.songoda.ultimateclaims.settings.Setting;
import com.songoda.ultimateclaims.utils.Methods;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PowerCell {

    protected final Claim claim;
    protected final UltimateClaims plugin = UltimateClaims.getInstance();

    protected Location location = null;

    protected List<ItemStack> items = new ArrayList<>();

    protected int currentPower = Setting.STARTING_POWER.getInt();

    protected double economyBalance = 0;
    protected PowerCellGui opened = null;

    public PowerCell(Claim claim) {
        this.claim = claim;
    }

    public int tick() {

        boolean loaded = false;
        if (location != null && location.getWorld() != null) {
            int x = location.getBlockX() >> 4;
            int z = location.getBlockZ() >> 4;

            loaded = location.getWorld().isChunkLoaded(x, z);
        }

        if (this.currentPower <= 0 && location != null) {
            if(opened != null && opened.isOpen())
                updateItemsFromGui();
            List<String> materials = Setting.ITEM_VALUES.getStringList();
            for (String value : materials) {
                CompatibleMaterial material = CompatibleMaterial.getMaterial(value.split(":")[0]);
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

                if (loaded && Setting.POWERCELL_HOLOGRAMS.getBoolean())
                    updateHologram();
                return this.currentPower;
            }
            double economyValue = getEconomyValue();
            if (economyBalance >= economyValue) {
                this.economyBalance -= economyValue;
                this.currentPower += 1;
                if (loaded && Setting.POWERCELL_HOLOGRAMS.getBoolean())
                    updateHologram();
                return this.currentPower;
            }
        }
        if (loaded && Setting.POWERCELL_HOLOGRAMS.getBoolean())
            updateHologram();
        return this.currentPower--;
    }

    private int getMaterialAmount(CompatibleMaterial material) {
        return getItems().stream().filter(item -> material.matches(item))
                .map((item) -> item.getAmount())
                .mapToInt(Integer::intValue).sum();
    }

    private void removeOneMaterial(CompatibleMaterial material) {
        if(opened != null && opened.isOpen())
            updateItemsFromGui();
        for (ItemStack item : getItems()) {
            if(material.matches(item)) {
                item.setAmount(item.getAmount() - 1);

                if (item.getAmount() <= 0)
                    this.items.remove(item);
                updateGuiInventory();
                return;
            }
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
            updateGuiInventory();
            rejects.stream().filter(item -> item.getType() != Material.AIR)
                    .forEach(item -> location.getWorld().dropItemNaturally(location, item));
        }
    }

    public void updateGuiInventory() {
        if (opened != null) {
            opened.updateGuiInventory(items);
        }
    }

    public void updateItemsFromGui() {
        if (opened == null) return;
        items.clear();
        for (int i = 10; i < 44; i++) {
            if (i == 17
                    || i == 18
                    || i == 26
                    || i == 27
                    || i == 35
                    || i == 36) continue;
            ItemStack item = opened.getItem(i);
            if(item != null && item.getType() != Material.AIR)
                addItem(item);
        }
    }

    public void updateHologram() {
        if (location != null) {
            if (getTotalPower() > 1) {
                HologramManager.updateHologram(location, plugin.getLocale().getMessage("general.claim.powercell")
                        .processPlaceholder("time", Methods.makeReadable(getTotalPower() * 60 * 1000))
                        .getMessage());
            } else {
                HologramManager.updateHologram(location, plugin.getLocale().getMessage("general.claim.powercell.low")
                        .processPlaceholder("time", Methods.makeReadable((getTotalPower() + Setting.MINIMUM_POWER.getInt()) * 60 * 1000))
                        .getMessage());
            }
        }
    }

    public void removeHologram() {
        if (location != null) {
            HologramManager.removeHologram(location);
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
        if(opened != null && opened.isOpen())
            updateItemsFromGui();
        double total = 0;
        List<String> materials = Setting.ITEM_VALUES.getStringList();
        for (String value : materials) {
            String parts[] = value.split(":");
            CompatibleMaterial material;
            if(parts.length == 2 && (material = CompatibleMaterial.getMaterial(parts[0].trim())) != null) {
                total += getMaterialAmount(material) * (Double.parseDouble(parts[1].trim()) / claim.getClaimSize());
            }
        }
        return (int) total;
    }

    public double getEconomyBalance() {
        return this.economyBalance;
    }

    public double getEconomyPower() {
        return economyBalance / getEconomyValue();
    }

    private double getItemValue(CompatibleMaterial material) {
        List<String> materials = Setting.ITEM_VALUES.getStringList();
        for (String value : materials) {
            String parts[] = value.split(":");
            if(parts.length == 2 && CompatibleMaterial.getMaterial(parts[0].trim()) == material)
                return Double.parseDouble(parts[1].trim()) / claim.getClaimSize();
        }
        return 0;
    }

    public double getEconomyValue() {
        return Setting.ECONOMY_VALUE.getDouble() * claim.getClaimSize();
    }

    public List<ItemStack> getItems() {
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

    public PowerCellGui getGui() {
        return opened != null ? opened : (opened = new PowerCellGui(this.claim));
    }

    public void destroy() {
        if (location != null) {
            getItems().stream().filter(item -> item != null)
                    .forEach(item -> location.getWorld().dropItemNaturally(location, item));
            removeHologram();
        }
        this.items.clear();
        if (opened != null)
            opened.exit();
        this.opened = null;
        this.clearItems();
        this.location = null;
    }
}
