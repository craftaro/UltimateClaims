package com.songoda.ultimateclaims.claim;

import com.songoda.ultimateclaims.member.ClaimPermissions;
import com.songoda.ultimateclaims.member.ClaimRole;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClaimBuilder {

    private final Claim claim;

    public ClaimBuilder() {
        this.claim = new Claim();
    }

    public ClaimBuilder setName(String name) {
        this.claim.setName(name);
        return this;
    }

    public ClaimBuilder setOwner(UUID owner) {
        this.claim.setOwner(owner);
        return this;
    }

    public ClaimBuilder setOwner(Player player) {
        return this.setOwner(player.getUniqueId());
    }

    public ClaimBuilder addMembers(UUID... uuids) {
        for (UUID uuid : uuids)
            this.claim.addMember(uuid, ClaimRole.MEMBER);
        return this;
    }

    public ClaimBuilder addClaimedChunks(Chunk... chunks) {
        for (Chunk chunk : chunks)
            this.claim.addClaimedChunk(chunk);
        return this;
    }

    public ClaimBuilder setPowerCell(Location location) {
        this.claim.setPowerCell(location);
        return this;
    }

    public ClaimBuilder setMemberPermissions(ClaimPermissions memberPermissions) {
        this.claim.setMemberPermissions(memberPermissions);
        return this;
    }

    public ClaimBuilder setVisitorPermissions(ClaimPermissions visitorPermissions) {
        this.claim.setVisitorPermissions(visitorPermissions);
        return this;
    }

    public ClaimBuilder banPlayer(UUID... uuids) {
        for (UUID uuid : uuids)
            this.claim.banPlayer(uuid);
        return this;
    }

    public Claim build() {
        return this.claim;
    }
}
