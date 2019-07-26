package com.songoda.ultimateclaims.tasks;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.invite.Invite;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class InviteTask extends BukkitRunnable {

    private static InviteTask instance;
    private static UltimateClaims plugin;

    private final Set<Invite> waitingInventations = new HashSet<>();

    public InviteTask(UltimateClaims plug) {
        plugin = plug;
    }

    public static InviteTask startTask(UltimateClaims plug) {
        plugin = plug;
        if (instance == null) {
            instance = new InviteTask(plugin);
            instance.runTaskTimer(plugin, 0, 20);
        }

        return instance;
    }

    @Override
    public void run() {
        for (Invite invite : waitingInventations) {
            if (invite.getCreated() - System.currentTimeMillis() == 30 * 60 * 1000) {
                Bukkit.broadcastMessage("An invite expired.");
                waitingInventations.remove(invite);
            }
        }
    }
}
