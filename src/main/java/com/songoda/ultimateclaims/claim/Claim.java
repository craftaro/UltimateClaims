package com.songoda.ultimateclaims.claim;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.member.*;
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
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public class Claim {

    private int id;
    private String name = null;
    private ClaimMember owner;
    private final Set<ClaimMember> members = new HashSet<>();

    private final Set<ClaimedChunk> claimedChunks = new HashSet<>();
    private final Set<UUID> bannedPlayers = new HashSet<>();

    private Location home = null;
    private boolean locked = false;

    private ClaimSettings claimSettings = new ClaimSettings();

    private ClaimPermissions memberPermissions = new ClaimPermissions()
            .setCanBreak(true)
            .setCanInteract(true)
            .setCanPlace(true)
            .setCanMobKill(true)
            .setCanRedstone(true)
            .setCanDoors(true);

    private ClaimPermissions visitorPermissions = new ClaimPermissions()
            .setCanBreak(false)
            .setCanInteract(false)
            .setCanPlace(false)
            .setCanMobKill(false)
            .setCanRedstone(false)
            .setCanDoors(false);

    private PowerCell powerCell = new PowerCell(this);

    private BossBar bossBarVisitor = null;
    private BossBar bossBarMember = null;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public ClaimedChunk getFirstClaimedChunk() {
        return this.claimedChunks.iterator().next();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if(bossBarMember != null)
            bossBarMember.setTitle(name);
        if(bossBarVisitor != null)
            bossBarVisitor.setTitle(name);
    }

    public BossBar getVisitorBossBar() {
        if(bossBarVisitor == null)
            bossBarVisitor = Bukkit.getServer().createBossBar(this.name, BarColor.YELLOW, BarStyle.SOLID);
        return bossBarVisitor;
    }

    public BossBar getMemberBossBar() {
        if(bossBarMember == null)
            bossBarMember = Bukkit.getServer().createBossBar(this.name, BarColor.GREEN, BarStyle.SOLID);
        return bossBarMember;
    }

    public ClaimMember getOwner() {
        return owner;
    }

    public ClaimMember setOwner(UUID owner) {
        return this.owner = new ClaimMember(this, owner, null, ClaimRole.OWNER);
    }

    public ClaimMember setOwner(Player owner) {
        return this.owner = new ClaimMember(this, owner.getUniqueId(), owner.getName(), ClaimRole.OWNER);
    }

    public Set<ClaimMember> getMembers() {
        return members;
    }

    public Set<ClaimMember> getOwnerAndMembers() {
        Set<ClaimMember> members = new HashSet<>(this.members);
        members.add(this.owner);
        return members;
    }

    public ClaimMember addMember(ClaimMember member) {
        this.members.add(member);
        return member;
    }
//
//    public ClaimMember addMember(UUID uuid, ClaimRole role) {
//        ClaimMember newMember = new ClaimMember(this, uuid, null, role);
//        this.members.add(newMember);
//        return newMember;
//    }

    public ClaimMember addMember(OfflinePlayer player, ClaimRole role) {
        ClaimMember newMember = new ClaimMember(this, player.getUniqueId(), player.getName(), role);
        this.members.add(newMember);
        return newMember;
    }

    public ClaimMember getMember(UUID uuid) {
        if (owner.getUniqueId().equals(uuid))
            return owner;
        return members.stream()
                .filter(member -> member.getUniqueId().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    /**
     * Search for a member by username
     * @param name name to search
     * @return Member instance matching this username, if any
     */
    public ClaimMember getMember(String name) {
        if(name == null) return null;
        if(name.equals(owner.getName())) return owner;
        return members.stream()
                .filter(member -> name.equals(member.getName()))
                .findFirst()
                .orElse(null);
    }

    public ClaimMember getMember(OfflinePlayer player) {
        return getMember(player.getUniqueId());
    }

    public void removeMember(UUID uuid) {
        Optional<ClaimMember> optional = this.members.stream().filter(member -> member.getUniqueId().equals(uuid)).findFirst();
        optional.ifPresent(this.members::remove);
    }

    public void removeMember(ClaimMember member) {
        this.members.remove(member);
    }

    public void removeMember(OfflinePlayer player) {
        this.removeMember(player.getUniqueId());
    }

    public boolean playerHasPerms(Player player, ClaimPerm claimPerm) {
        ClaimMember member = getMember(player);
        if (player.hasPermission("ultimateclaims.bypass")
                || player.getUniqueId().equals(owner.getUniqueId())) return true;
        if (member == null) return false;
        return member.getRole() == ClaimRole.VISITOR && getVisitorPermissions().hasPermission(claimPerm)
                || member.getRole() == ClaimRole.MEMBER && getMemberPermissions().hasPermission(claimPerm);
    }

    public boolean isOwnerOrMember(OfflinePlayer player) {
        if (player.getUniqueId().equals(owner.getUniqueId())) return true;
        return this.members.stream().anyMatch(member -> member.getRole() == ClaimRole.MEMBER);
    }

    public boolean containsChunk(Chunk chunk) {
        final String world = chunk.getWorld().getName();
        return this.claimedChunks.stream().anyMatch(x -> x.getWorld().equals(world) && x.getX() == chunk.getX() && x.getZ() == chunk.getZ());
    }

    public boolean containsChunk(String world, int chunkX, int chunkZ) {
        return this.claimedChunks.stream().anyMatch(x -> x.getWorld().equals(world) && x.getX() == chunkX && x.getZ() == chunkZ);
    }

    public int getClaimSize() {
        return this.claimedChunks.size();
    }

    public ClaimedChunk addClaimedChunk(Chunk chunk) {
        ClaimedChunk newChunk = new ClaimedChunk(this, chunk);
        this.claimedChunks.add(newChunk);
        return newChunk;
    }

    public ClaimedChunk addClaimedChunk(String world, int x, int z) {
        ClaimedChunk newChunk = new ClaimedChunk(this, world, x, z);
        this.claimedChunks.add(newChunk);
        return newChunk;
    }

    public ClaimedChunk addClaimedChunk(Chunk chunk, Player player) {
        Methods.animateChunk(chunk, player, Material.EMERALD_BLOCK);
        return addClaimedChunk(chunk);
    }

    public ClaimedChunk removeClaimedChunk(Chunk chunk) {
        ClaimedChunk removedChunk = new ClaimedChunk(this, chunk);
        this.claimedChunks.remove(removedChunk);
        return removedChunk;
    }

    public ClaimedChunk removeClaimedChunk(Chunk chunk, Player player) {
        Methods.animateChunk(chunk, player, Material.REDSTONE_BLOCK);
        return this.removeClaimedChunk(chunk);
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

    public boolean isBanned(UUID uuid) {
        return this.bannedPlayers.contains(uuid);
    }

    public void destroy() {
        this.claimedChunks.clear();
        this.powerCell.destroy();
        UltimateClaims.getInstance().getDataManager().deleteClaim(this);
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

    public ClaimSettings getClaimSettings() {
        return claimSettings;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass())
            return false;
        return this.id == ((Claim) obj).id;
    }
}
