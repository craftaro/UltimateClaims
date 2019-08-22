package com.songoda.ultimateclaims.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.core.library.commands.AbstractCommand;
import com.songoda.ultimateclaims.member.ClaimRole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CommandAddMember extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandAddMember(UltimateClaims plugin) {
        super(true, "addmember");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (args.length < 1)
            return ReturnType.SYNTAX_ERROR;

        if (!plugin.getClaimManager().hasClaim(player)) {
            plugin.getLocale().getMessage("command.general.noclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Claim claim = plugin.getClaimManager().getClaim(player);

        OfflinePlayer toInvite = Bukkit.getOfflinePlayer(args[0]);

        if (toInvite == null || !(toInvite.hasPlayedBefore() || toInvite.isOnline())) {
            plugin.getLocale().getMessage("command.general.noplayer").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (player.getUniqueId().equals(toInvite.getUniqueId())) {
            plugin.getLocale().getMessage("command.invite.notself").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (claim.getMembers().stream()
                .filter(m -> m.getRole() == ClaimRole.MEMBER)
                .anyMatch(m -> m.getUniqueId().equals(toInvite.getUniqueId()))) {
            plugin.getLocale().getMessage("command.invite.already").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        claim.addMember(toInvite, ClaimRole.MEMBER);

        if(toInvite.isOnline())
            plugin.getLocale().getMessage("command.addmember.added")
                    .processPlaceholder("claim", claim.getName())
                    .sendPrefixedMessage(toInvite.getPlayer());

        plugin.getLocale().getMessage("command.addmember.add")
                .processPlaceholder("name", toInvite.getName())
                .sendPrefixedMessage(player);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) {
            final Player player = sender instanceof Player ? (Player) sender : null;
            return Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p != sender
                            && p.getName().toLowerCase().startsWith(args[0].toLowerCase())
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
