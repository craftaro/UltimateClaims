/**
 * Hooks for adding a custom WorldGuard flag
 * 
 * Note: Hooks must be added before WG loads!
 */
package com.songoda.ultimateclaims.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

public class WorldGuardHook {
    
	static Boolean wgPlugin = null;
	static boolean hooksInstalled = false;
    static Map<String, Object> flags = new HashMap();

    /**
     * Attempt to register a worldGuard flag (ALLOW/DENY)
     * @param flag
     * @param state 
     */
    public static void addHook(String flag, boolean state) {
        if(wgPlugin == null) 
            wgPlugin = Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
        if(!wgPlugin) return;
        
        StateFlag wgFlag = new StateFlag(flag, state);
		try {
			WorldGuard.getInstance().getFlagRegistry().register(wgFlag);
            flags.put(flag, wgFlag);
		} catch (Exception ex) {
            Bukkit.getServer().getLogger().log(Level.WARNING, "Could not add flag {0} to WorldGuard", wgFlag.getName());
			wgFlag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get(wgFlag.getName());
			if(wgFlag == null) {
                wgPlugin = false;
				Bukkit.getServer().getLogger().log(Level.WARNING, "Could not hook WorldGuard");
            } else {
                flags.put(flag, wgFlag);
				Bukkit.getServer().getLogger().log(Level.WARNING, "Loaded existing {1} {0}", new Object[] {wgFlag.getName(), wgFlag.getClass().getSimpleName()});
            }
		}
	}
    
    public static boolean isEnabled() {
        return wgPlugin != null && wgPlugin;
    }

    /**
     * Checks this location to see what this flag is set to
     * @param l location to check
     * @param flag ALLOW/DENY flag to check
     * @return flag state, or null if undefined
     */
    public static Boolean getBooleanFlag(Location l, String flag) {
        if(wgPlugin == null || !wgPlugin) return null;
        Object flagObj = flags.get(flag);
        if(flagObj == null)
            flags.put(flag, flagObj = WorldGuard.getInstance().getFlagRegistry().get(flag));
        if(flagObj instanceof StateFlag) {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(l);
            return query.testState(loc, (RegionAssociable) null, (StateFlag) flagObj);
        }
        return null;
    }

    /**
     * Query all regions that are in or intersect this chunk
     * @param c chunk to check for regions in
     * @param flag ALLOW/DENY flag to check
     * @return flag state, or null if undefined
     */
    public static Boolean getBooleanFlag(Chunk c, String flag) {
        if(wgPlugin == null || !wgPlugin) return null;
        Object flagObj = flags.get(flag);
        if(flagObj == null)
            flags.put(flag, flagObj = WorldGuard.getInstance().getFlagRegistry().get(flag));
        if(flagObj instanceof StateFlag) {
            RegionManager worldManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(c.getWorld()));
            ProtectedCuboidRegion chunkRegion = new ProtectedCuboidRegion("__TEST__",
                BlockVector3.at(c.getX() << 4, c.getWorld().getMaxHeight(), c.getZ() << 4),
                BlockVector3.at((c.getX() << 4) + 15, 0, (c.getZ() << 4) + 15));
            ApplicableRegionSet set = worldManager.getApplicableRegions(chunkRegion);
            if(set.size() == 0)
                return null;
            State result = worldManager.getApplicableRegions(chunkRegion).queryState((RegionAssociable) null, (StateFlag) flagObj);
            return result == State.ALLOW;
        }
        return null;
    }
}
