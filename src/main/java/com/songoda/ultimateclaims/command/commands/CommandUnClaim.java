package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUnClaim extends AbstractCommand {

    public CommandUnClaim(AbstractCommand parent) {
        super("unclaim", parent, true);
    }

    @Override
    protected ReturnType runCommand(UltimateClaims instance, CommandSender sender, String... args) {
        Player player = (Player) sender;

        Claim claim = instance.getClaimManager().getClaim(player.getLocation().getChunk());

        if (claim == null) {
            instance.getLocale().getMessage("command.general.notclaimed").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (claim.getOwner().getUniqueId() != player.getUniqueId()){
            instance.getLocale().getMessage("command.general.notyourclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        claim.removeClaimedChunk(player.getLocation().getChunk());

        instance.getLocale().getMessage("command.unclaim.success").sendPrefixedMessage(sender);
        if (claim.getClaimedChunks().size() == 0)
            claim.destroy();

        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.unclaim";
    }

    @Override
    public String getSyntax() {
        return "/ucl unclaim";
    }

    @Override
    public String getDescription() {
        return "zunClaim land.";
    }
}
