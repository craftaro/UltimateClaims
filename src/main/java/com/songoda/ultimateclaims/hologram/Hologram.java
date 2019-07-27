package com.songoda.ultimateclaims.hologram;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.PowerCell;
import com.songoda.ultimateclaims.utils.Methods;
import org.bukkit.Location;

public abstract class Hologram {

    protected final UltimateClaims plugin;

    Hologram(UltimateClaims plugin) {
        this.plugin = plugin;
    }

    public void update(PowerCell powerCell) {
        update(powerCell.getLocation(), Methods.makeReadable((long)(powerCell.getTotalPower() * 60 * 1000)));
    }

    public void remove(PowerCell powerCell) {
        remove(powerCell.getLocation());
    }

    protected abstract void add(Location location, String line);

    protected abstract void remove(Location location);

    protected abstract void update(Location location, String line);

}
