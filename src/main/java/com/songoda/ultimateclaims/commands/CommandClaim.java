package com.songoda.ultimateclaims.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimBuilder;
import com.songoda.ultimateclaims.claim.ClaimedChunk;
import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.hooks.WorldGuardHook;
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
import org.bukkit.permissions.PermissionAttachmentInfo;

public class CommandClaim extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandClaim(UltimateClaims plugin) {
        super(true, "claim");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (plugin.getClaimManager().hasClaim(player.getLocation().getChunk())) {
            plugin.getLocale().getMessage("command.general.claimed").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        Chunk chunk = player.getLocation().getChunk();
        Claim claim;

        // firstly, can we even claim this chunk?
        Boolean flag;
        if((flag = WorldGuardHook.getBooleanFlag(chunk, "allow-claims")) != null && !flag) {
            plugin.getLocale().getMessage("command.claim.noregion").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (plugin.getClaimManager().hasClaim(player)) {
            claim = plugin.getClaimManager().getClaim(player);

            if (!claim.getPowerCell().hasLocation()) {
                plugin.getLocale().getMessage("command.claim.nocell").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            if (Setting.CHUNKS_MUST_TOUCH.getBoolean()
                    && !claim.containsChunk(chunk.getWorld().getChunkAt(chunk.getX() + 1, chunk.getZ()))
                    && !claim.containsChunk(chunk.getWorld().getChunkAt(chunk.getX() - 1, chunk.getZ()))
                    && !claim.containsChunk(chunk.getWorld().getChunkAt(chunk.getX(), chunk.getZ() + 1))
                    && !claim.containsChunk(chunk.getWorld().getChunkAt(chunk.getX(), chunk.getZ() - 1))) {
                plugin.getLocale().getMessage("command.claim.nottouching").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            int maxClaimable = Setting.MAX_CHUNKS.getInt();

            // allow permission overrides
            for (PermissionAttachmentInfo perms : player.getEffectivePermissions()) {
                int amount;
                String a;
                if (perms.getPermission().startsWith("ultimateclaims.maxclaims.") 
                        && (a = perms.getPermission().substring("ultimateclaims.maxclaims.".length())).matches("^[0-9]+$")
                        && (amount = Integer.parseInt(a)) > maxClaimable)
                    maxClaimable = amount;
            }

            if (claim.getClaimSize() >= maxClaimable) {
                plugin.getLocale().getMessage("command.claim.toomany")
                        .processPlaceholder("amount", Setting.MAX_CHUNKS.getInt())
                        .sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            ClaimedChunk newChunk = claim.addClaimedChunk(chunk, player);

            plugin.getDataManager().createChunk(newChunk);

            if (plugin.getHologram() != null)
                plugin.getHologram().update(claim.getPowerCell());
        } else {
            claim = new ClaimBuilder()
                    .setOwner(player)
                    .addClaimedChunk(chunk, player)
                    .build();
            plugin.getClaimManager().addClaim(player, claim);

            plugin.getDataManager().createClaim(claim);

            plugin.getLocale().getMessage("command.claim.info")
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

        plugin.getLocale().getMessage("command.claim.success").sendPrefixedMessage(sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) return null;
        if (args.length == 1) {
            List<String> claims = new ArrayList<>();
            for (Claim claim : plugin.getClaimManager().getRegisteredClaims()) {
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
