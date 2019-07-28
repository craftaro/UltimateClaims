package com.songoda.ultimateclaims.tasks;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.PowerCell;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.utils.settings.Setting;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

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
            PowerCell powerCell = claim.getPowerCell();
            int tick = powerCell.tick(claim);
            if (tick == -1) {
                for (ClaimMember member : claim.getMembers())
                    this.outOfPower(member);
                this.outOfPower(claim.getOwner());
            } else if (tick == (Setting.MINIMUM_POWER.getInt() + 10)) {
                for (ClaimMember member : claim.getMembers())
                    this.tenLeft(member);
                this.tenLeft(claim.getOwner());
            } else if (tick <= Setting.MINIMUM_POWER.getInt()) {
                for (ClaimMember member : claim.getMembers())
                    this.dissolved(member);
                this.dissolved(claim.getOwner());
                claim.destroy();
            }
        }
    }

    private void outOfPower(ClaimMember member) {
        Player player = Bukkit.getPlayer(member.getUniqueId());
        if (player != null)
            player.sendMessage("Your claim " + member.getClaim().getName() + " is out of power...");
    }

    private void tenLeft(ClaimMember member) {
        Player player = Bukkit.getPlayer(member.getUniqueId());
        if (player != null)
            player.sendMessage("Your claim " + member.getClaim().getName() + " is about to be dissolved...");
    }

    private void dissolved(ClaimMember member) {
        Player player = Bukkit.getPlayer(member.getUniqueId());
        if (player != null)
            player.sendMessage("Your claim " + member.getClaim().getName() + " was dissolved.");
    }
}
