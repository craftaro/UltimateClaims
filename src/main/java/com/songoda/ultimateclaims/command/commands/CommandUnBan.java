package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandUnBan extends AbstractCommand {

    public CommandUnBan(AbstractCommand parent) {
        super(parent, true, "unban");
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

        OfflinePlayer toBan = Bukkit.getPlayer(args[1]);

        if (toBan == null) {
            instance.getLocale().getMessage("command.general.noplayer").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (player.getUniqueId().equals(toBan.getUniqueId())) {
            instance.getLocale().getMessage("command.unban.notself").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (toBan.isOnline())
            instance.getLocale().getMessage("command.unban.unbanned")
                    .processPlaceholder("claim", toBan.getName())
                    .sendPrefixedMessage(toBan.getPlayer());

        instance.getLocale().getMessage("command.unban.unban")
                .processPlaceholder("name", toBan.getName())
                .processPlaceholder("claim", claim.getName())
                .sendPrefixedMessage(player);

        claim.unBanPlayer(toBan.getUniqueId());
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(UltimateClaims instance, CommandSender sender, String... args) {
        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream().filter(p -> p != sender)
                    .map(Player::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.unban";
    }

    @Override
    public String getSyntax() {
        return "/c unban <member>";
    }

    @Override
    public String getDescription() {
        return "Unban a member from your claim.";
    }
}
