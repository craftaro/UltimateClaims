package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.command.AbstractCommand;
import com.songoda.ultimateclaims.invite.Invite;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.utils.settings.Setting;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandAccept extends AbstractCommand {

    public CommandAccept(AbstractCommand parent) {
        super(parent, true, "accept");
    }

    @Override
    protected ReturnType runCommand(UltimateClaims instance, CommandSender sender, String... args) {
        Player player = (Player) sender;


        Invite invite = instance.getInviteTask().getInvite(player.getUniqueId());

        if (invite == null) {
            instance.getLocale().getMessage("command.accept.none").sendPrefixedMessage(player);
        } else {
            if (Math.toIntExact(invite.getClaim().getMembers().stream()
                    .filter(member -> member.getRole() == ClaimRole.MEMBER).count()) >= Setting.MAX_MEMBERS.getInt()) {
                instance.getLocale().getMessage("command.accept.maxed").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            if (invite.getClaim().getMember(player) == null)
                invite.getClaim().addMember(player, ClaimRole.MEMBER);

            if (invite.getClaim().getMember(player).getRole() == ClaimRole.VISITOR)
                invite.getClaim().getMember(player).setRole(ClaimRole.MEMBER);


            invite.accepted();
            instance.getLocale().getMessage("command.accept.success")
                    .processPlaceholder("claim", invite.getClaim().getName())
                    .sendPrefixedMessage(player);

            OfflinePlayer owner = Bukkit.getPlayer(invite.getInviter());

            if (owner != null && owner.isOnline())
                instance.getLocale().getMessage("command.accept.accepted")
                        .processPlaceholder("name", player.getName())
                        .sendPrefixedMessage(owner.getPlayer());
        }

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(UltimateClaims instance, CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.accept";
    }

    @Override
    public String getSyntax() {
        return "/c accept";
    }

    @Override
    public String getDescription() {
        return "Accept the latest claim invitation.";
    }
}
