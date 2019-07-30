package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.command.AbstractCommand;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimRole;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CommandLock extends AbstractCommand {

    public CommandLock(AbstractCommand parent) {
        super(parent, true, "lock");
    }

    @Override
    protected ReturnType runCommand(UltimateClaims instance, CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (!instance.getClaimManager().hasClaim(player)) {
            instance.getLocale().getMessage("command.general.noclaim").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Claim claim = instance.getClaimManager().getClaim(player);

        if (!claim.isLocked()) {
            instance.getLocale().getMessage("command.lock.locked")
                    .sendPrefixedMessage(player);
            for (ClaimMember member : claim.getMembers().stream().filter(m -> m.getRole() == ClaimRole.VISITOR)
                    .collect(Collectors.toList())) {
                member.eject();
            }
        } else
            instance.getLocale().getMessage("command.lock.unlocked")
                    .sendPrefixedMessage(player);

        claim.setLocked(!claim.isLocked());

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(UltimateClaims instance, CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.lock";
    }

    @Override
    public String getSyntax() {
        return "/c lock";
    }

    @Override
    public String getDescription() {
        return "Lock or unlock your claim.";
    }
}
