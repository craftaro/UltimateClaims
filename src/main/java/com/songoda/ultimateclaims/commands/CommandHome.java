package com.songoda.ultimateclaims.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.core.library.commands.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandHome extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandHome(UltimateClaims plugin, AbstractCommand parent) {
        super(parent, true, "home");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (args.length < 2)
            return ReturnType.SYNTAX_ERROR;

        StringBuilder claimBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            String line = args[i];
            claimBuilder.append(line).append(" ");
        }
        String claimStr = claimBuilder.toString().trim();

        Optional oClaim = plugin.getClaimManager().getRegisteredClaims().stream()
                .filter(c -> c.getName().toLowerCase().equals(claimStr.toLowerCase())
                        && c.getMember(player) != null).findFirst();

        if (!oClaim.isPresent() && !sender.hasPermission("ultimateclaims.bypass")) {
            plugin.getLocale().getMessage("command.general.notapartclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }
        Claim claim = (Claim) oClaim.get();

        if (claim.getHome() == null) {
            plugin.getLocale().getMessage("command.home.none").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        player.teleport(claim.getHome());

        plugin.getLocale().getMessage("command.home.success")
                .sendPrefixedMessage(player);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) return null;
        Player player = ((Player) sender);
        if (args.length == 2) {
            List<String> claims = new ArrayList<>();
            for (Claim claim : plugin.getClaimManager().getRegisteredClaims()) {
                if (!claim.isOwnerOrMember(player) && !sender.hasPermission("ultimateclaims.bypass")) continue;
                claims.add(claim.getName());
            }
            return claims;
        }
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.home";
    }

    @Override
    public String getSyntax() {
        return "/c home <claim>";
    }

    @Override
    public String getDescription() {
        return "Go to a claims home.";
    }
}
