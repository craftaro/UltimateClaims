package com.songoda.ultimateclaims.settings;

import org.bukkit.Location;

public class PluginSettings {

    private Location spawnPoint = null;

    public Location getSpawnPoint() {
        return spawnPoint == null ? null : spawnPoint.clone();
    }

    public void setSpawnPoint(Location spawnPoint) {
        this.spawnPoint = spawnPoint;
    }
}
