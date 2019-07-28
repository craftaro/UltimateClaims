package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandKick extends AbstractCommand {

    public CommandKick(AbstractCommand parent) {
        super("kick", parent, true);
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

        OfflinePlayer toKick = Bukkit.getPlayer(args[1]);

        if (toKick == null) {
            instance.getLocale().getMessage("command.general.noplayer").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (claim.getMember(toKick.getUniqueId()) == null) {
            instance.getLocale().getMessage("command.general.notinclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (toKick.isOnline())
            instance.getLocale().getMessage("command.kick.kicked")
                    .processPlaceholder("claim", toKick.getName())
                    .sendPrefixedMessage(toKick.getPlayer());

        instance.getLocale().getMessage("command.kick.kick")
                .processPlaceholder("name", toKick.getName())
                .processPlaceholder("claim", claim.getName())
                .sendPrefixedMessage(player);
        claim.removeMember(toKick.getUniqueId());
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.kick";
    }

    @Override
    public String getSyntax() {
        return "/c kick <member>";
    }

    @Override
    public String getDescription() {
        return "Kick a member from your claim.";
    }
}
