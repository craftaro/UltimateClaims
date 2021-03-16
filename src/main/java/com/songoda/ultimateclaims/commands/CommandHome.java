package com.songoda.ultimateclaims.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.core.commands.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandHome extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandHome(UltimateClaims plugin) {
        super(true, "home");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (args.length < 1)
            return ReturnType.SYNTAX_ERROR;

        String claimStr = String.join(" ", args);

        boolean bypass = sender.hasPermission("ultimateclaims.bypass.home");
        Optional<Claim> oClaim = plugin.getClaimManager().getRegisteredClaims().stream()
                .filter(c -> c.getName().toLowerCase().equals(claimStr.toLowerCase())
                        && (bypass || c.isOwnerOrMember(player))).findFirst();

        if (!oClaim.isPresent()) {
            plugin.getLocale().getMessage("command.general.notapartclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }
        Claim claim = oClaim.get();

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
        if (args.length == 1) {
            boolean bypass = sender.hasPermission("ultimateclaims.bypass.home");
            List<String> claims = new ArrayList<>();
            for (Claim claim : plugin.getClaimManager().getRegisteredClaims()) {
                if (!claim.isOwnerOrMember(player) && !bypass) continue;
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
        return "home <claim>";
    }

    @Override
    public String getDescription() {
        return "Go to a claims home.";
    }
}
