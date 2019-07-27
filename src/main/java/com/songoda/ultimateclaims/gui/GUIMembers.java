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

public class GUIMembers extends AbstractGUI {

    private UltimateClaims plugin;
    private Player player;
    private Claim claim;

    public GUIMembers(Player player, Claim claim) {
        super(player);
        this.player = player;
        this.claim = claim;
        plugin = UltimateClaims.getInstance();

        init(Methods.formatTitle(plugin.getLocale().getMessage("interface.members.title").getMessage()), 54);
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
        exitMeta.setDisplayName(plugin.getLocale().getMessage("interface.members.exittitle").getMessage());
        List<String> exitLore = new ArrayList<>();
        String[] exitSplit = plugin.getLocale().getMessage("interface.members.exitlore").getMessage().split("\\|");
        for (String line : exitSplit) exitLore.add(line);
        exitMeta.setLore(exitLore);
        exit.setItemMeta(exitMeta);

        ItemStack type = new ItemStack(Material.HOPPER);
        ItemMeta typeMeta = type.getItemMeta();
        typeMeta.setDisplayName(plugin.getLocale().getMessage("interface.members.changetypetitle").getMessage());
        List<String> typeLore = new ArrayList<>();
        String[] typeSplit = plugin.getLocale().getMessage("interface.members.typelore").getMessage().split("\\|");
        for (String line : typeSplit) typeLore.add(line);
        typeMeta.setLore(typeLore);
        type.setItemMeta(typeMeta);

        ItemStack sort = new ItemStack(Material.HOPPER);
        ItemMeta sortMeta = sort.getItemMeta();
        sortMeta.setDisplayName(plugin.getLocale().getMessage("interface.members.changesorttitle").getMessage());
        List<String> sortLore = new ArrayList<>();
        String[] sortSplit = plugin.getLocale().getMessage("interface.members.sortlore").getMessage().split("\\|");
        for (String line : sortSplit) sortLore.add(line);
        sortMeta.setLore(sortLore);
        sort.setItemMeta(sortMeta);

        ItemStack stats = new ItemStack(Material.PAINTING);
        ItemMeta statsMeta = stats.getItemMeta();
        statsMeta.setDisplayName(plugin.getLocale().getMessage("interface.members.statstitle").getMessage());
        List<String> statsLore = new ArrayList<>();
        String[] statsSplit = plugin.getLocale().getMessage("interface.members.statslore").getMessage().split("\\|");
        for (String line : statsSplit) statsLore.add(line);
        statsMeta.setLore(statsLore);
        stats.setItemMeta(statsMeta);

        ItemStack visitor = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.OAK_SIGN : Material.valueOf("SIGN"));
        ItemMeta visitorMeta = visitor.getItemMeta();
        visitorMeta.setDisplayName(plugin.getLocale().getMessage("interface.members.visitorsettingstitle").getMessage());
        List<String> visitorLore = new ArrayList<>();
        String[] visitorSplit = plugin.getLocale().getMessage("interface.members.visitorsettingslore").getMessage().split("\\|");
        for (String line : visitorSplit) visitorLore.add(line);
        visitorMeta.setLore(visitorLore);
        visitor.setItemMeta(visitorMeta);

        ItemStack member = new ItemStack(Material.PAINTING);
        ItemMeta memberMeta = visitor.getItemMeta();
        memberMeta.setDisplayName(plugin.getLocale().getMessage("interface.members.membersettingstitle").getMessage());
        List<String> memberLore = new ArrayList<>();
        String[] memberSplit = plugin.getLocale().getMessage("interface.members.membersettingslore").getMessage().split("\\|");
        for (String line : memberSplit) memberLore.add(line);
        memberMeta.setLore(memberLore);
        member.setItemMeta(memberMeta);

        ItemStack owner = new ItemStack(Material.ITEM_FRAME);
        ItemMeta ownerMeta = owner.getItemMeta();
        ownerMeta.setDisplayName(plugin.getLocale().getMessage("interface.members.ownersettingstitle").getMessage());
        List<String> ownerLore = new ArrayList<>();
        String[] ownerSplit = plugin.getLocale().getMessage("interface.members.ownersettingslore").getMessage().split("\\|");
        for (String line : ownerSplit) ownerLore.add(line);
        ownerMeta.setLore(ownerLore);
        owner.setItemMeta(ownerMeta);

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
        inventory.setItem(8, exit);
        inventory.setItem(3, type);
        inventory.setItem(4, stats);
        inventory.setItem(5, sort);
        inventory.setItem(37, previous);
        inventory.setItem(39, visitor);
        inventory.setItem(40, member);
        inventory.setItem(41, owner);
        inventory.setItem(43, next);

        // Pages + Member Skulls
    }

    @Override
    protected void registerClickables() {
        registerClickable(0, (player, inventory, cursor, slot, type) -> {
            // Should exit go back to the powercell or close the GUI?
        });

        registerClickable(8, (player, inventory, cursor, slot, type) -> {

        });

        registerClickable(3, (player, inventory, cursor, slot, type) -> {
            // Change Type - Show visitors, show members
        });

        registerClickable(5, (player, inventory, cursor, slot, type) -> {
            // Change Sort
        });

        registerClickable(37, (player, inventory, cursor, slot, type) -> {
            // Previous Page
        });

        registerClickable(39, (player, inventory, cursor, slot, type) -> {
            // Open visitor settings GUI.
        });

        registerClickable(40, (player, inventory, cursor, slot, type) -> {
            // Open member settings GUI.
        });

        registerClickable(41, (player, inventory, cursor, slot, type) -> {
            // Open owner settings GUI.
        });

        registerClickable(43, (player, inventory, cursor, slot, type) -> {
            // Open next page.
        });
    }

    @Override
    protected void registerOnCloses() {

    }
}
