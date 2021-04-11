package com.songoda.ultimateclaims.claim.region;

import org.bukkit.Chunk;

/**
 * Holds coordinates for a claimed chunk to be used in {@link com.songoda.ultimateclaims.dynmap.DynmapManager}
 */
public class ClaimCorners {
    public final String chunkID;
    public final double[] x, z;

    public ClaimCorners(Chunk chunk, double[] x, double[] z) {
        this.chunkID = chunk.getX() + ";" + chunk.getZ();

        this.x = x;
        this.z = z;
    }
}