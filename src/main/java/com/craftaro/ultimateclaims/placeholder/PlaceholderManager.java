package com.craftaro.ultimateclaims.placeholder;

import com.craftaro.core.utils.PlayerUtils;
import com.craftaro.ultimateclaims.UltimateClaims;
import com.craftaro.ultimateclaims.claim.Claim;
import com.craftaro.ultimateclaims.member.ClaimMember;
import com.craftaro.ultimateclaims.settings.Settings;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlaceholderManager extends PlaceholderExpansion {
    private final UltimateClaims plugin;

    public PlaceholderManager(UltimateClaims plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        List<Claim> claims = this.plugin.getClaimManager().getClaims(player);

        switch (params) {
            case "claims":
                return claims.isEmpty() ? this.plugin.getLocale().getMessage("general.word.none").toText() : claims.stream().map(Claim::getName).collect(Collectors.joining(", "));
            case "remainingpower":
            case "totalpower": // Legacy
                return claims.isEmpty() ? "0" : claims.stream().map(Claim::getPowercellTimeRemaining).collect(Collectors.joining(", "));
            case "totalchunks": {
                if (!player.isOnline()) return "0/0";
                return claims.isEmpty() ? "0/" + PlayerUtils.getNumberFromPermission((Player) player, "ultimateclaims.maxclaims", Settings.MAX_CHUNKS.getInt()) + "/" + claims.stream().mapToInt(c -> c.getMaxClaimSize(player.getPlayer())).sum();
            }
            case "owner":
                return claims.isEmpty() ? this.plugin.getLocale().getMessage("general.word.none").toText() : claims.stream().map(c -> c.getOwner().getName()).collect(Collectors.joining(", "));
            case "members": {
                List<ClaimMember> members = new ArrayList<>();
                for (Claim claim : claims)
                    members.addAll(claim.getOwnerAndMembers());
                return claims.isEmpty() ? this.plugin.getLocale().getMessage("general.word.none").toText() : members.stream().map(ClaimMember::getName).collect(Collectors.joining(", "));
            }
            case "bans": {
                List<String> bans = new ArrayList<>();
                for (Claim claim : claims)
                    for (UUID uuid : claim.getBannedPlayers()) {
                        String banned = Bukkit.getOfflinePlayer(uuid).getName();
                        if (banned != null)
                            bans.add(banned);
                    }
                return claims.isEmpty() ? this.plugin.getLocale().getMessage("general.word.none").toText() : String.join(", ", bans);
            }
            default:
                return null;
        }
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ultimateclaims";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Songoda";
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }
}
