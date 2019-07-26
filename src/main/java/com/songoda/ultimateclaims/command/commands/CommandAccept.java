package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.command.AbstractCommand;
import com.songoda.ultimateclaims.invite.Invite;
import com.songoda.ultimateclaims.member.ClaimRole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAccept extends AbstractCommand {

    public CommandAccept(AbstractCommand parent) {
        super("accept", parent, true);
    }

    @Override
    protected ReturnType runCommand(UltimateClaims instance, CommandSender sender, String... args) {
        Player player = (Player) sender;


        Invite invite = instance.getInviteTask().getInvite(player.getUniqueId());

        if (invite == null) {
            sender.sendMessage("You have no invites");
        } else {
            invite.getClaim().addMember(player, ClaimRole.MEMBER);
            invite.accepted();
            sender.sendMessage("Invite accepted.");
        }

        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.accept";
    }

    @Override
    public String getSyntax() {
        return "/ucl invite";
    }

    @Override
    public String getDescription() {
        return "Invite.";
    }
}
