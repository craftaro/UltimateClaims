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

import java.util.List;
import java.util.stream.Collectors;

public class CommandAddMember extends AbstractCommand {

    public CommandAddMember(AbstractCommand parent) {
        super(parent, true, "addmember");
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

        OfflinePlayer toInvite = Bukkit.getOfflinePlayer(args[1]);

        if (toInvite == null || !(toInvite.hasPlayedBefore() || toInvite.isOnline())) {
            instance.getLocale().getMessage("command.general.noplayer").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (player.getUniqueId().equals(toInvite.getUniqueId())) {
            instance.getLocale().getMessage("command.invite.notself").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (claim.getMembers().stream()
                .filter(m -> m.getRole() == ClaimRole.MEMBER)
                .anyMatch(m -> m.getUniqueId().equals(toInvite.getUniqueId()))) {
            instance.getLocale().getMessage("command.invite.already").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        claim.addMember(toInvite, ClaimRole.MEMBER);

        if(toInvite.isOnline())
            instance.getLocale().getMessage("command.addmember.added")
                    .processPlaceholder("claim", claim.getName())
                    .sendPrefixedMessage(toInvite.getPlayer());

        instance.getLocale().getMessage("command.addmember.add")
                .processPlaceholder("name", toInvite.getName())
                .sendPrefixedMessage(player);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(UltimateClaims instance, CommandSender sender, String... args) {
        if (args.length == 2) {
            final Player player = sender instanceof Player ? (Player) sender : null;
            return Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p != sender
                            && p.getName().toLowerCase().startsWith(args[1].toLowerCase())
                            && (player == null || (player.canSee(p) && p.getMetadata("vanished").isEmpty())))
                    .map(Player::getName).collect(Collectors.toList());
        }
        return null;
    }


    @Override
    public String getPermissionNode() {
        return "ultimateclaims.addmember";
    }

    @Override
    public String getSyntax() {
        return "/c addmember <player>";
    }

    @Override
    public String getDescription() {
        return "Add a player to access your claim.";
    }
}
