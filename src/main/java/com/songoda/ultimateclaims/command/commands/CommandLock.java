package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLock extends AbstractCommand {

    public CommandLock(AbstractCommand parent) {
        super("lock", parent, true);
    }

    @Override
    protected ReturnType runCommand(UltimateClaims instance, CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (!instance.getClaimManager().hasClaim(player)) {
            instance.getLocale().getMessage("command.general.noclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Claim claim = instance.getClaimManager().getClaim(player);

        if (!claim.isLocked())
            instance.getLocale().getMessage("command.lock.locked")
                    .sendPrefixedMessage(player);
        else
            instance.getLocale().getMessage("command.lock.unlocked")
                    .sendPrefixedMessage(player);

        claim.setLocked(!claim.isLocked());

        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.lock";
    }

    @Override
    public String getSyntax() {
        return "/c lock";
    }

    @Override
    public String getDescription() {
        return "Lock or unlock your claim.";
    }
}
