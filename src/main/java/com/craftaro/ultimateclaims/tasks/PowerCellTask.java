package com.craftaro.ultimateclaims.tasks;

import com.craftaro.ultimateclaims.UltimateClaims;
import com.craftaro.ultimateclaims.claim.Claim;
import com.craftaro.ultimateclaims.claim.ClaimDeleteReason;
import com.craftaro.ultimateclaims.claim.PowerCell;
import com.craftaro.ultimateclaims.member.ClaimMember;
import com.craftaro.ultimateclaims.member.ClaimRole;
import com.craftaro.ultimateclaims.settings.Settings;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PowerCellTask extends BukkitRunnable {
    private static PowerCellTask instance;
    private static UltimateClaims plugin;

    public PowerCellTask(UltimateClaims plug) {
        plugin = plug;
    }

    public static PowerCellTask startTask(UltimateClaims plug) {
        plugin = plug;
        if (instance == null) {
            instance = new PowerCellTask(plugin);
            instance.runTaskTimer(plugin, 0, 60 * 20); // 60 * 20
        }

        return instance;
    }

    @Override
    public void run() {
        for (Claim claim : new ArrayList<>(plugin.getClaimManager().getRegisteredClaims())) {
            for (PowerCell powerCell : claim.getPowerCells()) {
                List<ClaimMember> members = claim.getOwnerAndMembers().stream()
                        .filter(member -> member.getRole() != ClaimRole.VISITOR).collect(Collectors.toList());
                for (ClaimMember member : members) {
                    if (member.getPlayer().isOnline()) {
                        member.setPlayTime(member.getPlayTime() + (60 * 1000)); // Should be a var.
                    }
                }
                int tick = powerCell.tick();
                if (powerCell.getTotalPower() <= 0) {
                    if (tick == -1 && !powerCell.hasLocation()) {
                        for (ClaimMember member : claim.getMembers()) {
                            this.dissolved(member);
                        }
                        this.dissolved(claim.getOwner());
                        claim.destroy(ClaimDeleteReason.POWERCELL_TIMEOUT);
                    } else if (tick == -1) {
                        for (ClaimMember member : members) {
                            this.outOfPower(member);
                        }
                    } else if (tick == (Settings.MINIMUM_POWER.getInt() + 10)) {
                        for (ClaimMember member : members) {
                            this.tenLeft(member);
                        }
                    } else if (tick <= Settings.MINIMUM_POWER.getInt()) {
                        for (ClaimMember member : members) {
                            this.dissolved(member);
                        }
                        claim.destroy(ClaimDeleteReason.POWERCELL_TIMEOUT);
                    }
                }
            }
        }
    }

    private void outOfPower(ClaimMember member) {
        OfflinePlayer player = member.getPlayer();
        if (player.isOnline()) {
            plugin.getLocale().getMessage("event.powercell.lowpower")
                    .processPlaceholder("claim", member.getClaim().getName())
                    .sendPrefixedMessage(player.getPlayer());
        }
    }

    private void tenLeft(ClaimMember member) {
        OfflinePlayer player = member.getPlayer();
        if (player.isOnline()) {
            plugin.getLocale().getMessage("event.powercell.superpower")
                    .processPlaceholder("claim", member.getClaim().getName())
                    .sendPrefixedMessage(player.getPlayer());
        }
    }

    private void dissolved(ClaimMember member) {
        OfflinePlayer player = member.getPlayer();
        if (player.isOnline()) {
            plugin.getLocale().getMessage("general.claim.dissolve")
                    .processPlaceholder("claim", member.getClaim().getName())
                    .sendPrefixedMessage(player.getPlayer());
        }
    }
}
