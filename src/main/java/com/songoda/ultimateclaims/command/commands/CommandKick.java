package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.command.AbstractCommand;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimRole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandKick extends AbstractCommand {

    public CommandKick(AbstractCommand parent) {
        super(parent, true, "kick");
    }

    @Override
    protected ReturnType runCommand(UltimateClaims instance, CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (args.length < 2)
            return ReturnType.SYNTAX_ERROR;

        if (!instance.getClaimManager().hasClaim(player)) {
            instance.getLocale().getMessage("command.general.noclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Claim claim = instance.getClaimManager().getClaim(player);

        OfflinePlayer toKick = Bukkit.getPlayer(args[1]);

        if (toKick == null) {
            instance.getLocale().getMessage("command.general.noplayer").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (claim.getMember(toKick.getUniqueId()) == null) {
            instance.getLocale().getMessage("command.general.notinclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (player.getUniqueId() == toKick.getUniqueId()) {
            instance.getLocale().getMessage("command.kick.notself").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (toKick.isOnline())
            instance.getLocale().getMessage("command.kick.kicked")
                    .processPlaceholder("claim", toKick.getName())
                    .sendPrefixedMessage(toKick.getPlayer());

        instance.getLocale().getMessage("command.kick.kick")
                .processPlaceholder("name", toKick.getName())
                .processPlaceholder("claim", claim.getName())
                .sendPrefixedMessage(player);
        claim.removeMember(toKick.getUniqueId());
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(UltimateClaims instance, CommandSender sender, String... args) {
        if (!(sender instanceof Player)) return null;
        Player player = ((Player) sender);
        if (args.length == 2) {
            List<String> claims = new ArrayList<>();
            for (Claim claim : instance.getClaimManager().getRegisteredClaims()) {
                if (!claim.isOwnerOrMember(player)) continue;
                claims.add(claim.getName());
            }
            return claims;
        }
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.kick";
    }

    @Override
    public String getSyntax() {
        return "/c kick <member>";
    }

    @Override
    public String getDescription() {
        return "Kick a member from your claim.";
    }
}
