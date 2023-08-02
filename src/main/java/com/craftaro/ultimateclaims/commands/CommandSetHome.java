package com.craftaro.ultimateclaims.commands;

import com.craftaro.ultimateclaims.UltimateClaims;
import com.craftaro.ultimateclaims.claim.Claim;
import com.craftaro.core.commands.AbstractCommand;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandSetHome extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandSetHome(UltimateClaims plugin) {
        super(CommandType.PLAYER_ONLY, "sethome");
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

        plugin.getDataHelper().updateClaim(claim);

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
        return "Set the home for your claim.";
    }
}
