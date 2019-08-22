package com.songoda.ultimateclaims.hologram;

import com.songoda.core.library.hologram.HologramManager;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.PowerCell;
import com.songoda.ultimateclaims.utils.Methods;
import com.songoda.ultimateclaims.utils.settings.Setting;
import org.bukkit.Location;

public class Hologram {

    protected final UltimateClaims plugin;

    public Hologram(UltimateClaims plugin) {
        this.plugin = plugin;
    }

    public void update(PowerCell powerCell) {
        if (powerCell.getTotalPower() > 1) {
            HologramManager.update(powerCell.getLocation(), plugin.getLocale().getMessage("general.claim.powercell")
                    .processPlaceholder("time", Methods.makeReadable(powerCell.getTotalPower() * 60 * 1000))
                    .getMessage());
        } else {
            HologramManager.update(powerCell.getLocation(), plugin.getLocale().getMessage("general.claim.powercell.low")
                    .processPlaceholder("time", Methods.makeReadable((powerCell.getTotalPower() + Setting.MINIMUM_POWER.getInt()) * 60 * 1000))
                    .getMessage());
        }
    }

    public void remove(PowerCell powerCell) {
        HologramManager.remove(powerCell.getLocation());
    }

}
