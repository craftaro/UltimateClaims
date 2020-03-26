package com.songoda.ultimateclaims.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimedChunk;
import com.songoda.ultimateclaims.claim.PowerCell;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandUnClaim extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandUnClaim(UltimateClaims plugin) {
        super(true, "unclaim");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        Chunk chunk = player.getLocation().getChunk();
        Claim claim = plugin.getClaimManager().getClaim(chunk);

        if (claim == null) {
            plugin.getLocale().getMessage("command.general.notclaimed").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (!claim.getOwner().getUniqueId().equals(player.getUniqueId())) {
            plugin.getLocale().getMessage("command.general.notyourclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        if (claim.getPowerCell().hasLocation()) {
            PowerCell powerCell = claim.getPowerCell();
            if (powerCell.getLocation().getChunk() == chunk) {
                plugin.getLocale().getMessage("command.unclaim.powercell").sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
            }
        }

        // we've just unclaimed the chunk we're in, so we've "moved" out of the claim
        // Note: Can't use streams here because `Bukkit.getOnlinePlayers()` has a different protoype in legacy
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().getChunk().equals(chunk)) {
                ClaimMember member = claim.getMember(p);
                if (member != null) {
                    if (member.getRole() == ClaimRole.VISITOR)
                        claim.removeMember(member);
                    else
                        member.setPresent(false);
                }
                if (Settings.CLAIMS_BOSSBAR.getBoolean()) {
                    claim.getVisitorBossBar().removePlayer(p);
                    claim.getMemberBossBar().removePlayer(p);
                }
            }
        }

        // Remove chunk from claim
        ClaimedChunk removedChunk = claim.removeClaimedChunk(chunk, player);
        if (Bukkit.getPluginManager().isPluginEnabled("dynmap"))
            plugin.getDynmapManager().refresh(claim);
        if (claim.getClaimSize() == 0) {
            plugin.getLocale().getMessage("general.claim.dissolve")
                    .processPlaceholder("claim", claim.getName())
                    .sendPrefixedMessage(player);

            // return cash to the player
            double claimBank = claim.getPowerCell().getEconomyBalance();
            if (claimBank > 0) {
                EconomyManager.deposit(player, claimBank);
                plugin.getLocale().getMessage("general.claim.returnfunds")
                        .processPlaceholder("amount", claimBank)
                        .sendPrefixedMessage(player);
            }

            claim.destroy();
        } else {
            plugin.getDataManager().deleteChunk(removedChunk);

            plugin.getLocale().getMessage("command.unclaim.success").sendPrefixedMessage(sender);
        }

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.unclaim";
    }

    @Override
    public String getSyntax() {
        return "unclaim";
    }

    @Override
    public String getDescription() {
        return "Unclaim land from your claim.";
    }
}
