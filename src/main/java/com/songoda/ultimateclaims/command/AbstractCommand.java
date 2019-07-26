package com.songoda.ultimateclaims.command;

import com.songoda.ultimateclaims.UltimateClaims;
import org.bukkit.command.CommandSender;

public abstract class AbstractCommand {

    private final AbstractCommand parent;
    private final String command;
    private final boolean noConsole;

    protected AbstractCommand(String command, AbstractCommand parent, boolean noConsole) {
        this.command = command;
        this.parent = parent;
        this.noConsole = noConsole;
    }

    public AbstractCommand getParent() {
        return parent;
    }

    public String getCommand() {
        return command;
    }

    public boolean isNoConsole() {
        return noConsole;
    }

    protected abstract ReturnType runCommand(UltimateClaims instance, CommandSender sender, String... args);

    public abstract String getPermissionNode();

    public abstract String getSyntax();

    public abstract String getDescription();

    public enum ReturnType {SUCCESS, FAILURE, SYNTAX_ERROR}
}
