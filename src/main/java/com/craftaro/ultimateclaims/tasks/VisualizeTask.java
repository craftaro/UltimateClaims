package com.craftaro.ultimateclaims.tasks;

import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.utils.ReflectionUtils;
import com.craftaro.ultimateclaims.UltimateClaims;
import com.craftaro.ultimateclaims.claim.Claim;
import com.craftaro.ultimateclaims.claim.ClaimManager;
import com.craftaro.ultimateclaims.claim.region.ClaimedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class VisualizeTask extends BukkitRunnable {
    private static VisualizeTask instance;
    private static UltimateClaims plugin;
    private static final Map<OfflinePlayer, Boolean> ACTIVE = new ConcurrentHashMap<>();
    private static final Random RANDOM = new Random();

    private static final Color[] COLORS = new Color[]{
            Color.fromRGB(0, 255, 0),    // Green
            Color.fromRGB(0, 0, 255),    // Blue
            Color.fromRGB(255, 255, 0),  // Yellow
            Color.fromRGB(255, 0, 255),  // Magenta
            Color.fromRGB(0, 255, 255),  // Cyan
            Color.fromRGB(255, 165, 0),  // Orange
            Color.fromRGB(128, 0, 128),  // Purple
            Color.fromRGB(128, 128, 0),  // Olive
            Color.fromRGB(0, 128, 128),  // Teal
            Color.fromRGB(0, 0, 128)     // Navy
    };

    private static final Map<ClaimedRegion, Color> regionColors = new ConcurrentHashMap<>();
    private static final AtomicInteger colorIndex = new AtomicInteger(0);

    int radius;

    public VisualizeTask(UltimateClaims plug) {
        plugin = plug;
        this.radius = Bukkit.getServer().getViewDistance();
    }

    public static VisualizeTask startTask(UltimateClaims plug) {
        plugin = plug;
        if (instance == null) {
            instance = new VisualizeTask(plugin);
            instance.runTaskTimerAsynchronously(plugin, 60, 10);
        }
        return instance;
    }

    public static boolean togglePlayer(Player p) {
        Boolean isActive = ACTIVE.get(p);
        ACTIVE.put(p, isActive = (isActive == null || !isActive));
        return isActive;
    }

    public static void removePlayer(Player p) {
        ACTIVE.remove(p);
    }

    @Override
    public void run() {
        ACTIVE.entrySet().stream()
                .filter(e -> e.getValue() && e.getKey().isOnline())
                .forEach(e -> particleTick((Player) e.getKey()));
    }

    void particleTick(Player player) {
        final ClaimManager claimManager = plugin.getClaimManager();
        final Location playerLocation = player.getLocation();
        final World world = playerLocation.getWorld();
        int startY = playerLocation.getBlockY() + 1;
        int cxi = (playerLocation.getBlockX() >> 4) - this.radius, cxn = cxi + this.radius * 2;
        int czi = (playerLocation.getBlockZ() >> 4) - this.radius, czn = czi + this.radius * 2;

        for (int cx = cxi; cx < cxn; cx++) {
            for (int cz = czi; cz < czn; cz++) {
                if (!world.isChunkLoaded(cx, cz)) {
                    continue;
                }

                Claim claim = claimManager.getClaim(world.getName(), cx, cz);
                if (claim != null) {
                    Chunk chunk = world.getChunkAt(cx, cz);
                    ClaimedRegion region = claim.getClaimedRegion(chunk);

                    // Assign color to region if not already assigned
                    Color color = regionColors.computeIfAbsent(region, r -> {
                        int idx = colorIndex.getAndIncrement() % COLORS.length;
                        return COLORS[idx];
                    });

                    showChunkParticles(player, chunk, startY, color);
                }
            }
        }
    }

    void showChunkParticles(Player player, Chunk chunk, int startY, Color color) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (RANDOM.nextFloat() >= .2) {
                    continue;
                }

                if (startY >= chunk.getWorld().getMaxHeight()) {
                    continue;
                }

                Block b = chunk.getBlock(x, startY, z);
                int maxDown = 8;
                boolean show;
                do {
                    show = b.getType().isTransparent() && !(b = b.getRelative(BlockFace.DOWN)).getType().isTransparent();
                } while (--maxDown > 0 && !show);

                if (show) {
                    final Location loc = b.getLocation().add(.5, 1.5, .5);

                    if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
                        player.spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 0, new Particle.DustOptions(color, 2F));
                    } else if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
                        float red = color.getRed() / 255.0F;
                        float green = color.getGreen() / 255.0F;
                        float blue = color.getBlue() / 255.0F;
                        player.spawnParticle(Particle.REDSTONE, loc, 0, red, green, blue, 1.0);
                    } else if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_8)) {
                        try {
                            float red = color.getRed() / 255.0F;
                            float green = color.getGreen() / 255.0F;
                            float blue = color.getBlue() / 255.0F;
                            Object[] data = new Object[]{loc, Effect.valueOf("COLOURED_DUST"), 30, 2, red, green, blue, 1.0F, 1, 16};
                            ReflectionUtils.invokePrivateMethod(player.spigot().getClass(), "playEffect", player.spigot(),
                                    new Class[]{Location.class,
                                            Effect.class,
                                            int.class,
                                            int.class,
                                            float.class,
                                            float.class,
                                            float.class,
                                            float.class,
                                            int.class,
                                            int.class},
                                    data);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
