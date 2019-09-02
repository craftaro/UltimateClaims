package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.command.AbstractCommand;
import com.songoda.ultimateclaims.gui.GUIRecipe;
import com.songoda.ultimateclaims.invite.Invite;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CommandRecipe extends AbstractCommand {

    public CommandRecipe(AbstractCommand parent) {
        super(parent, true, "recipe");
    }

    @Override
    protected ReturnType runCommand(UltimateClaims instance, CommandSender sender, String... args) {

        new GUIRecipe((Player)sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(UltimateClaims instance, CommandSender sender, String... args) {
        return null;
    }


    @Override
    public String getPermissionNode() {
        return "ultimateclaims.recipe";
    }

    @Override
    public String getSyntax() {
        return "/c recipe";
    }

    @Override
    public String getDescription() {
        return "View the recipe for a powercell.";
    }
}
