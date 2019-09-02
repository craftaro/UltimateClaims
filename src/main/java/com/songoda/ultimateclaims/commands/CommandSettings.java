package com.songoda.ultimateclaims.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.configuration.editor.ConfigEditorGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandSettings extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandSettings(UltimateClaims plugin) {
        super(true, "Settings");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        plugin.getGuiManager().showGUI((Player) sender, new ConfigEditorGui(plugin, null, "UltimateClaims Settings Manager", plugin.getConfig().getCoreConfig()));
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.admin";
    }

    @Override
    public String getSyntax() {
        return "/c settings";
    }

    @Override
    public String getDescription() {
        return "Edit the UltimateClaims Settings.";
    }
}
