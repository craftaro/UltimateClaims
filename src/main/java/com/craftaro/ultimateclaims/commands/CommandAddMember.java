package com.craftaro.ultimateclaims.commands;

import com.craftaro.ultimateclaims.UltimateClaims;
import com.craftaro.ultimateclaims.api.events.ClaimMemberAddEvent;
import com.craftaro.ultimateclaims.claim.Claim;
import com.craftaro.core.commands.AbstractCommand;
import com.craftaro.core.utils.PlayerUtils;
import com.craftaro.ultimateclaims.member.ClaimMember;
import com.craftaro.ultimateclaims.member.ClaimRole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandAddMember extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandAddMember(UltimateClaims plugin) {
        super(CommandType.PLAYER_ONLY, "addmember");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (args.length < 1)
            return ReturnType.SYNTAX_ERROR;

        if (!plugin.getClaimManager().hasClaim(player)) {
            plugin.getLocale().getMessage("command.general.noclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Claim claim = plugin.getClaimManager().getClaim(player);

        OfflinePlayer toInvite = Bukkit.getOfflinePlayer(args[0]);

        if (!(toInvite.hasPlayedBefore() || toInvite.isOnline())) {
            plugin.getLocale().getMessage("command.general.noplayer").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (player.getUniqueId().equals(toInvite.getUniqueId())) {
            plugin.getLocale().getMessage("command.invite.notself").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (claim.getMembers().stream()
                .filter(m -> m.getRole() == ClaimRole.MEMBER)
                .anyMatch(m -> m.getUniqueId().equals(toInvite.getUniqueId()))) {
            plugin.getLocale().getMessage("command.invite.already").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        ClaimMemberAddEvent event = new ClaimMemberAddEvent(claim, toInvite);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return ReturnType.FAILURE;
        }

        ClaimMember newMember = claim.addMember(toInvite, ClaimRole.MEMBER);
        plugin.getDataManager().createMember(newMember);

        if(toInvite.isOnline())
            plugin.getLocale().getMessage("command.addmember.added")
                    .processPlaceholder("claim", claim.getName())
                    .sendPrefixedMessage(toInvite.getPlayer());

        plugin.getLocale().getMessage("command.addmember.add")
                .processPlaceholder("name", toInvite.getName())
                .sendPrefixedMessage(player);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) {
            return PlayerUtils.getVisiblePlayerNames(sender, args[0]);
        }
        return null;
    }


    @Override
    public String getPermissionNode() {
        return "ultimateclaims.addmember";
    }

    @Override
    public String getSyntax() {
        return "addmember <player>";
    }

    @Override
    public String getDescription() {
        return "Add a player to access your claim.";
    }
}
