package com.songoda.ultimateclaims.claim;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimPermissions;
import com.songoda.ultimateclaims.member.ClaimPermissionsBuilder;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.utils.Methods;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class Claim {

    private String name = null;
    private ClaimMember owner;
    private final Set<ClaimMember> members = new HashSet<>();
    private final Set<ClaimedChunk> claimedChunks = new HashSet<>();
    private final Set<UUID> bannedPlayers = new HashSet<>();

    private Location home = null;
    private boolean locked = false;

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

    private PowerCell powerCell = new PowerCell(this);

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
        this.owner = new ClaimMember(this, owner, ClaimRole.OWNER);
    }

    public Set<ClaimMember> getMembers() {
        return members;
    }

    public Set<ClaimMember> getOwnerAndMembers() {
        Set<ClaimMember> members = new HashSet<>(this.members);
        members.add(this.owner);
        return members;
    }

    public void addMember(UUID uuid, ClaimRole role) {
        this.members.add(new ClaimMember(this, uuid, role));
    }

    public void addMember(OfflinePlayer player, ClaimRole role) {
        addMember(player.getUniqueId(), role);
    }

    public ClaimMember getMember(UUID uuid) {
        if (owner.getUniqueId() == uuid)
            return owner;
        Optional<ClaimMember> optional = this.members.stream().filter(member -> member.getUniqueId() == uuid).findFirst();
        return optional.orElse(null);
    }

    public ClaimMember getMember(OfflinePlayer player) {
        return getMember(player.getUniqueId());
    }

    public void removeMember(UUID uuid) {
        Optional<ClaimMember> optional = this.members.stream().filter(member -> member.getUniqueId() == uuid).findFirst();
        optional.ifPresent(this.members::remove);
    }

    public void removeMember(ClaimMember member) {
        this.members.remove(member);
    }

    public void removeMember(OfflinePlayer player) {
        this.removeMember(player.getUniqueId());
    }

    public boolean isOwnerOrMember(OfflinePlayer player) {
        if (player.getUniqueId() == owner.getUniqueId()) return true;
        return this.members.stream().anyMatch(member -> member.getRole() == ClaimRole.MEMBER);
    }

    public boolean containsChunk(Chunk chunk) {
        return this.claimedChunks.stream().anyMatch(x -> x.equals(new ClaimedChunk(chunk)));
    }

    public int getClaimSize() {
        return this.claimedChunks.size();
    }

    public void addClaimedChunk(Chunk chunk) {
        this.claimedChunks.add(new ClaimedChunk(chunk));
    }

    public void addClaimedChunk(Chunk chunk, Player player) {
        Methods.animateChunk(chunk, player, Material.EMERALD_BLOCK);
        addClaimedChunk(chunk);
    }

    public void removeClaimedChunk(Chunk chunk) {
        this.claimedChunks.remove(new ClaimedChunk(chunk));
    }

    public void removeClaimedChunk(Chunk chunk, Player player) {
        Methods.animateChunk(chunk, player, Material.REDSTONE_BLOCK);
        this.removeClaimedChunk(chunk);
    }

    public PowerCell getPowerCell() {
        return powerCell;
    }

    public void setPowerCell(PowerCell powerCell) {
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
        this.bannedPlayers.add(uuid);
    }

    public void unBanPlayer(UUID uuid) {
        this.bannedPlayers.remove(uuid);
    }

    public void destroy() {
        this.claimedChunks.clear();
        this.powerCell.destroy();
        UltimateClaims.getInstance().getClaimManager().removeClaim(this);
    }

    public Set<UUID> getBannedPlayers() {
        return bannedPlayers;
    }

    public void setOwner(ClaimMember owner) {
        this.owner = owner;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) {
        this.home = home;
    }
}
