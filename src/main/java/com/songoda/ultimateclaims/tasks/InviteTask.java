package com.songoda.ultimateclaims.tasks;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.invite.Invite;
import com.songoda.ultimateclaims.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

public class InviteTask extends BukkitRunnable {

    private static InviteTask instance;
    private static UltimateClaims plugin;

    private final Set<Invite> waitingInventations = new CopyOnWriteArraySet<>();

    public InviteTask(UltimateClaims plug) {
        plugin = plug;
    }

    public static InviteTask startTask(UltimateClaims plug) {
        plugin = plug;
        if (instance == null) {
            instance = new InviteTask(plugin);
            instance.runTaskTimerAsynchronously(plugin, 60, 60);
        }

        return instance;
    }

    @Override
    public void run() {
        if(waitingInventations.isEmpty())
            return;

        final long now = System.currentTimeMillis(),
                timeout = Settings.INVITE_TIMEOUT.getInt() * 1000;

        // clean up expired invites
        for (Invite invite : new ArrayList<>(waitingInventations)) {
            if (invite.isAccepted() || !plugin.getClaimManager().hasClaim(invite.getInviter()))
                this.waitingInventations.remove(invite);

            if (now - invite.getCreated() >= timeout) {
                OfflinePlayer inviter = Bukkit.getPlayer(invite.getInviter());
                OfflinePlayer invited = Bukkit.getPlayer(invite.getInvited());

                if (inviter != null && inviter.isOnline())
                    plugin.getLocale().getMessage("event.invite.expired")
                            .sendPrefixedMessage(inviter.getPlayer());

                if (invited != null && invited.isOnline())
                    plugin.getLocale().getMessage("event.invite.expired")
                            .sendPrefixedMessage(invited.getPlayer());
                waitingInventations.remove(invite);
            }
        }
    }

    public Invite addInvite(Invite invite) {
        this.waitingInventations.add(invite);
        return invite;
    }

    public Invite getInvite(UUID uuid) {
        return waitingInventations.stream()
                .filter(invite -> invite.getInvited() == uuid).findFirst().orElse(null);
    }
}
