package com.songoda.ultimateclaims.tasks;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TrackerTask extends BukkitRunnable {

    private static TrackerTask instance;
    private static UltimateClaims plugin;

    public TrackerTask(UltimateClaims plug) {
        plugin = plug;
    }

    public static TrackerTask startTask(UltimateClaims plug) {
        plugin = plug;
        if (instance == null) {
            instance = new TrackerTask(plugin);
            instance.runTaskTimer(plugin, 30, 20);
        }

        return instance;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Claim claim = plugin.getClaimManager().getClaim(player.getLocation().getChunk());
            if (claim == null) continue;
            ClaimMember member = claim.getMember(player);
            if (member == null) {
                claim.addMember(player, ClaimRole.VISITOR);
                member = claim.getMember(player);
            }
            member.setPresent(true);
            if (claim.isBanned(player.getUniqueId()) || claim.isLocked() && claim.getMember(player).getRole() == ClaimRole.VISITOR)
                member.eject();
        }
        for (Claim claim : plugin.getClaimManager().getRegisteredClaims()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ClaimMember member = claim.getMember(player);
                if (member == null || !member.isPresent()) continue;
                Claim in = plugin.getClaimManager().getClaim(player.getLocation().getChunk());
                if (in != claim)
                    member.setPresent(false);
            }
        }
    }
}
