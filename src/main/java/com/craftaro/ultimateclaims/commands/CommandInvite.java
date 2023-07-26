package com.craftaro.ultimateclaims.commands;

import com.craftaro.ultimateclaims.UltimateClaims;
import com.craftaro.ultimateclaims.claim.Claim;
import com.craftaro.core.commands.AbstractCommand;
import com.craftaro.core.utils.PlayerUtils;
import com.craftaro.ultimateclaims.invite.Invite;
import com.craftaro.ultimateclaims.member.ClaimRole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandInvite extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandInvite(UltimateClaims plugin) {
        super(CommandType.PLAYER_ONLY, "invite");
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

        OfflinePlayer invited = Bukkit.getPlayer(args[0]);

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
        if (args.length == 1) {
            return PlayerUtils.getVisiblePlayerNames(sender, args[0]);
        }
        return null;
    }


    @Override
    public String getPermissionNode() {
        return "ultimateclaims.invite";
    }

    @Override
    public String getSyntax() {
        return "invite <player>";
    }

    @Override
    public String getDescription() {
        return "Invite a player to join your claim.";
    }
}
