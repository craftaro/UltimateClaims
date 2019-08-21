package com.songoda.ultimateclaims.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.core.library.commands.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandSetSpawn extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandSetSpawn(UltimateClaims plugin, AbstractCommand parent) {
        super(parent, true, "setspawn");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player)sender;
        plugin.getPluginSettings().setSpawnPoint(player.getLocation());
        plugin.getDataManager().createOrUpdatePluginSettings(plugin.getPluginSettings());
        plugin.getLocale().newMessage("&aSpawn point set!").sendPrefixedMessage(player);
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
        return "/c setspawn";
    }

    @Override
    public String getDescription() {
        return "Set the spawn point for varies use such as ejecting and banning.";
    }
}
