package com.songoda.ultimateclaims.claim;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.xml.stream.Location;
import java.util.List;

public class PowerCell {

    private Location location;

    private Inventory inventory;

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

    public Inventory getInventory() {
        return inventory;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
