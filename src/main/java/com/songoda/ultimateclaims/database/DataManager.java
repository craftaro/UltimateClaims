package com.songoda.ultimateclaims.database;

import org.bukkit.plugin.Plugin;

public class DataManager {

    private final Plugin plugin;

    public DataManager(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * @return the prefix to be used by all table names
     */
    public String getTablePrefix() {
        return this.plugin.getDescription().getName().toLowerCase() + '_';
    }

}
