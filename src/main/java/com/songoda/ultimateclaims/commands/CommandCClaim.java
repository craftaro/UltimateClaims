package com.songoda.ultimateclaims.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.hooks.WorldGuardHook;
import com.songoda.core.utils.NumberUtils;
import com.songoda.core.utils.TimeUtils;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.api.events.ClaimChunkClaimEvent;
import com.songoda.ultimateclaims.api.events.ClaimCreateEvent;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimBuilder;
import com.songoda.ultimateclaims.claim.region.ClaimedChunk;
import com.songoda.ultimateclaims.claim.region.ClaimedRegion;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandCClaim extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandCClaim(UltimateClaims plugin) {
        super(true, "cclaim");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (Settings.DISABLED_WORLDS.getStringList().contains(player.getWorld().getName())) {
            plugin.getLocale().getMessage("command.claim.disabledworld").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        Chunk centerChunk = player.getLocation().getChunk();
        Claim claim;

        // firstly, can we even claim this chunk?
        Boolean flag;
        if ((flag = WorldGuardHook.getBooleanFlag(centerChunk, "allow-claims")) != null && !flag) {
            plugin.getLocale().getMessage("command.claim.noregion").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (plugin.getClaimManager().hasClaim(player)) {
            claim = plugin.getClaimManager().getClaim(player);

            if (!claim.getPowerCell().hasLocation()) {
                plugin.getLocale().getMessage("command.claim.nocell").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            ClaimedRegion region = claim.getPotentialRegion(centerChunk);

            if (Settings.CHUNKS_MUST_TOUCH.getBoolean() && region == null) {
                plugin.getLocale().getMessage("command.claim.nottouching").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            int maxClaimable = claim.getMaxClaimSize(player);

            if (claim.getClaimSize() >= maxClaimable) {
                plugin.getLocale().getMessage("command.claim.toomany")
                        .processPlaceholder("amount", maxClaimable)
                        .sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            ClaimChunkClaimEvent event = new ClaimChunkClaimEvent(claim, centerChunk);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return ReturnType.FAILURE;
            }

            String radiuss = String.join(" ", args);

            // value - number?
            if (!NumberUtils.isNumeric(radiuss)) {
                plugin.getLocale().getMessage("command.claim.notanumber")
                        .sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            int radius = Integer.parseInt(radiuss);

            // value 1-10 ?
            if ((radius < 1) || (radius > 10)) {
                plugin.getLocale().getMessage("command.claim.incorrectnumber")
                        .sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            List<Chunk> getChunks;
            {
                // start radius cuboid match
                List<Chunk> chunks = new ArrayList<>();
                for (int x = centerChunk.getX() - radius; x < centerChunk.getX() + radius +1; x++) {
                    for (int z = centerChunk.getZ() - radius; z < centerChunk.getZ() + radius +1; z++) {
                        Chunk chunk = centerChunk.getWorld().getChunkAt(x, z);
                        // skip claimed chunks
                        if (!plugin.getClaimManager().hasClaim(chunk)) {

                            //DEV TODO REMOVE
                            Bukkit.getLogger().info("Create chunk:" + chunk);

                            // start save logic
                            boolean newRegion = claim.isNewRegion(chunk);

                            // check max region limit
                            if (newRegion && claim.getClaimedRegions().size() >= Settings.MAX_REGIONS.getInt()) {
                                plugin.getLocale().getMessage("command.claim.maxregions").sendPrefixedMessage(sender);
                                return ReturnType.FAILURE;
                            }

                            claim.addClaimedChunk(chunk, player);
                            ClaimedChunk claimedChunk = claim.getClaimedChunk(chunk);
                            plugin.getDataManager().createClaimedChunk(claimedChunk);

                            if (newRegion) {
                                plugin.getDataManager().createClaimedRegion(claimedChunk.getRegion());

                                if (Bukkit.getPluginManager().isPluginEnabled("dynmap"))
                                    plugin.getDynmapManager().refresh(claim);

                                if (Settings.POWERCELL_HOLOGRAMS.getBoolean())
                                    claim.getPowerCell().updateHologram();
                            }
                        }
                    }
                }
            }

        } else {
            claim = new ClaimBuilder()
                    .setOwner(player)
                    .addClaimedChunk(centerChunk, player)
                    .build();

            ClaimCreateEvent event = new ClaimCreateEvent(claim);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return ReturnType.FAILURE;
            }

            plugin.getClaimManager().addClaim(player, claim);
            if (Bukkit.getPluginManager().isPluginEnabled("dynmap"))
                plugin.getDynmapManager().refresh(claim);

            plugin.getDataManager().createClaim(claim);

            plugin.getLocale().getMessage("command.claim.info")
                    .processPlaceholder("time", TimeUtils.makeReadable((long) (Settings.STARTING_POWER.getInt() * 60 * 1000)))
                    .sendPrefixedMessage(sender);
        }

        // we've just claimed the chunk we're in, so we've "moved" into the claim
        // Note: Can't use streams here because `Bukkit.getOnlinePlayers()` has a different protoype in legacy
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().getChunk().equals(centerChunk)) {
                ClaimMember member = claim.getMember(p);

                if (member != null)
                    member.setPresent(true);
                else
                    // todo: expunge banned players
                    member = claim.addMember(p, ClaimRole.VISITOR);

                if (Settings.CLAIMS_BOSSBAR.getBoolean()) {
                    if (member.getRole() == ClaimRole.VISITOR) {
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
        return "ultimateclaims.cclaim";
    }

    @Override
    public String getSyntax() {
        return "cclaim <радиус>";
    }

    @Override
    public String getDescription() {
        return "Расширить поселение по радиусу - кубоид.";
    }
}
