package com.songoda.ultimateclaims.claim;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.core.utils.PlayerUtils;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.api.events.ClaimDeleteEvent;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimPerm;
import com.songoda.ultimateclaims.member.ClaimPermissions;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.settings.Settings;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class Claim {

    private int id;
    private String name = null;
    private ClaimMember owner;
    private final Set<ClaimMember> members = new HashSet<>();

    private final Set<ClaimedChunk> claimedChunks = new HashSet<>();
    private final Set<UUID> bannedPlayers = new HashSet<>();

    private Location home = null;
    private boolean locked = false;

    private final ClaimSettings claimSettings = new ClaimSettings()
            .setEnabled(ClaimSetting.HOSTILE_MOB_SPAWNING, Settings.DEFAULT_CLAIM_HOSTILE_MOB_SPAWN.getBoolean())
            .setEnabled(ClaimSetting.FIRE_SPREAD, Settings.DEFAULT_CLAIM_FIRE_SPREAD.getBoolean())
            .setEnabled(ClaimSetting.MOB_GRIEFING, Settings.DEFAULT_CLAIM_MOB_GRIEFING.getBoolean())
            .setEnabled(ClaimSetting.LEAF_DECAY, Settings.DEFAULT_CLAIM_LEAF_DECAY.getBoolean())
            .setEnabled(ClaimSetting.PVP, Settings.DEFAULT_CLAIM_PVP.getBoolean())
            .setEnabled(ClaimSetting.TNT, Settings.DEFAULT_CLAIM_TNT.getBoolean())
            .setEnabled(ClaimSetting.FLY, Settings.DEFAULT_CLAIM_FLY.getBoolean());

    private ClaimPermissions memberPermissions = new ClaimPermissions()
            .setAllowed(ClaimPerm.BREAK, Settings.DEFAULT_MEMBER_BREAK.getBoolean())
            .setAllowed(ClaimPerm.INTERACT, Settings.DEFAULT_MEMBER_INTERACT.getBoolean())
            .setAllowed(ClaimPerm.PLACE, Settings.DEFAULT_MEMBER_PLACE.getBoolean())
            .setAllowed(ClaimPerm.MOB_KILLING, Settings.DEFAULT_MEMBER_MOB_KILL.getBoolean())
            .setAllowed(ClaimPerm.REDSTONE, Settings.DEFAULT_MEMBER_REDSTONE.getBoolean())
            .setAllowed(ClaimPerm.DOORS, Settings.DEFAULT_MEMBER_DOORS.getBoolean())
            .setAllowed(ClaimPerm.TRADING, Settings.DEFAULT_MEMBER_TRADE.getBoolean());

    private ClaimPermissions visitorPermissions = new ClaimPermissions()
            .setAllowed(ClaimPerm.BREAK, Settings.DEFAULT_VISITOR_BREAK.getBoolean())
            .setAllowed(ClaimPerm.INTERACT, Settings.DEFAULT_VISITOR_INTERACT.getBoolean())
            .setAllowed(ClaimPerm.PLACE, Settings.DEFAULT_VISITOR_PLACE.getBoolean())
            .setAllowed(ClaimPerm.MOB_KILLING, Settings.DEFAULT_VISITOR_MOB_KILL.getBoolean())
            .setAllowed(ClaimPerm.REDSTONE, Settings.DEFAULT_VISITOR_REDSTONE.getBoolean())
            .setAllowed(ClaimPerm.DOORS, Settings.DEFAULT_VISITOR_DOORS.getBoolean())
            .setAllowed(ClaimPerm.TRADING, Settings.DEFAULT_VISITOR_TRADE.getBoolean());

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
        if (bossBarMember != null)
            bossBarMember.setTitle(name);
        if (bossBarVisitor != null)
            bossBarVisitor.setTitle(name);
    }

    public BossBar getVisitorBossBar() {
        if (bossBarVisitor == null)
            bossBarVisitor = Bukkit.getServer().createBossBar(this.name, BarColor.YELLOW, BarStyle.SOLID);
        return bossBarVisitor;
    }

    public BossBar getMemberBossBar() {
        if (bossBarMember == null)
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

    public void transferOwnership(OfflinePlayer newOwner) {
        if (newOwner.getUniqueId() == owner.getUniqueId())
            return;

        removeMember(newOwner.getUniqueId());
        addMember(owner);
        setOwner(newOwner.getUniqueId());
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
     *
     * @param name name to search
     * @return Member instance matching this username, if any
     */
    public ClaimMember getMember(String name) {
        if (name == null) return null;
        if (name.equals(owner.getName())) return owner;
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

    public int getMaxClaimSize(Player player) {
        return PlayerUtils.getNumberFromPermission(player, "ultimateclaims.maxclaims", Settings.MAX_CHUNKS.getInt());
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
        animateChunk(chunk, player, Material.EMERALD_BLOCK);
        return addClaimedChunk(chunk);
    }

    public ClaimedChunk removeClaimedChunk(Chunk chunk) {
        ClaimedChunk removedChunk = new ClaimedChunk(this, chunk);
        this.claimedChunks.remove(removedChunk);
        return removedChunk;
    }

    public ClaimedChunk removeClaimedChunk(Chunk chunk, Player player) {
        animateChunk(chunk, player, Material.REDSTONE_BLOCK);
        return this.removeClaimedChunk(chunk);
    }

    public void animateChunk(Chunk chunk, Player player, Material material) {
        int bx = chunk.getX() << 4;
        int bz = chunk.getZ() << 4;

        World world = player.getWorld();

        Random random = new Random();

        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13))
            for (int xx = bx; xx < bx + 16; xx++) {
                for (int zz = bz; zz < bz + 16; zz++) {
                    for (int yy = player.getLocation().getBlockY() - 5; yy < player.getLocation().getBlockY() + 5; yy++) {
                        Block block = world.getBlockAt(xx, yy, zz);
                        CompatibleMaterial m = CompatibleMaterial.getMaterial(block);
                        if (!m.isOccluding() || m.isInteractable()) continue;
                        Bukkit.getScheduler().runTaskLater(UltimateClaims.getInstance(), () -> {
                            player.sendBlockChange(block.getLocation(), material, (byte) 0);
                            Bukkit.getScheduler().runTaskLater(UltimateClaims.getInstance(), () ->
                                    player.sendBlockChange(block.getLocation(), block.getBlockData()), random.nextInt(30) + 1);
                            player.playSound(block.getLocation(), CompatibleSound.BLOCK_METAL_STEP.getSound(), 1F, .2F);
                        }, random.nextInt(30) + 1);
                    }
                }
            }
    }

    public List<ClaimCorners> getCorners() {
        if (this.claimedChunks.size() <= 0) return null;

        List<ClaimCorners> result = new ArrayList<>();

        for (ClaimedChunk cChunk : this.claimedChunks) {
            double[] xArr = new double[2],
                    zArr = new double[2];

            int cX = cChunk.getX() * 16,
                    cZ = cChunk.getZ() * 16;

            xArr[0] = cX;
            zArr[0] = cZ;

            xArr[1] = cX + 16;
            zArr[1] = cZ + 16;

            result.add(new ClaimCorners(cChunk.getChunk(), xArr, zArr));
        }

        return result;
    }

    public boolean hasPowerCell() {
        return powerCell.location != null;
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

    public void destroy(ClaimDeleteReason reason) {
        ClaimDeleteEvent event = new ClaimDeleteEvent(this, reason);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        this.claimedChunks.clear();
        if (Bukkit.getPluginManager().isPluginEnabled("dynmap"))
            UltimateClaims.getInstance().getDynmapManager().refresh(this);
        this.powerCell.destroy();
        UltimateClaims.getInstance().getDataManager().deleteClaim(this);
        UltimateClaims.getInstance().getClaimManager().removeClaim(this);

        // we've just unclaimed the chunk we're in, so we've "moved" out of the claim
        if (bossBarMember != null) bossBarMember.removeAll();
        if (bossBarVisitor != null) bossBarVisitor.removeAll();
        getOwnerAndMembers().forEach(m -> m.setPresent(false));
        members.clear();

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

    public String getPowercellTimeRemaining() {
        if (hasPowerCell())
            return powerCell.getTimeRemaining();
        else
            return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass())
            return false;
        return this.id == ((Claim) obj).id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.id;
        return hash;
    }
}
