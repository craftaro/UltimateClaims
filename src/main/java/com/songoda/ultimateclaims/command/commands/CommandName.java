package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.command.AbstractCommand;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.StringJoiner;

public class CommandName extends AbstractCommand {

    public CommandName(AbstractCommand parent) {
        super(parent, true, "name");
    }

    @Override
    protected ReturnType runCommand(UltimateClaims instance, CommandSender sender, String... args) {

        if (args.length <= 1)
            return ReturnType.SYNTAX_ERROR;

        Player player = (Player) sender;

        Chunk chunk = player.getLocation().getChunk();
        Claim claim = instance.getClaimManager().getClaim(chunk);

        if (claim == null) {
            instance.getLocale().getMessage("command.general.notclaimed").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (!claim.getOwner().getUniqueId().equals(player.getUniqueId())) {
            instance.getLocale().getMessage("command.general.notyourclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        StringJoiner nameTemp = new StringJoiner(" ");
        for (int i = 1; i < args.length; ++i) {
            nameTemp.add(args[i]);
        }
        final String name = nameTemp.toString();

        claim.setName(name);

        instance.getDataManager().updateClaim(claim);

        instance.getLocale().getMessage("command.name.set")
                .processPlaceholder("name", name)
                .sendPrefixedMessage(sender);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(UltimateClaims instance, CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.name";
    }

    @Override
    public String getSyntax() {
        return "/c name";
    }

    @Override
    public String getDescription() {
        return "Set the display name for your claim.";
    }
}
