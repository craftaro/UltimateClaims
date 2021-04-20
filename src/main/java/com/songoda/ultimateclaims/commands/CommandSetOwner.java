package com.songoda.ultimateclaims.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.member.ClaimRole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandSetOwner extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandSetOwner(UltimateClaims plugin) {
        super(true, "setowner");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (args.length < 1)
            return ReturnType.SYNTAX_ERROR;

        OfflinePlayer newOwner = Bukkit.getPlayer(args[0]);

        if (newOwner == null || !newOwner.isOnline()) {
            plugin.getLocale().getMessage("command.general.noplayer").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Claim claim = plugin.getClaimManager().getClaim(player);

        if (player.getUniqueId().equals(newOwner.getUniqueId())) {
            plugin.getLocale().getMessage("command.setowner.notself").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (!claim.getMembers().stream()
                .filter(m -> m.getRole() == ClaimRole.MEMBER)
                .anyMatch(m -> m.getUniqueId().equals(newOwner.getUniqueId()))) {
            plugin.getLocale().getMessage("command.setowner.noinclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (!plugin.getClaimManager().hasClaim(player)) {
            plugin.getLocale().getMessage("command.general.noclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (!plugin.getClaimManager().hasClaim(player)) {
            plugin.getLocale().getMessage("command.general.noclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (claim.transferOwnership(newOwner))
            plugin.getLocale().getMessage("command.setowner.success")
                    .processPlaceholder("claim", claim.getName())
                    .sendPrefixedMessage(player);
        else
            plugin.getLocale().getMessage("command.setowner.failed")
                    .processPlaceholder("claim", claim.getName())
                    .sendPrefixedMessage(player);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.setowner";
    }

    @Override
    public String getSyntax() {
        return "setowner <поселенец>";
    }

    @Override
    public String getDescription() {
        return "Передать права на руководство поселением.";
    }
}
