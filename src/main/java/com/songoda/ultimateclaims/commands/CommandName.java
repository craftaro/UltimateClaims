package com.songoda.ultimateclaims.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.core.commands.AbstractCommand;
import com.songoda.ultimateclaims.settings.Settings;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandName extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandName(UltimateClaims plugin) {
        super(CommandType.PLAYER_ONLY, "name");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length < 1)
            return ReturnType.SYNTAX_ERROR;

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

        final String name = String.join(" ", args);

        if (name.length() > Settings.NAME_CHAR_LIMIT.getInt()) {
            plugin.getLocale().getMessage("command.name.toolong")
                    .processPlaceholder("max", Settings.NAME_CHAR_LIMIT.getInt())
                    .sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }


        claim.setName(name);

        plugin.getDataManager().updateClaim(claim);

        plugin.getLocale().getMessage("command.name.set")
                .processPlaceholder("name", name)
                .sendPrefixedMessage(sender);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.name";
    }

    @Override
    public String getSyntax() {
        return "name <name>";
    }

    @Override
    public String getDescription() {
        return "Set the display name for your claim.";
    }
}
