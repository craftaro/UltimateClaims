package com.songoda.ultimateclaims.claim.region;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;

import java.util.*;

public class ClaimedRegion {

    private UUID uniqueId = UUID.randomUUID();
    private final Claim claim;

    private final Set<ClaimedChunk> claimedChunks = new HashSet<>();

    public ClaimedRegion(Claim claim) {
        this.claim = claim;
    }

    public ClaimedRegion(ClaimedChunk newChunk, Claim claim) {
        this.claim = claim;
        claimedChunks.add(newChunk);
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
        return claim;
    }

    public void addChunk(ClaimedChunk chunk) {
        claimedChunks.add(chunk);
        chunk.setRegion(this);
    }

    public void addChunks(Set<ClaimedChunk> chunks) {
        for (ClaimedChunk chunk : chunks)
            if (!claimedChunks.contains(chunk))
                addChunk(chunk);
    }

    public List<ClaimedRegion> removeChunk(ClaimedChunk chunk) {
        if (claimedChunks.remove(chunk)) {
            List<ClaimedRegion> newRegions = new LinkedList<>();
            List<ClaimedChunk> toSearch = chunk.getAttachedChunks();
            Set<ClaimedChunk> scanned = new HashSet<>();
            for (ClaimedChunk claimedChunk : new LinkedList<>(toSearch)) {
                if (scanned.contains(claimedChunk)) continue;
                ClaimedChunk masterChunk = toSearch.get(0);

                Set<ClaimedChunk> searchedChunks = new LinkedHashSet<>();
                List<ClaimedChunk> nextChunks = new LinkedList<>(masterChunk.getAttachedChunks());
                nextChunks.add(masterChunk);
                boolean done = false;
                while (!done) {
                    ClaimedChunk currentChunk = nextChunks.get(0);
                    nextChunks.remove(currentChunk);
                    searchedChunks.add(currentChunk);
                    for (ClaimedChunk potentialChunk : currentChunk.getAttachedChunks()) {
                        if (!searchedChunks.contains(potentialChunk)) {
                            nextChunks.add(potentialChunk);
                        }
                    }
                    if (nextChunks.isEmpty())
                        done = true;
                }
                if (searchedChunks.containsAll(toSearch) && newRegions.isEmpty())
                    return new ArrayList<>();
                toSearch.remove(0);
                scanned.addAll(searchedChunks);
                ClaimedRegion region = new ClaimedRegion(claim);
                if (!newRegions.contains(this)) {
                    region = this;
                    claimedChunks.clear();
                }
                newRegions.add(region);
                for (ClaimedChunk searchedChunk : searchedChunks) {
                    searchedChunk.setRegion(region);
                    region.addChunk(searchedChunk);
                }
                if (region != this)
                    UltimateClaims.getInstance().getDataManager().createClaimedRegion(region);
            }
            UltimateClaims.getInstance().getDataManager().updateClaimedChunks(scanned);
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
        return Collections.unmodifiableSet(claimedChunks);
    }

    public UUID getUniqueId() {
        return uniqueId;
    }
}
