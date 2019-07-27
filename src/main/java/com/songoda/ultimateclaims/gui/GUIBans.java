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

    public GUIBans(Player player, Claim claim) {
        super(player);
        this.claim = claim;
        this.plugin = UltimateClaims.getInstance();

        init(Methods.formatTitle(plugin.getLocale().getMessage("interface.banned.title").getMessage()), 54);
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
        exitMeta.setDisplayName(plugin.getLocale().getMessage("interface.settings.exittitle").getMessage());
        List<String> exitLore = new ArrayList<>();
        String[] exitSplit = plugin.getLocale().getMessage("interface.settings.exitlore").getMessage().split("\\|");
        for (String line : exitSplit) exitLore.add(line);
        exitMeta.setLore(exitLore);
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
        previousMeta.setDisplayName(plugin.getLocale().getMessage("interface.members.previoustitle").getMessage());
        List<String> previousLore = new ArrayList<>();
        String[] previousSplit = plugin.getLocale().getMessage("interface.members.previouslore").getMessage().split("\\|");
        for (String line : previousSplit) previousLore.add(line);
        previousMeta.setLore(previousLore);
        previous.setItemMeta(previousMeta);

        ItemStack next = new ItemStack(Material.PAPER);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(plugin.getLocale().getMessage("interface.members.nexttitle").getMessage());
        List<String> nextLore = new ArrayList<>();
        String[] nextSplit = plugin.getLocale().getMessage("interface.members.nextlore").getMessage().split("\\|");
        for (String line : nextSplit) nextLore.add(line);
        nextMeta.setLore(nextLore);
        next.setItemMeta(nextMeta);

        inventory.setItem(0, exit);
        inventory.setItem(4, info);
        inventory.setItem(8, exit);
        inventory.setItem(37, previous);
        inventory.setItem(43, next);

        // Skulls + Pages
    }

    @Override
    protected void registerClickables() {
        registerClickable(0, (player, inventory, cursor, slot, type) -> {
            // Return or close GUI
        });

        registerClickable(8, (player, inventory, cursor, slot, type) -> {
            // ^
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
