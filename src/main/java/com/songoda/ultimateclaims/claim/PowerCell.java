package com.songoda.ultimateclaims.claim;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.hooks.HologramManager;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.gui.PowerCellGui;
import com.songoda.ultimateclaims.settings.Settings;
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

    protected int currentPower = Settings.STARTING_POWER.getInt();

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
            updateItemsFromGui();
            List<String> materials = Settings.ITEM_VALUES.getStringList();
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

                if (loaded && Settings.POWERCELL_HOLOGRAMS.getBoolean())
                    updateHologram();
                return this.currentPower;
            }
            double economyValue = getEconomyValue();
            if (economyBalance >= economyValue) {
                this.economyBalance -= economyValue;
                this.currentPower += 1;
                if (loaded && Settings.POWERCELL_HOLOGRAMS.getBoolean())
                    updateHologram();
                return this.currentPower;
            }
        }
        if (loaded && Settings.POWERCELL_HOLOGRAMS.getBoolean())
            updateHologram();
        stackItems();
        return this.currentPower--;
    }

    private int getMaterialAmount(CompatibleMaterial material) {
        return getItems().stream().filter(material::matches)
                .map(ItemStack::getAmount).mapToInt(Integer::intValue).sum();
    }

    private void removeOneMaterial(CompatibleMaterial material) {
        updateItemsFromGui();
        List<ItemStack> items = getItems();
        for (int i = 0; i < items.size(); i ++) {
            ItemStack item = items.get(i);
            if (material.matches(item)) {
                item.setAmount(item.getAmount() - 1);

                if (item.getAmount() <= 0)
                    this.items.remove(item);
                updateGuiInventory();
                return;
            }
            if (i >= 28) break;
        }
    }

    public void rejectUnusable() {
        if (location == null)
            return;
        // list of all valid materials with positive value
        List<Material> materials = Settings.ITEM_VALUES.getStringList().stream()
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

        if (!rejects.isEmpty()) {
            // YEET
            updateGuiInventory();
            rejects.stream().filter(item -> item.getType() != Material.AIR)
                    .forEach(item -> location.getWorld().dropItemNaturally(location, item));
        }
    }

    public void updateGuiInventory() {
        if (opened != null)
            opened.updateGuiInventory(items);
    }

    public void updateItemsFromGui() {
        updateItemsFromGui(false);
    }

    public void updateItemsFromGui(boolean force) {
        if (!isInventoryOpen()
                && !force) return;
        items.clear();
        for (int i = 10; i < 44; i++) {
            if (i == 17
                    || i == 18
                    || i == 26
                    || i == 27
                    || i == 35
                    || i == 36) continue;
            ItemStack item = opened.getItem(i);
            if (item != null && item.getType() != Material.AIR)
                addItem(item);
        }
    }

    public boolean isInventoryOpen() {
        return opened != null
                && opened.getInventory() != null
                && !opened.getInventory().getViewers().isEmpty();
    }

    public void updateHologram() {
        if (location != null) {
            if (getTotalPower() > 1) {
                HologramManager.updateHologram(location, plugin.getLocale().getMessage("general.claim.powercell")
                        .processPlaceholder("time", Methods.makeReadable(getTotalPower() * 60 * 1000))
                        .getMessage());
            } else {
                HologramManager.updateHologram(location, plugin.getLocale().getMessage("general.claim.powercell.low")
                        .processPlaceholder("time", Methods.makeReadable((getTotalPower() + Settings.MINIMUM_POWER.getInt()) * 60 * 1000))
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
        updateItemsFromGui();
        double total = 0;
        List<String> materials = Settings.ITEM_VALUES.getStringList();
        for (String value : materials) {
            String parts[] = value.split(":");
            CompatibleMaterial material;
            if (parts.length == 2 && (material = CompatibleMaterial.getMaterial(parts[0].trim())) != null) {
                double itemValue = getMaterialAmount(material) * Double.parseDouble(parts[1].trim());

                switch (getCostEquation()) {
                    case DEFAULT:
                        total += itemValue / claim.getClaimSize();
                        break;
                    case LINEAR:
                        total += itemValue / (claim.getClaimSize() * getLinearValue());
                        break;
                    default:
                        total += itemValue;
                }
            }
        }
        return (int) total;
    }

    // Must not be ran if this inventory is open.
    public void stackItems() {
        List<Integer> removed = new ArrayList<>();
        List<ItemStack> newItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i ++) {
            ItemStack item = items.get(i);
            CompatibleMaterial material = CompatibleMaterial.getMaterial(item);

            if (removed.contains(i))
                continue;

            ItemStack newItem = item.clone();
            newItems.add(newItem);
            removed.add(i);

            if (item.getAmount() >= item.getMaxStackSize())
                continue;

            for (int j = 0; j < items.size(); j ++) {
                ItemStack second = items.get(j);
                
                if (newItem.getAmount() > newItem.getMaxStackSize())
                    break;

                if (item.getAmount() >= second.getMaxStackSize()
                        || removed.contains(j)
                        || CompatibleMaterial.getMaterial(second) != material)
                    continue;

                if (item.getAmount() + second.getAmount() > item.getMaxStackSize()) {
                    second.setAmount(newItem.getAmount() + second.getAmount() - newItem.getMaxStackSize());
                    newItem.setAmount(newItem.getMaxStackSize());
                } else {
                    removed.add(j);
                    newItem.setAmount(newItem.getAmount() + second.getAmount());
                }
            }
        }
        items = newItems;
    }

    public double getEconomyBalance() {
        return this.economyBalance;
    }

    public double getEconomyPower() {
        return economyBalance / getEconomyValue();
    }

    private double getItemValue(CompatibleMaterial material) {
        List<String> materials = Settings.ITEM_VALUES.getStringList();
        for (String value : materials) {
            String parts[] = value.split(":");
            if (parts.length == 2 && CompatibleMaterial.getMaterial(parts[0].trim()) == material) {
                double itemValue = Double.parseDouble(parts[1].trim());

                switch (getCostEquation()) {
                    case DEFAULT:
                        return itemValue / claim.getClaimSize();
                    case LINEAR:
                        return itemValue / (claim.getClaimSize() * getLinearValue());
                    default:
                        return itemValue;
                }
            }
        }
        return 0;
    }

    public double getEconomyValue() {
        double value = Settings.ECONOMY_VALUE.getDouble();

        switch (getCostEquation()) {
            case DEFAULT:
                return value * claim.getClaimSize();
            case LINEAR:
                return value * (claim.getClaimSize() * getLinearValue());
            default:
                return value;
        }
    }

    private CostEquation getCostEquation() {
        if (Settings.COST_EQUATION.getString().startsWith("LINEAR")) return CostEquation.LINEAR;
        else return CostEquation.valueOf(Settings.COST_EQUATION.getString());
    }

    private double getLinearValue() {
        if (getCostEquation() != CostEquation.LINEAR) return 1.0d;
        String[] equationSplit = Settings.COST_EQUATION.getString().split(" ");
        return Double.parseDouble(equationSplit[1]);
    }

    public List<ItemStack> getItems() {
        return new ArrayList<>(this.items);
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
    }

    public boolean addItem(ItemStack item) {
        if (items.size() >= 28) return false;
        this.items.add(item);
        return true;
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
        return opened != null ? opened : (opened = new PowerCellGui(UltimateClaims.getInstance(), this.claim));
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
