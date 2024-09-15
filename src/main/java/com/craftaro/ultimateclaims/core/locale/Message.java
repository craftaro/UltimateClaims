package com.craftaro.ultimateclaims.core.locale;

import com.craftaro.core.SongodaCore;
import com.craftaro.core.chat.AdventureUtils;
import com.craftaro.core.chat.MiniMessagePlaceholder;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.third_party.net.kyori.adventure.text.Component;
import java.util.List;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message {
    private static boolean canActionBar = false;
    private Component prefix = null;
    private Component message;

    public Message(String message) {
        this.message = AdventureUtils.formatComponent(message);
    }

    public void sendMessage(Player player) {
        this.sendMessage((CommandSender)player);
    }

    public void sendMessage(CommandSender sender) {
        AdventureUtils.sendMessage(SongodaCore.getHijackedPlugin(), this.message, new CommandSender[]{sender});
    }

    public void sendTitle(CommandSender sender) {
        if (sender instanceof Player) {
            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) {
                AdventureUtils.sendTitle(SongodaCore.getHijackedPlugin(), AdventureUtils.createTitle(Component.empty(), this.getMessage(), 10, 30, 10), sender);
            } else {
                AdventureUtils.sendTitle(SongodaCore.getHijackedPlugin(), AdventureUtils.createTitle(Component.empty(), this.getMessage()), sender);
            }
        } else {
            AdventureUtils.sendMessage(SongodaCore.getHijackedPlugin(), this.message, new CommandSender[]{sender});
        }
    }

    public void sendActionBar(CommandSender sender) {
        if (!(sender instanceof Player)) {
            AdventureUtils.sendMessage(SongodaCore.getHijackedPlugin(), this.message, new CommandSender[]{sender});
        } else if (!canActionBar) {
            this.sendTitle(sender);
        } else {
            AdventureUtils.sendActionBar(SongodaCore.getHijackedPlugin(), this.getMessage(), sender);
        }
    }

    public void sendPrefixedMessage(CommandSender sender) {
        AdventureUtils.sendMessage(SongodaCore.getHijackedPlugin(), this.prefix.append(this.message), new CommandSender[]{sender});
    }

    public Component getPrefixedMessage() {
        return this.prefix.append(this.message);
    }

    public Component getMessage() {
        return this.message;
    }

    public List<Component> getMessageLines() {
        return AdventureUtils.splitComponent(this.message, '\n');
    }

    public Message processPlaceholder(String placeholder, Object replacement) {
        MiniMessagePlaceholder miniMessagePlaceholder = new MiniMessagePlaceholder(placeholder, replacement == null ? "" : replacement.toString());
        this.message = AdventureUtils.formatPlaceholder(this.message, new MiniMessagePlaceholder[]{miniMessagePlaceholder});
        return this;
    }

    Message setPrefix(String prefix) {
        this.prefix = AdventureUtils.formatComponent(prefix + " ");
        return this;
    }

    public String toString() {
        return AdventureUtils.toLegacy(this.message);
    }

    public String toText() {
        return AdventureUtils.toLegacy(this.message);
    }

    static {
        try {
            Class.forName("net.md_5.bungee.api.ChatMessageType");
            Class.forName("net.md_5.bungee.api.chat.TextComponent");
            Player.Spigot.class.getDeclaredMethod("sendMessage", ChatMessageType.class, TextComponent.class);
            canActionBar = true;
        } catch (Exception var1) {
        }

    }
}
