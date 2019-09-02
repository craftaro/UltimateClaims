package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandSetSpawn extends AbstractCommand {

    public CommandSetSpawn(AbstractCommand parent) {
        super(parent, true, "setspawn");
    }

    @Override
    protected ReturnType runCommand(UltimateClaims instance, CommandSender sender, String... args) {
        Player player = (Player)sender;
        instance.getPluginSettings().setSpawnPoint(player.getLocation());
        instance.getDataManager().createOrUpdatePluginSettings(instance.getPluginSettings());
        instance.getLocale().newMessage("&aSpawn point set!").sendPrefixedMessage(player);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(UltimateClaims instance, CommandSender sender, String... args) {
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
