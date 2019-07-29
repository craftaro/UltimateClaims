package com.songoda.ultimateclaims.gui;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.utils.Methods;
import com.songoda.ultimateclaims.utils.ServerVersion;
import com.songoda.ultimateclaims.utils.gui.AbstractGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIBans extends AbstractGUI {

    private UltimateClaims plugin;
    private Claim claim;
    private boolean back;

    public GUIBans(Player player, Claim claim, boolean back) {
        super(player);
        this.claim = claim;
        this.plugin = UltimateClaims.getInstance();
        this.back = back;

        init(Methods.formatTitle(plugin.getLocale().getMessage("interface.bans.title").getMessage()), 54);
    }

    @Override
    protected void constructGUI() {
        inventory.clear();
        resetClickables();
        registerClickables();

        inventory.setItem(1, Methods.getBackgroundGlass(true));
        inventory.setItem(9, Methods.getBackgroundGlass(true));

        inventory.setItem(7, Methods.getBackgroundGlass(true));
        inventory.setItem(17, Methods.getBackgroundGlass(true));

        inventory.setItem(36, Methods.getBackgroundGlass(true));
        inventory.setItem(45, Methods.getBackgroundGlass(true));
        inventory.setItem(46, Methods.getBackgroundGlass(true));

        inventory.setItem(44, Methods.getBackgroundGlass(true));
        inventory.setItem(52, Methods.getBackgroundGlass(true));
        inventory.setItem(53, Methods.getBackgroundGlass(true));

        inventory.setItem(2, Methods.getBackgroundGlass(false));
        inventory.setItem(3, Methods.getBackgroundGlass(false));
        inventory.setItem(5, Methods.getBackgroundGlass(false));
        inventory.setItem(6, Methods.getBackgroundGlass(false));
        inventory.setItem(18, Methods.getBackgroundGlass(false));
        inventory.setItem(26, Methods.getBackgroundGlass(false));
        inventory.setItem(27, Methods.getBackgroundGlass(false));
        inventory.setItem(35, Methods.getBackgroundGlass(false));
        inventory.setItem(47, Methods.getBackgroundGlass(false));
        inventory.setItem(48, Methods.getBackgroundGlass(false));
        inventory.setItem(49, Methods.getBackgroundGlass(false));
        inventory.setItem(50, Methods.getBackgroundGlass(false));
        inventory.setItem(51, Methods.getBackgroundGlass(false));

        ItemStack exit = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.OAK_FENCE_GATE : Material.valueOf("FENCE_GATE"));
        ItemMeta exitMeta = exit.getItemMeta();
        if (back) exitMeta.setDisplayName(plugin.getLocale().getMessage("general.interface.back").getMessage());
        else exitMeta.setDisplayName(plugin.getLocale().getMessage("general.interface.exit").getMessage());
        exit.setItemMeta(exitMeta);

        ItemStack info = new ItemStack(Material.PAINTING);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(plugin.getLocale().getMessage("interface.bans.infotitle").getMessage());
        List<String> infoLore = new ArrayList<>();
        String[] infoSplit = plugin.getLocale().getMessage("interface.bans.infolore").getMessage().split("\\|");
        for (String line : infoSplit) infoLore.add(line);
        infoMeta.setLore(infoLore);
        info.setItemMeta(infoMeta);

        ItemStack previous = new ItemStack(Material.MAP);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.setDisplayName(plugin.getLocale().getMessage("general.interface.previous").getMessage());
        previous.setItemMeta(previousMeta);

        ItemStack next = new ItemStack(Material.PAPER);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(plugin.getLocale().getMessage("general.interface.next").getMessage());
        next.setItemMeta(nextMeta);

        inventory.setItem(0, exit);
        inventory.setItem(4, info);
        inventory.setItem(8, exit);
        inventory.setItem(37, previous);
        inventory.setItem(43, next);

    }

    @Override
    protected void registerClickables() {
        registerClickable(0, (player, inventory, cursor, slot, type) -> {
            if (back)
                new GUIPowerCell(player, claim);
            else
                player.closeInventory();
        });

        registerClickable(8, (player, inventory, cursor, slot, type) -> {
            if (back)
                new GUIPowerCell(player, claim);
            else
                player.closeInventory();
        });

        registerClickable(37, (player, inventory, cursor, slot, type) -> {
            // Previous page
        });

        registerClickable(43, (player, inventory, cursor, slot, type) -> {
            // Next Page
        });
    }

    @Override
    protected void registerOnCloses() {

    }
}
