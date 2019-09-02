package com.songoda.ultimateclaims.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.core.commands.AbstractCommand;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.settings.Setting;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class CommandDissolve extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandDissolve(UltimateClaims plugin) {
        super(true, "dissolve");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (!plugin.getClaimManager().hasClaim(player)) {
            plugin.getLocale().getMessage("command.general.noclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Claim claim = plugin.getClaimManager().getClaim(player);

        // we've just unclaimed the chunk we're in, so we've "moved" out of the claim
        for(ClaimMember m : claim.getOwnerAndMembers()) {
            OfflinePlayer p = m.getPlayer();
            if(!p.isOnline()) continue;

            Claim playerClaim = plugin.getClaimManager().getClaim(((Player) p).getLocation().getChunk());
            if(playerClaim != null && playerClaim == claim) {
                ClaimMember member = claim.getMember(p);
                if (member != null) {
                    if (member.getRole() == ClaimRole.VISITOR)
                        claim.removeMember(member);
                    else
                        member.setPresent(false);
                }
                if(Setting.CLAIMS_BOSSBAR.getBoolean()) {
                    claim.getVisitorBossBar().removePlayer((Player) p);
                    claim.getMemberBossBar().removePlayer((Player) p);
                }
            }
        }

        claim.destroy();
        plugin.getLocale().getMessage("general.claim.dissolve")
                .processPlaceholder("claim", claim.getName())
                .sendPrefixedMessage(player);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.dissolve";
    }

    @Override
    public String getSyntax() {
        return "/c dissolve";
    }

    @Override
    public String getDescription() {
        return "Dissolve your claim.";
    }
}
