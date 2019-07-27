package com.songoda.ultimateclaims.tasks;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.PowerCell;
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
            powerCell.getLocation().getWorld()
                    .spawnParticle(Particle.SPELL_WITCH, powerCell.getLocation().add(.5,.5,.5), 10);
        }
    }
}
