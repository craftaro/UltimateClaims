package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimBuilder;
import com.songoda.ultimateclaims.command.AbstractCommand;
import com.songoda.ultimateclaims.utils.Methods;
import com.songoda.ultimateclaims.utils.settings.Setting;
import org.bukkit.Chunk;
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
            instance.getLocale().getMessage("command.general.claimed").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        Chunk chunk = player.getLocation().getChunk();

        if (instance.getClaimManager().hasClaim(player)) {
            Claim claim = instance.getClaimManager().getClaim(player);

            if (!claim.getPowerCell().hasLocation()) {
                instance.getLocale().getMessage("command.claim.nocell").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            if (Setting.CHUNKS_MUST_TOUCH.getBoolean()
            && !claim.getClaimedChunks().contains(chunk.getWorld().getChunkAt(chunk.getX() + 1, chunk.getZ()))
            && !claim.getClaimedChunks().contains(chunk.getWorld().getChunkAt(chunk.getX() - 1, chunk.getZ()))
            && !claim.getClaimedChunks().contains(chunk.getWorld().getChunkAt(chunk.getX(), chunk.getZ() + 1))
            && !claim.getClaimedChunks().contains(chunk.getWorld().getChunkAt(chunk.getX(), chunk.getZ() - 1))) {
                instance.getLocale().getMessage("command.claim.nottouching").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            claim.addClaimedChunk(chunk, player);

            if (instance.getHologram() != null)
                instance.getHologram().update(claim.getPowerCell());
        } else {
            instance.getClaimManager().addClaim(player,
                    new ClaimBuilder()
                            .setOwner(player)
                            .addClaimedChunk(chunk, player)
                            .build());

            instance.getLocale().getMessage("command.claim.info")
                    .processPlaceholder("time", Methods.makeReadable((long) (Setting.STARTING_POWER.getInt() * 60 * 1000)))
                    .sendPrefixedMessage(sender);
        }

        instance.getLocale().getMessage("command.claim.success").sendPrefixedMessage(sender);
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
