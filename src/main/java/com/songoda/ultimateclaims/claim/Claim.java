package com.songoda.ultimateclaims.claim;

import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimPermissions;
import com.songoda.ultimateclaims.member.ClaimPermissionsBuilder;
import com.songoda.ultimateclaims.member.ClaimRole;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class Claim {

    private String name = null;
    private ClaimMember owner;
    private final Set<ClaimMember> members = new HashSet<>();
    private final Set<Chunk> claimedChunks = new HashSet<>();
    private final Set<UUID> bannedPlayer = new HashSet<>();

    private ClaimPermissions memberPermissions = new ClaimPermissionsBuilder()
            .setCanBuild(true)
            .setCanInteract(true)
            .setCanPlace(true)
            .build();

    private ClaimPermissions visitorPermissions = new ClaimPermissionsBuilder()
            .setCanBuild(false)
            .setCanInteract(false)
            .setCanPlace(false)
            .build();

    private Location powerCell = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClaimMember getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = new ClaimMember(owner, ClaimRole.OWNER);
    }

    public Set<ClaimMember> getMembers() {
        return members;
    }

    public void addMember(UUID uuid, ClaimRole role) {
        this.members.add(new ClaimMember(uuid, role));
    }

    public void addMember(Player player, ClaimRole role) {
        addMember(player.getUniqueId(), role);
    }

    public ClaimMember getMember(UUID uuid) {
        if (owner.getUniqueId() == uuid)
            return owner;
        Optional<ClaimMember> optional = this.members.stream().filter(member -> member.getUniqueId() == uuid).findFirst();
        return optional.orElse(null);
    }

    public ClaimMember getMember(Player player) {
        return getMember(player.getUniqueId());
    }

    public void removeMember(UUID uuid) {
        Optional<ClaimMember> optional = this.members.stream().filter(member -> member.getUniqueId() == uuid).findFirst();
        optional.ifPresent(this.members::remove);
    }

    public void removeMember(ClaimMember member) {
        this.members.remove(member);
    }

    public void removeMember(Player player) {
        removeMember(player);
    }

    public Set<Chunk> getClaimedChunks() {
        return claimedChunks;
    }

    public void addClaimedChunk(Chunk chunk) {
        this.claimedChunks.add(chunk);
    }

    public void removeClaimedChunk(Chunk chunk) {
        this.claimedChunks.remove(chunk);
    }

    public Location getPowerCell() {
        return powerCell;
    }

    public void setPowerCell(Location powerCell) {
        this.powerCell = powerCell;
    }

    public ClaimPermissions getMemberPermissions() {
        return memberPermissions;
    }

    public void setMemberPermissions(ClaimPermissions memberPermissions) {
        this.memberPermissions = memberPermissions;
    }

    public ClaimPermissions getVisitorPermissions() {
        return visitorPermissions;
    }

    public void setVisitorPermissions(ClaimPermissions visitorPermissions) {
        this.visitorPermissions = visitorPermissions;
    }

    public void banPlayer(UUID uuid) {
        this.bannedPlayer.add(uuid);
    }

    public void unBanPlayer(UUID uuid) {
        this.bannedPlayer.remove(uuid);
    }

}
