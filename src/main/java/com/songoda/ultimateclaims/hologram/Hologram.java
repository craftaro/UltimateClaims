package com.songoda.ultimateclaims.hologram;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.PowerCell;
import com.songoda.ultimateclaims.utils.Methods;
import com.songoda.ultimateclaims.utils.settings.Setting;
import org.bukkit.Location;

public abstract class Hologram {

    protected final UltimateClaims plugin;

    Hologram(UltimateClaims plugin) {
        this.plugin = plugin;
    }

    public void update(PowerCell powerCell) {
        if (powerCell.getCurrentPower() > 1) {
            update(powerCell.getLocation(), plugin.getLocale().getMessage("general.claim.powercell")
                    .processPlaceholder("time", Methods.makeReadable(powerCell.getTotalPower() * 60 * 1000))
                    .getMessage());
        } else {
            update(powerCell.getLocation(), plugin.getLocale().getMessage("general.claim.powercell.low")
                    .processPlaceholder("time", Methods.makeReadable((powerCell.getTotalPower() + Setting.MINIMUM_POWER.getInt()) * 60 * 1000))
                    .getMessage());
        }
    }

    public void remove(PowerCell powerCell) {
        remove(powerCell.getLocation());
    }

    protected abstract void add(Location location, String line);

    protected abstract void remove(Location location);

    protected abstract void update(Location location, String line);

}
