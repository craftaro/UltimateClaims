package com.songoda.ultimateclaims.claim.region;

import org.bukkit.Chunk;
import org.bukkit.World;

/**
 * Holds coordinates for a claimed chunk to be used in {@link com.songoda.ultimateclaims.dynmap.DynmapManager}
 */
public class ClaimCorners {
    private final Chunk chunk;

    public final String chunkID;
    public final double[] x, z;

    public ClaimCorners(Chunk chunk, double[] x, double[] z) {
        this.chunk = chunk;

        this.chunkID = chunk.getX() + ";" + chunk.getZ();
        this.x = x;
        this.z = z;
    }

    public World getWorld() {
        return this.chunk.getWorld();
    }
}
