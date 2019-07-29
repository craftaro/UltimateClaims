package com.songoda.ultimateclaims.claim;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Objects;

public class ClaimedChunk {

    private int id;
    private Claim claim;
    private final String world;
    private final int x;
    private final int z;

    public ClaimedChunk(Chunk chunk) {
        this(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public ClaimedChunk(String world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public Chunk getChunk() {
        World world = Bukkit.getWorld(this.world);
        if (world == null)
            return null;
        return world.getChunkAt(this.x, this.z);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ClaimedChunk) {
            ClaimedChunk other = (ClaimedChunk) o;
            return this.world.equals(other.world) && this.x == other.x && this.z == other.z;
        } else if (o instanceof Chunk) {
            Chunk other = (Chunk) o;
            return this.world.equals(other.getWorld().getName()) && this.x == other.getX() && this.z == other.getZ();
        } else return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.world, this.x, this.z);
    }

}
