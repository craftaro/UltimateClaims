package com.craftaro.ultimateclaims.claim.region;

import com.craftaro.ultimateclaims.UltimateClaims;
import com.craftaro.ultimateclaims.claim.Claim;
import com.craftaro.ultimateclaims.claim.PowerCell;

import java.util.*;

public class ClaimedRegion {
    private UUID uniqueId = UUID.randomUUID();
    private final Claim claim;

    private final Set<ClaimedChunk> claimedChunks = new HashSet<>();

    // Add this:
    private PowerCell powerCell;

    public ClaimedRegion(Claim claim) {
        this.claim = claim;
    }

    public ClaimedRegion(ClaimedChunk newChunk, Claim claim) {
        this.claim = claim;
        this.claimedChunks.add(newChunk);
        newChunk.setRegion(this);
    }

    public ClaimedRegion(ClaimedChunk newChunk, UUID uniqueId, Claim claim) {
        this(newChunk, claim);
        this.uniqueId = uniqueId;
    }

    public ClaimedRegion(UUID uniqueId, Claim claim) {
        this.uniqueId = uniqueId;
        this.claim = claim;
    }

    public Claim getClaim() {
        return this.claim;
    }

    public void addChunk(ClaimedChunk chunk) {
        this.claimedChunks.add(chunk);
        chunk.setRegion(this);
    }

    public void addChunks(Set<ClaimedChunk> chunks) {
        for (ClaimedChunk chunk : chunks) {
            if (!this.claimedChunks.contains(chunk)) {
                addChunk(chunk);
            }
        }
    }

    public List<ClaimedRegion> removeChunk(ClaimedChunk chunk) {
        if (this.claimedChunks.remove(chunk)) {
            List<ClaimedRegion> newRegions = new LinkedList<>();
            List<ClaimedChunk> toSearch = new ArrayList<>(this.claimedChunks);
            Set<ClaimedChunk> scanned = new HashSet<>();
            while (!toSearch.isEmpty()) {
                ClaimedChunk masterChunk = toSearch.get(0);

                Set<ClaimedChunk> searchedChunks = new LinkedHashSet<>();
                List<ClaimedChunk> nextChunks = new LinkedList<>();
                nextChunks.add(masterChunk);
                boolean done = false;
                while (!done) {
                    ClaimedChunk currentChunk = nextChunks.get(0);
                    nextChunks.remove(currentChunk);
                    searchedChunks.add(currentChunk);
                    for (ClaimedChunk potentialChunk : currentChunk.getAttachedChunks()) {
                        if (!searchedChunks.contains(potentialChunk) && this.claimedChunks.contains(potentialChunk)) {
                            nextChunks.add(potentialChunk);
                        }
                    }
                    if (nextChunks.isEmpty()) {
                        done = true;
                    }
                }
                toSearch.removeAll(searchedChunks);
                scanned.addAll(searchedChunks);
                ClaimedRegion region = new ClaimedRegion(this.claim);
                if (!newRegions.contains(this)) {
                    region = this;
                    this.claimedChunks.clear();
                }
                newRegions.add(region);
                for (ClaimedChunk searchedChunk : searchedChunks) {
                    searchedChunk.setRegion(region);
                    region.addChunk(searchedChunk);
                }
                // Handle power cells during region splitting
                if (this.powerCell != null && region == this) {
                    region.setPowerCell(this.powerCell);
                } else if (region != this) {
                    region.setPowerCell(null);
                }
                if (region != this) {
                    UltimateClaims.getInstance().getDataHelper().createClaimedRegion(region);
                }
            }
            UltimateClaims.getInstance().getDataHelper().updateClaimedChunks(scanned);
            return newRegions;
        }
        return new ArrayList<>();
    }

    public ClaimedChunk getFirstClaimedChunk() {
        return this.claimedChunks.iterator().next();
    }

    public boolean containsChunk(String world, int chunkX, int chunkZ) {
        return this.claimedChunks.stream().anyMatch(x -> x.getWorld().equals(world) && x.getX() == chunkX && x.getZ() == chunkZ);
    }

    public Set<ClaimedChunk> getChunks() {
        return Collections.unmodifiableSet(this.claimedChunks);
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public PowerCell getPowerCell() {
        return this.powerCell;
    }

    public void setPowerCell(PowerCell powerCell) {
        this.powerCell = powerCell;
    }

    public boolean hasPowerCell() {
        return this.powerCell != null && this.powerCell.hasLocation();
    }
}
