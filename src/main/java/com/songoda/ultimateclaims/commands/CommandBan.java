package com.songoda.ultimateclaims.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.core.library.commands.AbstractCommand;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimRole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CommandBan extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandBan(UltimateClaims plugin, AbstractCommand parent) {
        super(parent, true, "ban");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (args.length < 2)
            return ReturnType.SYNTAX_ERROR;

        if (!plugin.getClaimManager().hasClaim(player)) {
            plugin.getLocale().getMessage("command.general.noclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Claim claim = plugin.getClaimManager().getClaim(player);
        ClaimMember target = claim.getMember(args[1]);
        OfflinePlayer toBan;

        if(target != null) {
            toBan = target.getPlayer();
        } else {
            // unknown player: double-check
            toBan = Bukkit.getOfflinePlayer(args[1]);

            if (toBan == null || !(toBan.hasPlayedBefore() || toBan.isOnline())) {
                plugin.getLocale().getMessage("command.general.noplayer").sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            } else if (player.getUniqueId().equals(toBan.getUniqueId())) {
                plugin.getLocale().getMessage("command.kick.notself").sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            }

            // all good!
            target = claim.getMember(toBan.getUniqueId());
        }

        if (toBan.isOnline())
            plugin.getLocale().getMessage("command.ban.banned")
                    .processPlaceholder("claim", claim.getName())
                    .sendPrefixedMessage(toBan.getPlayer());

        plugin.getLocale().getMessage("command.ban.ban")
                .processPlaceholder("name", toBan.getName())
                .processPlaceholder("claim", claim.getName())
                .sendPrefixedMessage(player);

        if (target != null) {
            claim.removeMember(toBan);
            target.eject();
            if (target.getRole() == ClaimRole.MEMBER)
                plugin.getDataManager().deleteMember(target);
        }

        claim.banPlayer(toBan.getUniqueId());
        plugin.getDataManager().createBan(claim, toBan.getUniqueId());
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
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
