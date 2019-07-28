package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDissolve extends AbstractCommand {

    public CommandDissolve(AbstractCommand parent) {
        super("dissolve", parent, true);
    }

    @Override
    protected ReturnType runCommand(UltimateClaims instance, CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (!instance.getClaimManager().hasClaim(player)) {
            instance.getLocale().getMessage("command.general.noclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Claim claim = instance.getClaimManager().getClaim(player);

        claim.destroy();
        instance.getLocale().getMessage("general.claim.dissolve")
                .processPlaceholder("claim", claim.getName())
                .sendPrefixedMessage(player);

        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.dissolve";
    }

    @Override
    public String getSyntax() {
        return "/ucl dissolve";
    }

    @Override
    public String getDescription() {
        return "dissolve.";
    }
}
