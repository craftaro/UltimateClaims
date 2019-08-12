package com.songoda.ultimateclaims.command.commands;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.command.AbstractCommand;
import com.songoda.ultimateclaims.tasks.VisualizeTask;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import net.md_5.bungee.api.ChatColor;

public class CommandShow extends AbstractCommand {

    public CommandShow(AbstractCommand parent) {
        super(parent, true, "show");
    }

    @Override
    protected ReturnType runCommand(UltimateClaims instance, CommandSender sender, String... args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Command must be called as a player");
            return ReturnType.FAILURE;
        }
        Player player = (Player) sender;

        if (args.length > 1)
            return ReturnType.SYNTAX_ERROR;

        if(VisualizeTask.togglePlayer(player))
            instance.getLocale().getMessage("command.show.start").sendPrefixedMessage(player);
        else
            instance.getLocale().getMessage("command.show.stop").sendPrefixedMessage(player);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(UltimateClaims instance, CommandSender sender, String... args) {
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
