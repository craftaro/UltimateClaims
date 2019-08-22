package com.songoda.ultimateclaims.tasks;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.PowerCell;
import com.songoda.core.library.compatibility.ServerVersion;
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
            instance.runTaskTimerAsynchronously(plugin, 0, 5);
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

            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
                float xx = (float) (0 + (Math.random() * 1));
                float yy = (float) (0 + (Math.random() * 1));
                float zz = (float) (0 + (Math.random() * 1));
                location.getWorld().spawnParticle(Particle.REDSTONE, location, 2, xx, yy, zz, 1, new Particle.DustOptions(powerCell.getCurrentPower() >= 0 ? Color.LIME : Color.RED, 1.3F));
            } else if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_12)) {
                float red = powerCell.getCurrentPower() >= 0 ? 0.1F : 1.0F;
                float green = powerCell.getCurrentPower() >= 0 ? 1.0F : 0.1F;
                for (int i = 0; i < 2; i++) {
                    float xx = (float) (1.7 * (Math.random() - Math.random()));
                    float yy = (float) (1.0 * (Math.random() - Math.random()));
                    float zz = (float) (1.7 * (Math.random() - Math.random()));
                    Location at = location.clone().add(xx, yy, zz);
                    location.getWorld().spawnParticle(Particle.REDSTONE, at, 0, red, green, 0.1, 1.0); // particle, location, count, red, green, blue, extra data
                }
            }
            //else if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_8))
                // 1.8 requires PacketPlayOutWorldParticles for this. todo?
        }
    }
}
