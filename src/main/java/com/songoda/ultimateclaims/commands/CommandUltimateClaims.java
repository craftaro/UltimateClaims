package com.songoda.ultimateclaims.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.core.library.commands.AbstractCommand;
import com.songoda.ultimateclaims.utils.Methods;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandUltimateClaims extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandUltimateClaims(UltimateClaims plugin) {
        super(null, false, "UltimateClaims");
        this.plugin = plugin;
    }

    @Override
    protected AbstractCommand.ReturnType runCommand(CommandSender sender, String... args) {
        sender.sendMessage("");
        plugin.getLocale().newMessage("&7Version " + plugin.getDescription().getVersion()
                + " Created with <3 by &5&l&oSongoda").sendPrefixedMessage(sender);

        for (AbstractCommand command : plugin.getCommandManager().getCommands()) {
            if (command.getPermissionNode() == null || sender.hasPermission(command.getPermissionNode())) {
                sender.sendMessage(Methods.formatText("&8 - &a" + command.getSyntax() + "&7 - " + command.getDescription()));
            }
        }
        sender.sendMessage("");

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/UltimateClaims";
    }

    @Override
    public String getDescription() {
        return "Displays this page.";
    }
}
