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

import java.util.List;
import java.util.stream.Collectors;

public class CommandBan extends AbstractCommand {

    public CommandBan(AbstractCommand parent) {
        super(parent, true, "ban");
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
        ClaimMember target = claim.getMember(args[1]);
        OfflinePlayer toBan;

        if(target != null) {
            toBan = target.getPlayer();
        } else {
            // unknown player: double-check
            toBan = Bukkit.getOfflinePlayer(args[1]);

            if (toBan == null || !(toBan.hasPlayedBefore() || toBan.isOnline())) {
                instance.getLocale().getMessage("command.general.noplayer").sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            } else if (player.getUniqueId().equals(toBan.getUniqueId())) {
                instance.getLocale().getMessage("command.kick.notself").sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            }

            // all good!
            target = claim.getMember(toBan.getUniqueId());
        }

        if (toBan.isOnline())
            instance.getLocale().getMessage("command.ban.banned")
                    .processPlaceholder("claim", claim.getName())
                    .sendPrefixedMessage(toBan.getPlayer());

        instance.getLocale().getMessage("command.ban.ban")
                .processPlaceholder("name", toBan.getName())
                .processPlaceholder("claim", claim.getName())
                .sendPrefixedMessage(player);

        if (target != null) {
            claim.removeMember(toBan);
            target.eject();
            if (target.getRole() == ClaimRole.MEMBER)
                instance.getDataManager().deleteMember(target);
        }

        claim.banPlayer(toBan.getUniqueId());
        instance.getDataManager().createBan(claim, toBan.getUniqueId());
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(UltimateClaims instance, CommandSender sender, String... args) {
        if (args.length == 2) {
            final Player player = sender instanceof Player ? (Player) sender : null;
            return Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p != sender
                            && p.getName().toLowerCase().startsWith(args[1].toLowerCase())
                            && (player == null || (player.canSee(p) && p.getMetadata("vanished").isEmpty())))
                    .map(Player::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.ban";
    }

    @Override
    public String getSyntax() {
        return "/c ban <member>";
    }

    @Override
    public String getDescription() {
        return "Ban a member from your claim.";
    }
}
