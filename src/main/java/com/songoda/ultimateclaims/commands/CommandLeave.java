package com.songoda.ultimateclaims.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.core.library.commands.AbstractCommand;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimRole;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandLeave extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandLeave(UltimateClaims plugin, AbstractCommand parent) {
        super(parent, true, "leave");
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

        Optional<Claim> oClaim = plugin.getClaimManager().getRegisteredClaims().stream()
                .filter(c -> c.getName().toLowerCase().equals(claimStr.toLowerCase())
                        && c.getMember(player) != null).findFirst();

        if (!oClaim.isPresent()) {
            plugin.getLocale().getMessage("command.general.notapartclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (player.getUniqueId().equals((oClaim.get()).getOwner().getUniqueId())) {
            plugin.getLocale().getMessage("command.leave.owner").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Claim claim = oClaim.get();
        ClaimMember memberToRemove = claim.getMember(player);

        plugin.getDataManager().deleteMember(memberToRemove);

        claim.removeMember(player);

        plugin.getLocale().getMessage("command.leave.youleft")
                .processPlaceholder("claim", claim.getName())
                .sendPrefixedMessage(player);

        for (ClaimMember member : claim.getMembers())
            this.notify(member);
        this.notify(claim.getOwner());

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) return null;
        Player player = ((Player) sender);
        if (args.length == 2) {
            List<String> claims = new ArrayList<>();
            for (Claim claim : plugin.getClaimManager().getRegisteredClaims()) {
                if (!claim.isOwnerOrMember(player)) continue;
                claims.add(claim.getName());
            }
            return claims;
        }
        return null;
    }

    private void notify(ClaimMember member) {
        Player player = Bukkit.getPlayer(member.getUniqueId());
        if (player != null)
            plugin.getLocale().getMessage("command.leave.left")
                    .processPlaceholder("player", player.getName())
                    .processPlaceholder("claim", member.getClaim().getName())
                    .sendPrefixedMessage(player);
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.leave";
    }

    @Override
    public String getSyntax() {
        return "/c leave <claim>";
    }

    @Override
    public String getDescription() {
        return "Leave a claim that you are a member of.";
    }
}
