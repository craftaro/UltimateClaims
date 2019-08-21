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

public class CommandKick extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandKick(UltimateClaims plugin, AbstractCommand parent) {
        super(parent, true, "kick");
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
        OfflinePlayer toKick;

        if(target != null) {
            toKick = target.getPlayer();
        } else {
            // unknown player: double-check
            toKick = Bukkit.getOfflinePlayer(args[1]);

            if (toKick == null || !(toKick.hasPlayedBefore() || toKick.isOnline())) {
                plugin.getLocale().getMessage("command.general.noplayer").sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            } else if (player.getUniqueId().equals(toKick.getUniqueId())) {
                plugin.getLocale().getMessage("command.kick.notself").sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            }

            // all good!
            target = claim.getMember(toKick.getUniqueId());
        }

        if (target == null || target.getRole() != ClaimRole.MEMBER) {
            plugin.getLocale().getMessage("command.general.notinclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (toKick.isOnline())
            plugin.getLocale().getMessage("command.kick.kicked")
                    .processPlaceholder("claim", claim.getName())
                    .sendPrefixedMessage(toKick.getPlayer());

        plugin.getLocale().getMessage("command.kick.kick")
                .processPlaceholder("name", toKick.getName())
                .processPlaceholder("claim", claim.getName())
                .sendPrefixedMessage(player);

        // and YEET!
        target.setRole(ClaimRole.VISITOR);
        plugin.getDataManager().deleteMember(target);
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
