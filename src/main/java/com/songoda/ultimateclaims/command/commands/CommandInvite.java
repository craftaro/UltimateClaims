package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.command.AbstractCommand;
import com.songoda.ultimateclaims.invite.Invite;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandInvite extends AbstractCommand {

    public CommandInvite(AbstractCommand parent) {
        super("invite", parent, true);
    }

    @Override
    protected ReturnType runCommand(UltimateClaims instance, CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (args.length < 2)
            return ReturnType.SYNTAX_ERROR;

        if (!instance.getClaimManager().hasClaim(player)) {
            instance.getLocale().getMessage("command.general.noclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Claim claim = instance.getClaimManager().getClaim(player);

        OfflinePlayer invited = Bukkit.getPlayer(args[1]);

        if (invited == null && !invited.isOnline()) {
            instance.getLocale().getMessage("command.general.noplayer").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        instance.getInviteTask().addInvite(new Invite(player.getUniqueId(), invited.getUniqueId(), claim));

        instance.getLocale().getMessage("command.invite.invite")
                .processPlaceholder("name", invited.getName())
                .sendPrefixedMessage(player);

        instance.getLocale().getMessage("command.invite.invited")
                .processPlaceholder("claim", claim.getName())
                .sendPrefixedMessage(invited.getPlayer());
        return ReturnType.SUCCESS;
    }


    @Override
    public String getPermissionNode() {
        return "ultimateclaims.invite";
    }

    @Override
    public String getSyntax() {
        return "/c invite <player>";
    }

    @Override
    public String getDescription() {
        return "Invite a player to join your claim.";
    }
}
