package com.craftaro.ultimateclaims.placeholder;

import com.craftaro.ultimateclaims.UltimateClaims;
import com.craftaro.ultimateclaims.claim.Claim;
import com.craftaro.ultimateclaims.member.ClaimMember;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

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
    public String onRequest(OfflinePlayer player, String identifier) {
        List<Claim> claims = plugin.getClaimManager().getClaims(player);

        switch (identifier) {
            case "claims":
                return claims.size() == 0 ? plugin.getLocale().getMessage("general.word.none").getMessage() : claims.stream().map(Claim::getName).collect(Collectors.joining(", "));
            case "remainingpower":
            case "totalpower": // Legacy
                return claims.size() == 0 ? "0" : claims.stream().map(Claim::getPowercellTimeRemaining).collect(Collectors.joining(", "));
            case "totalchunks": {
                if (!player.isOnline()) return "0/0";
                return claims.size() == 0 ? "0/0" : claims.stream().mapToInt(Claim::getClaimSize).sum() + "/" + claims.stream().mapToInt(c -> c.getMaxClaimSize(player.getPlayer())).sum();
            }
            case "owner":
                return claims.size() == 0 ? plugin.getLocale().getMessage("general.word.none").getMessage() : claims.stream().map(c -> c.getOwner().getName()).collect(Collectors.joining(", "));
            case "members": {
                List<ClaimMember> members = new ArrayList<>();
                for (Claim claim : claims)
                    members.addAll(claim.getOwnerAndMembers());
                return claims.size() == 0 ? plugin.getLocale().getMessage("general.word.none").getMessage() : members.stream().map(ClaimMember::getName).collect(Collectors.joining(", "));
            }
            case "bans": {
                List<String> bans = new ArrayList<>();
                for (Claim claim : claims)
                    for (UUID uuid : claim.getBannedPlayers()) {
                        String banned = Bukkit.getOfflinePlayer(uuid).getName();
                        if (banned != null)
                            bans.add(banned);
                    }
                return claims.size() == 0 ? plugin.getLocale().getMessage("general.word.none").getMessage() : String.join(", ", bans);
            }
            default:
                return null;
        }
    }

    @Override
    public String getIdentifier() {
        return "ultimateclaims";
    }

    @Override
    public String getAuthor() {
        return "Songoda";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

}
