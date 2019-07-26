package com.songoda.ultimateclaims.tasks;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.invite.Invite;
import com.songoda.ultimateclaims.utils.settings.Setting;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
        for (Invite invite : new ArrayList<>(waitingInventations)) {
            if (!plugin.getClaimManager().hasClaim(invite.getInviter()))
                this.waitingInventations.remove(invite);

            if (System.currentTimeMillis() - invite.getCreated()
                    >= Setting.INVITE_TIMEOUT.getInt() * 1000) {
                OfflinePlayer inviter = Bukkit.getPlayer(invite.getInviter());
                OfflinePlayer invited = Bukkit.getPlayer(invite.getInvited());

                if (inviter != null && inviter.isOnline())
                    inviter.getPlayer().sendMessage("Your invite expired");

                if (invited != null && invited.isOnline())
                    invited.getPlayer().sendMessage("Your invite expired");

                waitingInventations.remove(invite);
            }
        }
    }

    public Invite addInvite(Invite invite) {
        this.waitingInventations.add(invite);
        return invite;
    }
}
