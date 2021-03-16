package com.songoda.ultimateclaims.commands.admin;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimDeleteReason;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandTransferOwnership extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandTransferOwnership(UltimateClaims plugin) {
        super(true, "admin transferownership");
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

        Chunk chunk = player.getLocation().getChunk();
        Claim claim = plugin.getClaimManager().getClaim(chunk);

        if (claim == null) {
            plugin.getLocale().getMessage("command.general.notclaimed").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        claim.transferOwnership(newOwner);

        // Send a message to player
        plugin.getLocale().getMessage("command.transferownership.success")
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
        return "ultimateclaims.admin.transferownership";
    }

    @Override
    public String getSyntax() {
        return "admin transferownership <player>";
    }

    @Override
    public String getDescription() {
        return "Transfer the claim your are standing in to another player.";
    }
}
