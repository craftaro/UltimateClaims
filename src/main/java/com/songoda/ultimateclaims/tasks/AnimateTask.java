package com.songoda.ultimateclaims.tasks;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.PowerCell;
import com.songoda.ultimateclaims.utils.ServerVersion;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class AnimateTask extends BukkitRunnable {

    private static AnimateTask instance;
    private static UltimateClaims plugin;

    public AnimateTask(UltimateClaims plug) {
        plugin = plug;
    }

    public static AnimateTask startTask(UltimateClaims plug) {
        plugin = plug;
        if (instance == null) {
            instance = new AnimateTask(plugin);
            instance.runTaskTimer(plugin, 0, 5);
        }

        return instance;
    }

    @Override
    public void run() {
        for (Claim claim : new ArrayList<>(plugin.getClaimManager().getRegisteredClaims())) {
            PowerCell powerCell = claim.getPowerCell();

            if (!powerCell.hasLocation()) continue;
            Location location = powerCell.getLocation().add(.5, .5, .5);

            int x = location.getBlockX() >> 4;
            int z = location.getBlockZ() >> 4;

            if (!location.getWorld().isChunkLoaded(x, z)) {
                continue;
            }

            float xx = (float) (0 + (Math.random() * 1));
            float yy = (float) (0 + (Math.random() * 1));
            float zz = (float) (0 + (Math.random() * 1));

            if (plugin.isServerVersionAtLeast(ServerVersion.V1_13))
                location.getWorld().spawnParticle(Particle.REDSTONE, location, 5, xx, yy, zz, 1, new Particle.DustOptions(powerCell.getCurrentPower() >= 0 ? Color.LIME : Color.RED, 1F));
            else if (plugin.isServerVersionAtLeast(ServerVersion.V1_12))
                location.getWorld().spawnParticle(Particle.REDSTONE, location, 5, xx, yy, zz);
            else if (plugin.isServerVersionAtLeast(ServerVersion.V1_8))
                location.getWorld().spawnParticle(Particle.REDSTONE, location, 5, xx, yy, zz, 1, 1F);
        }
    }
}
