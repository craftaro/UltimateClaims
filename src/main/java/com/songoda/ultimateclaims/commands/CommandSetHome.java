package com.songoda.ultimateclaims.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.core.commands.AbstractCommand;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandSetHome extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandSetHome(UltimateClaims plugin) {
        super(true, "sethome");
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

        if (!claim.getOwner().getUniqueId().equals(player.getUniqueId())) {
            plugin.getLocale().getMessage("command.general.notyourclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        claim.setHome(player.getLocation());

        plugin.getDataManager().updateClaim(claim);

        plugin.getLocale().getMessage("command.sethome.set").sendPrefixedMessage(sender);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.sethome";
    }

    @Override
    public String getSyntax() {
        return "sethome";
    }

    @Override
    public String getDescription() {
        return "Установить центр поселения.";
    }
}
