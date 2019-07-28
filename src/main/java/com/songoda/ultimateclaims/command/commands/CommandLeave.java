package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.command.AbstractCommand;
import com.songoda.ultimateclaims.member.ClaimMember;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class CommandLeave extends AbstractCommand {

    public CommandLeave(AbstractCommand parent) {
        super("leave", parent, true);
    }

    @Override
    protected ReturnType runCommand(UltimateClaims instance, CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (args.length < 2)
            return ReturnType.SYNTAX_ERROR;


        StringBuilder claimBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            String line = args[i];
            claimBuilder.append(line).append(" ");
        }
        String claimStr = claimBuilder.toString().trim();

        Optional oClaim = instance.getClaimManager().getRegisteredClaims().stream()
                .filter(c -> c.getName().toLowerCase().equals(claimStr.toLowerCase())
                        && c.getMember(player) != null).findFirst();

        if (!oClaim.isPresent()) {
            instance.getLocale().getMessage("command.general.notapartclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }
        Claim claim = (Claim) oClaim.get();

        claim.removeMember(player);

        instance.getLocale().getMessage("command.leave.youleft")
                .processPlaceholder("claim", claim.getName())
                .sendPrefixedMessage(player);

        for (ClaimMember member : claim.getMembers())
            this.notify(instance, member);
        this.notify(instance, claim.getOwner());

        return ReturnType.SUCCESS;
    }

    private void notify(UltimateClaims instance, ClaimMember member) {
        Player player = Bukkit.getPlayer(member.getUniqueId());
        if (player != null)
            instance.getLocale().getMessage("command.leave.left")
                    .processPlaceholder("player", player.getName())
                    .processPlaceholder("claim", member.getClaim().getName())
                    .sendPrefixedMessage(player);
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.leave";
    }

    @Override
    public String getSyntax() {
        return "/c leave <claim>";
    }

    @Override
    public String getDescription() {
        return "Leave a claim that you are a member of.";
    }
}
