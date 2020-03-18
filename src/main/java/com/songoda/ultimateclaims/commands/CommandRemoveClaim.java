package com.songoda.ultimateclaims.commands;

import com.songoda.core.hooks.EconomyManager;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.core.commands.AbstractCommand;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class CommandRemoveClaim extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandRemoveClaim(UltimateClaims plugin) {
        super(true, "removeclaim");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        Chunk chunk = player.getLocation().getChunk();
        Claim claim = plugin.getClaimManager().getClaim(chunk);

        if (claim == null) {
            plugin.getLocale().getMessage("command.general.notclaimed").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        OfflinePlayer offlineOwner = claim.getOwner().getPlayer();

        // If the owner is online, send him a dissolve message and return PowerCell funds
        if (offlineOwner.isOnline()) {
            Player owner = offlineOwner.getPlayer();

            plugin.getLocale().getMessage("general.claim.dissolve")
                    .processPlaceholder("claim", claim.getName())
                    .sendPrefixedMessage(owner);

            // Return cash to the owner of the claim, if online
            double claimBank = claim.getPowerCell().getEconomyBalance();

            if (claimBank > 0 && EconomyManager.deposit(owner, claimBank))
                plugin.getLocale().getMessage("general.claim.returnfunds")
                        .processPlaceholder("amount", claimBank)
                        .sendPrefixedMessage(owner);
        }

        // Remove the whole claim
        claim.destroy();

        // Send a message to player
        plugin.getLocale().getMessage("command.removeclaim.success")
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
        return "ultimateclaims.removeclaim";
    }

    @Override
    public String getSyntax() {
        return "removeclaim";
    }

    @Override
    public String getDescription() {
        return "Remove a claim you're standing in.";
    }
}
