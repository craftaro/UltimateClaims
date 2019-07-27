package com.songoda.ultimateclaims.gui;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.PowerCell;
import com.songoda.ultimateclaims.utils.Methods;
import com.songoda.ultimateclaims.utils.ServerVersion;
import com.songoda.ultimateclaims.utils.gui.AbstractGUI;
import com.songoda.ultimateclaims.utils.gui.Clickable;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIPowerCell extends AbstractGUI {

    private UltimateClaims plugin;
    private PowerCell powercell;
    private Claim claim;

    public GUIPowerCell(Player player, Claim claim) {
        super(player);
        this.powercell = claim.getPowerCell();
        this.claim = claim;
        plugin = UltimateClaims.getInstance();

        init(Methods.formatTitle(plugin.getLocale().getMessage("interface.powercell.title").getMessage()), 54);
    }

    @Override
    protected void constructGUI() {
        inventory.clear();
        resetClickables();
        registerClickables();

        inventory.setItem(0, Methods.getBackgroundGlass(true));
        inventory.setItem(1, Methods.getBackgroundGlass(true));
        inventory.setItem(9, Methods.getBackgroundGlass(true));

        inventory.setItem(7, Methods.getBackgroundGlass(true));
        inventory.setItem(8, Methods.getBackgroundGlass(true));
        inventory.setItem(17, Methods.getBackgroundGlass(true));

        inventory.setItem(36, Methods.getBackgroundGlass(true));
        inventory.setItem(45, Methods.getBackgroundGlass(true));
        inventory.setItem(46, Methods.getBackgroundGlass(true));

        inventory.setItem(44, Methods.getBackgroundGlass(true));
        inventory.setItem(52, Methods.getBackgroundGlass(true));
        inventory.setItem(53, Methods.getBackgroundGlass(true));

        inventory.setItem(3, Methods.getBackgroundGlass(false));
        inventory.setItem(5, Methods.getBackgroundGlass(false));
        inventory.setItem(18, Methods.getBackgroundGlass(false));
        inventory.setItem(26, Methods.getBackgroundGlass(false));
        inventory.setItem(27, Methods.getBackgroundGlass(false));
        inventory.setItem(35, Methods.getBackgroundGlass(false));
        inventory.setItem(47, Methods.getBackgroundGlass(false));
        inventory.setItem(49, Methods.getBackgroundGlass(false));
        inventory.setItem(51, Methods.getBackgroundGlass(false));

        ItemStack economy = new ItemStack(Material.SUNFLOWER);
        ItemMeta economyMeta = economy.getItemMeta();
        economyMeta.setDisplayName(plugin.getLocale().getMessage("interface.powercell.economytitle").getMessage());
        List<String> economyLore = new ArrayList<>();
        String[] economySplit = plugin.getLocale().getMessage("interface.powercell.economylore").getMessage().split("\\|");
        for (String line : economySplit) economyLore.add(Methods.formatText(line));
        economyMeta.setLore(economyLore);
        economy.setItemMeta(economyMeta);

        ItemStack total = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.CLOCK : Material.valueOf("WATCH"));
        ItemMeta totalMeta = total.getItemMeta();
        totalMeta.setDisplayName(plugin.getLocale().getMessage("interface.powercell.totaltitle").getMessage());
        List<String> totalLore = new ArrayList<>();
        String[] totalSplit = plugin.getLocale().getMessage("interface.powercell.totallore")
                .processPlaceholder("time", powercell.getTotalPower()).getMessage().split("\\|");
        for (String line : totalSplit) totalLore.add(line);
        totalMeta.setLore(totalLore);
        total.setItemMeta(totalMeta);

        ItemStack valuables = new ItemStack(Material.DIAMOND);
        ItemMeta valuablesMeta = valuables.getItemMeta();
        valuablesMeta.setDisplayName(plugin.getLocale().getMessage("interface.powercell.valuablestitle").getMessage());
        List<String> valublesLore = new ArrayList<>();
        String[] valuablesSplit = plugin.getLocale().getMessage("interface.powercell.valuableslore").getMessage().split("\\|");
        for (String line : valuablesSplit) valublesLore.add(line);
        valuablesMeta.setLore(valublesLore);
        valuables.setItemMeta(valuablesMeta);

        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(plugin.getLocale().getMessage("interface.powercell.infotitle").getMessage());
        List<String> infoLore = new ArrayList<>();
        String[] infoSplit = plugin.getLocale().getMessage("interface.powercell.infolore")
                .processPlaceholder("owner", claim.getOwner()).getMessage().split("\\|");
        for (String line : infoSplit) infoLore.add(line);
        infoMeta.setLore(infoLore);
        info.setItemMeta(infoMeta);

        ItemStack members = new ItemStack(Material.PAINTING);
        ItemMeta membersMeta = members.getItemMeta();
        membersMeta.setDisplayName(plugin.getLocale().getMessage("interface.powercell.memberstitle").getMessage());
        List<String> membersLore = new ArrayList<>();
        String[] membersSplit = plugin.getLocale().getMessage("interface.powercell.memberslore").getMessage().split("\\|");
        for (String line : membersSplit) membersLore.add(line);
        membersMeta.setLore(membersLore);
        members.setItemMeta(membersMeta);

        inventory.setItem(2, economy);
        inventory.setItem(4, total);
        inventory.setItem(6, valuables);
        inventory.setItem(48, info);
        inventory.setItem(50, members);
    }

    @Override
    protected void registerClickables() {
        registerClickable(2, (player, inventory, cursor, slot, type) -> {
            // Click to add more time - type in chat the amount you want to deposit.
        });

        registerClickable(50, (player, inventory, cursor, slot, type) -> {
            // Open members GUI
        });
    }

    @Override
    protected void registerOnCloses() {

    }
}
