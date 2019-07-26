package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.command.AbstractCommand;
import com.songoda.ultimateclaims.member.ClaimRole;
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
            sender.sendMessage("YOu need to be in a claim to do this.");
            return ReturnType.FAILURE;
        }

        OfflinePlayer invited = Bukkit.getPlayer(args[1]);

        if (invited == null) {
            sender.sendMessage("That player does not exist or is not online.");
            return ReturnType.FAILURE;
        }


        instance.getClaimManager().getClaim(player).addMember(invited.getUniqueId(), ClaimRole.MEMBER);

        sender.sendMessage("You invited them");
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.invite";
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
