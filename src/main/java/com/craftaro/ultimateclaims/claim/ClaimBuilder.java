package com.craftaro.ultimateclaims.claim;

import com.craftaro.ultimateclaims.member.ClaimPermissions;
import com.craftaro.ultimateclaims.member.ClaimRole;
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

    public ClaimBuilder setOwner(Player player) {
        claim.setOwner(player.getUniqueId()).setName(player.getName());
        if (claim.getName() == null)
            claim.setName(claim.getDefaultName());
        return this;
    }

    public ClaimBuilder addMembers(Player... players) {
        for (Player player : players)
            this.claim.addMember(player, ClaimRole.MEMBER);
        return this;
    }

    public ClaimBuilder addClaimedChunk(Chunk chunk, Player player) {
        this.claim.addClaimedChunk(chunk, player);
        return this;
    }

    public ClaimBuilder setPowerCell(PowerCell powerCell) {
        this.claim.setPowerCell(powerCell);
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

    public ClaimBuilder setLocked(boolean locked) {
        this.setLocked(locked);
        return this;
    }

    public ClaimBuilder setHome(Location home) {
        this.setHome(home);
        return this;
    }



    public Claim build() {
        return this.claim;
    }
}
