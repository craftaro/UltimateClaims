package com.songoda.ultimateclaims.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.core.library.commands.AbstractCommand;
import com.songoda.ultimateclaims.invite.Invite;
import com.songoda.ultimateclaims.member.ClaimRole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CommandInvite extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandInvite(UltimateClaims plugin, AbstractCommand parent) {
        super(parent, true, "invite");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (args.length < 2)
            return ReturnType.SYNTAX_ERROR;

        if (!plugin.getClaimManager().hasClaim(player)) {
            plugin.getLocale().getMessage("command.general.noclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Claim claim = plugin.getClaimManager().getClaim(player);

        OfflinePlayer invited = Bukkit.getPlayer(args[1]);

        if (invited == null || !invited.isOnline()) {
            plugin.getLocale().getMessage("command.general.noplayer").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (player.getUniqueId().equals(invited.getUniqueId())) {
            plugin.getLocale().getMessage("command.invite.notself").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (claim.getMembers().stream()
                .filter(m -> m.getRole() == ClaimRole.MEMBER)
                .anyMatch(m -> m.getUniqueId().equals(invited.getUniqueId()))) {
            plugin.getLocale().getMessage("command.invite.already").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (plugin.getInviteTask().getInvite(player.getUniqueId()) != null) {
            plugin.getLocale().getMessage("command.invite.alreadyinvited").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        plugin.getInviteTask().addInvite(new Invite(player.getUniqueId(), invited.getUniqueId(), claim));

        plugin.getLocale().getMessage("command.invite.invite")
                .processPlaceholder("name", invited.getName())
                .sendPrefixedMessage(player);

        plugin.getLocale().getMessage("command.invite.invited")
                .processPlaceholder("claim", claim.getName())
                .sendPrefixedMessage(invited.getPlayer());
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
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
