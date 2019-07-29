package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimBuilder;
import com.songoda.ultimateclaims.command.AbstractCommand;
import com.songoda.ultimateclaims.utils.Methods;
import com.songoda.ultimateclaims.utils.settings.Setting;
import com.songoda.ultimateclaims.utils.spigotmaps.MapBuilder;
import com.songoda.ultimateclaims.utils.spigotmaps.rendering.ImageRenderer;
import com.songoda.ultimateclaims.utils.spigotmaps.util.ImageTools;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

public class CommandClaim extends AbstractCommand {

    public CommandClaim(AbstractCommand parent) {
        super("claim", parent, true);
    }

    @Override
    protected ReturnType runCommand(UltimateClaims instance, CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (instance.getClaimManager().hasClaim(player.getLocation().getChunk())) {
            instance.getLocale().getMessage("command.general.claimed").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        Chunk chunk = player.getLocation().getChunk();

        if (instance.getClaimManager().hasClaim(player)) {
            Claim claim = instance.getClaimManager().getClaim(player);

            if (!claim.getPowerCell().hasLocation()) {
                instance.getLocale().getMessage("command.claim.nocell").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            if (Setting.CHUNKS_MUST_TOUCH.getBoolean()
                    && !claim.getClaimedChunks().contains(chunk.getWorld().getChunkAt(chunk.getX() + 1, chunk.getZ()))
                    && !claim.getClaimedChunks().contains(chunk.getWorld().getChunkAt(chunk.getX() - 1, chunk.getZ()))
                    && !claim.getClaimedChunks().contains(chunk.getWorld().getChunkAt(chunk.getX(), chunk.getZ() + 1))
                    && !claim.getClaimedChunks().contains(chunk.getWorld().getChunkAt(chunk.getX(), chunk.getZ() - 1))) {
                instance.getLocale().getMessage("command.claim.nottouching").sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            if (claim.getClaimedChunks().size() >= Setting.MAX_CHUNKS.getInt()) {
                instance.getLocale().getMessage("command.claim.toomany")
                        .processPlaceholder("amount", Setting.MAX_CHUNKS.getInt())
                        .sendPrefixedMessage(player);
                return ReturnType.FAILURE;
            }

            claim.addClaimedChunk(chunk, player);

            if (instance.getHologram() != null)
                instance.getHologram().update(claim.getPowerCell());
        } else {
            instance.getClaimManager().addClaim(player,
                    new ClaimBuilder()
                            .setOwner(player)
                            .addClaimedChunk(chunk, player)
                            .build());

            if (Setting.GIVE_RECIPE_MAP.getBoolean()) {
                try {
                    BufferedImage catImage = ImageIO.read(instance.getResource("recipe.png"));
                    catImage = ImageTools.resizeToMapSize(catImage);
                    ImageRenderer renderer = ImageRenderer.builder()
                            .image(catImage)
                            .build();
                    ItemStack mapItem = MapBuilder.create()
                            .addRenderers(renderer).build().createItemStack();
                    ItemMeta meta = mapItem.getItemMeta();
                    meta.setDisplayName(instance.getLocale().getMessage("general.powercellrecipe").getMessage());
                    mapItem.setItemMeta(meta);
                    Map<Integer, ItemStack> items = player.getInventory().addItem(mapItem);
                    for (ItemStack item : items.values())
                        player.getLocation().getWorld().dropItemNaturally(player.getLocation(), item);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            instance.getLocale().getMessage("command.claim.info")
                    .processPlaceholder("time", Methods.makeReadable((long) (Setting.STARTING_POWER.getInt() * 60 * 1000)))
                    .sendPrefixedMessage(sender);
        }

        instance.getLocale().getMessage("command.claim.success").sendPrefixedMessage(sender);
        return ReturnType.SUCCESS;
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
