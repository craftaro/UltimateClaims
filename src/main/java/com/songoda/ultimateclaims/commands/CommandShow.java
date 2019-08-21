package com.songoda.ultimateclaims.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.core.library.commands.AbstractCommand;
import com.songoda.ultimateclaims.tasks.VisualizeTask;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import net.md_5.bungee.api.ChatColor;

public class CommandShow extends AbstractCommand {

    private final UltimateClaims plugin;

    public CommandShow(UltimateClaims plugin, AbstractCommand parent) {
        super(parent, true, "show");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Command must be called as a player");
            return ReturnType.FAILURE;
        }
        Player player = (Player) sender;

        if (args.length > 1)
            return ReturnType.SYNTAX_ERROR;

        if(VisualizeTask.togglePlayer(player))
            plugin.getLocale().getMessage("command.show.start").sendPrefixedMessage(player);
        else
            plugin.getLocale().getMessage("command.show.stop").sendPrefixedMessage(player);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimateclaims.show";
    }

    @Override
    public String getSyntax() {
        return "/c show";
    }

    @Override
    public String getDescription() {
        return "Visualize claims around you";
    }
}
