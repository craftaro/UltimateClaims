package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimBuilder;
import com.songoda.ultimateclaims.claim.ClaimedChunk;
import com.songoda.ultimateclaims.command.AbstractCommand;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.utils.Methods;
import com.songoda.ultimateclaims.utils.settings.Setting;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;

public class CommandClaim extends AbstractCommand {

    public CommandClaim(AbstractCommand parent) {
        super(parent, true, "claim");
    }

    @Override
    protected ReturnType runCommand(UltimateClaims instance, CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (instance.getClaimManager().hasClaim(player.getLocation().getChunk())) {
            instance.getLocale().getMessage("command.general.claimed").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        Chunk chunk = player.getLocation().getChunk();
        Claim claim;

        if (instance.getClaimManager().hasClaim(player)) {
            claim = instance.getClaimManager().getClaim(player);

            if (!claim.getPowerCell().hasLocation()) {
                instance.getLocale().getMessage("command.claim.nocell").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            if (Setting.CHUNKS_MUST_TOUCH.getBoolean()
                    && !claim.containsChunk(chunk.getWorld().getChunkAt(chunk.getX() + 1, chunk.getZ()))
                    && !claim.containsChunk(chunk.getWorld().getChunkAt(chunk.getX() - 1, chunk.getZ()))
                    && !claim.containsChunk(chunk.getWorld().getChunkAt(chunk.getX(), chunk.getZ() + 1))
                    && !claim.containsChunk(chunk.getWorld().getChunkAt(chunk.getX(), chunk.getZ() - 1))) {
                instance.getLocale().getMessage("command.claim.nottouching").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            if (claim.getClaimSize() >= Setting.MAX_CHUNKS.getInt()) {
                instance.getLocale().getMessage("command.claim.toomany")
                        .processPlaceholder("amount", Setting.MAX_CHUNKS.getInt())
                        .sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            ClaimedChunk newChunk = claim.addClaimedChunk(chunk, player);

            instance.getDataManager().createChunk(newChunk);

            if (instance.getHologram() != null)
                instance.getHologram().update(claim.getPowerCell());
        } else {
            claim = new ClaimBuilder()
                    .setOwner(player)
                    .addClaimedChunk(chunk, player)
                    .build();
            instance.getClaimManager().addClaim(player, claim);

            instance.getDataManager().createClaim(claim);

            instance.getLocale().getMessage("command.claim.info")
                    .processPlaceholder("time", Methods.makeReadable((long) (Setting.STARTING_POWER.getInt() * 60 * 1000)))
                    .sendPrefixedMessage(sender);
        }

        // we've just claimed the chunk we're in, so we've "moved" into the claim
        // Note: Can't use streams here because `Bukkit.getOnlinePlayers()` has a different protoype in legacy
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.getLocation().getChunk().equals(chunk)) {
                ClaimMember member = claim.getMember(p);

                if (member != null)
                    member.setPresent(true);
                else
                    // todo: expunge banned players
                    member = claim.addMember(p, ClaimRole.VISITOR);

                if(Setting.CLAIMS_BOSSBAR.getBoolean()) {
                    if(member.getRole() == ClaimRole.VISITOR) {
                        claim.getVisitorBossBar().addPlayer(p);
                    } else {
                        claim.getMemberBossBar().addPlayer(p);
                    }
                }
            }
        }

        instance.getLocale().getMessage("command.claim.success").sendPrefixedMessage(sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(UltimateClaims instance, CommandSender sender, String... args) {
        if (!(sender instanceof Player)) return null;
        if (args.length == 1) {
            List<String> claims = new ArrayList<>();
            for (Claim claim : instance.getClaimManager().getRegisteredClaims()) {
                if (claim.getMember((Player) sender) == null
                        || claim.getMember((Player) sender).getRole() == ClaimRole.VISITOR) continue;
                claims.add(claim.getName());
            }
            return claims;
        }
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.claim";
    }

    @Override
    public String getSyntax() {
        return "/c claim";
    }

    @Override
    public String getDescription() {
        return "Claim the land you are currently standing in for your claim.";
    }
}
