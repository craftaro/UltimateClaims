package com.songoda.ultimateclaims.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.core.library.commands.AbstractCommand;
import com.songoda.ultimateclaims.gui.GUIRecipe;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandRecipe extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandRecipe(UltimateClaims plugin) {
        super(true, "recipe");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {

        new GUIRecipe((Player)sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
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
