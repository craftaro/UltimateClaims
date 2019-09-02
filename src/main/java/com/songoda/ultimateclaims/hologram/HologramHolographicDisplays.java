package com.songoda.ultimateclaims.hologram;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.songoda.ultimateclaims.UltimateClaims;
import org.bukkit.Bukkit;
import org.bukkit.Location;


public class HologramHolographicDisplays extends Hologram {

    public HologramHolographicDisplays(UltimateClaims plugin) {
        super(plugin);
    }

    @Override
    public void add(Location location, String line) {
        com.gmail.filoghost.holographicdisplays.api.Hologram hologram = HologramsAPI.createHologram(plugin, location);
        hologram.appendTextLine(line);
    }

    @Override
    public void remove(Location location) {
        fixLocation(location);
        for (com.gmail.filoghost.holographicdisplays.api.Hologram hologram : HologramsAPI.getHolograms(plugin)) {
            if (hologram.getX() != location.getX()
                    || hologram.getY() != location.getY()
                    || hologram.getZ() != location.getZ()) continue;
            hologram.delete();
        }
    }

    @Override
    public void update(Location location, String line) {
        if(location == null) 
            return;
        int x = location.getBlockX() >> 4;
        int z = location.getBlockZ() >> 4;
        if (location.getWorld() == null || !location.getWorld().isChunkLoaded(x, z))
            return;

        fixLocation(location);
        for (com.gmail.filoghost.holographicdisplays.api.Hologram hologram : HologramsAPI.getHolograms(plugin)) {
            if (hologram.getX() != location.getX()
                    || hologram.getY() != location.getY()
                    || hologram.getZ() != location.getZ()) continue;
            hologram.clearLines();
            hologram.appendTextLine(line);
            return;
        }
        add(location, line);
    }

    private void fixLocation(Location location) {
        location.add(0.5, 1.52, 0.5);
    }
}
