package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.ClaimBuilder;
import com.songoda.ultimateclaims.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandClaim extends AbstractCommand {

    public CommandClaim(AbstractCommand parent) {
        super("claim", parent, true);
    }

    @Override
    protected ReturnType runCommand(UltimateClaims instance, CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (instance.getClaimManager().hasClaim(player.getLocation().getChunk())) {
            player.sendMessage("This is already claimed.");
            return ReturnType.FAILURE;
        }

        if (instance.getClaimManager().hasClaim(player))
            instance.getClaimManager().getClaim(player).addClaimedChunk(player.getLocation().getChunk());
        else
            instance.getClaimManager().addClaim(player,
                    new ClaimBuilder()
                            .setOwner(player)
                            .addClaimedChunks(player.getLocation().getChunk())
                            .build());

        sender.sendMessage("claimed " + instance.getClaimManager().getClaim(player).getClaimedChunks().size());
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.claim";
    }

    @Override
    public String getSyntax() {
        return "/ucl claim";
    }

    @Override
    public String getDescription() {
        return "Claim land.";
    }
}
