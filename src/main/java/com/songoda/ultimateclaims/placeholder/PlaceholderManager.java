package com.songoda.ultimateclaims.placeholder;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.utils.Methods;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
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
            case "totalpower":
                return claims.size() == 0 ? "0" : claims.stream().map(c -> Methods.makeReadable(c.getTotalPower() * 60 * 1000)).collect(Collectors.joining(", "));
            case "totalchunks":
                return claims.size() == 0 ? "0" : Integer.toString(claims.stream().mapToInt(Claim::getClaimSize).sum());
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
