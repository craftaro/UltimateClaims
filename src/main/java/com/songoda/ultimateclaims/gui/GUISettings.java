package com.songoda.ultimateclaims.gui;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.utils.Methods;
import com.songoda.ultimateclaims.utils.ServerVersion;
import com.songoda.ultimateclaims.utils.gui.AbstractGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUISettings extends AbstractGUI {

    private UltimateClaims plugin;
    private Player player;
    private Claim claim;
    private ClaimRole role;

    public GUISettings(Player player, Claim claim, ClaimRole role) {
        super(player);
        this.player = player;
        this.claim = claim;
        this.role = role;
        this.plugin = UltimateClaims.getInstance();

        init(Methods.formatTitle(plugin.getLocale().getMessage("interface.settings.title")
                .processPlaceholder("role", role.toString().toLowerCase()).getMessage()), 36);
    }

    @Override
    protected void constructGUI() {
        inventory.clear();
        resetClickables();
        registerClickables();

        inventory.setItem(1, Methods.getBackgroundGlass(true));
        inventory.setItem(7, Methods.getBackgroundGlass(true));
        inventory.setItem(9, Methods.getBackgroundGlass(true));
        inventory.setItem(17, Methods.getBackgroundGlass(true));
        inventory.setItem(18, Methods.getBackgroundGlass(true));
        inventory.setItem(26, Methods.getBackgroundGlass(true));
        inventory.setItem(27, Methods.getBackgroundGlass(true));
        inventory.setItem(28, Methods.getBackgroundGlass(true));
        inventory.setItem(34, Methods.getBackgroundGlass(true));
        inventory.setItem(35, Methods.getBackgroundGlass(true));

        inventory.setItem(2, Methods.getBackgroundGlass(false));
        inventory.setItem(3, Methods.getBackgroundGlass(false));
        inventory.setItem(4, Methods.getBackgroundGlass(false));
        inventory.setItem(5, Methods.getBackgroundGlass(false));
        inventory.setItem(6, Methods.getBackgroundGlass(false));
        inventory.setItem(29, Methods.getBackgroundGlass(false));
        inventory.setItem(30, Methods.getBackgroundGlass(false));
        inventory.setItem(31, Methods.getBackgroundGlass(false));
        inventory.setItem(32, Methods.getBackgroundGlass(false));
        inventory.setItem(33, Methods.getBackgroundGlass(false));


        ItemStack exit = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.OAK_FENCE_GATE : Material.valueOf("FENCE_GATE"));
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.setDisplayName(plugin.getLocale().getMessage("interface.settings.exittitle").getMessage());
        List<String> exitLore = new ArrayList<>();
        String[] exitSplit = plugin.getLocale().getMessage("interface.settings.exitlore").getMessage().split("\\|");
        for (String line : exitSplit) exitLore.add(line);
        exitMeta.setLore(exitLore);
        exit.setItemMeta(exitMeta);

        ItemStack blockBreak = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta breakMeta = blockBreak.getItemMeta();
        breakMeta.setDisplayName(plugin.getLocale().getMessage("interface.settings.breaktitle").getMessage());
        List<String> breakLore = new ArrayList<>();
        String[] breakSplit = plugin.getLocale().getMessage("interface.settings.breaklore").getMessage().split("\\|");
        for (String line : breakSplit) breakLore.add(line);
        breakMeta.setLore(breakLore);
        blockBreak.setItemMeta(breakMeta);

        ItemStack place = new ItemStack(Material.STONE);
        ItemMeta placeMeta = place.getItemMeta();
        placeMeta.setDisplayName(plugin.getLocale().getMessage("interface.settings.placetitle").getMessage());
        List<String> placeLore = new ArrayList<>();
        String[] placeSplit = plugin.getLocale().getMessage("interface.settings.placelore").getMessage().split("\\|");
        for (String line : placeSplit) placeLore.add(line);
        placeMeta.setLore(placeLore);
        place.setItemMeta(placeMeta);

        ItemStack interact = new ItemStack(Material.LEVER);
        ItemMeta interactMeta = interact.getItemMeta();
        interactMeta.setDisplayName(plugin.getLocale().getMessage("interface.settings.interacttitle").getMessage());
        List<String> interactLore = new ArrayList<>();
        String[] interactSplit = plugin.getLocale().getMessage("interface.settings.itneractlore").getMessage().split("\\|");
        for (String line : interactSplit) interactLore.add(line);
        interactMeta.setLore(interactLore);
        interact.setItemMeta(interactMeta);

        inventory.setItem(0, exit);
        inventory.setItem(8, exit);
        inventory.setItem(11, blockBreak);
        inventory.setItem(13, place);
        inventory.setItem(15, interact);
    }

    @Override
    protected void registerClickables() {
        registerClickable(0, (player, inventory, cursor, slot, type) -> {
            // Should exit go back to the members GUI or close the GUI?
        });

        registerClickable(8, (player, inventory, cursor, slot, type) -> {
            // ^
        });

        registerClickable(11, (player, inventory, cursor, slot, type) -> {
            // Toggle block break perms
        });

        registerClickable(13, (player, inventory, cursor, slot, type) -> {
            // Toggle block place perms.
        });

        registerClickable(15, (player, inventory, cursor, slot, type) -> {
            //
        });
    }

    @Override
    protected void registerOnCloses() {

    }
}
