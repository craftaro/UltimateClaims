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

public class GUISettings extends AbstractGUI {

    private UltimateClaims plugin;
    private Player player;
    private Claim claim;
    private boolean back;

    public GUISettings(Player player, Claim claim, boolean back) {
        super(player);
        this.player = player;
        this.claim = claim;
        this.plugin = UltimateClaims.getInstance();
        this.back = back;

        init(plugin.getLocale().getMessage("interface.settings.title").getMessage(), 36);
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
        inventory.setItem(35, Methods.getBackgroundGlass(true));
        inventory.setItem(28, Methods.getBackgroundGlass(true));
        inventory.setItem(34, Methods.getBackgroundGlass(true));

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
        exitMeta.setDisplayName(plugin.getLocale().getMessage("general.interface.back").getMessage());
        exit.setItemMeta(exitMeta);

        if (player.hasPermission("ultimateclaims.toggle.hostilemobspawning")) {
            ItemStack hostileMobSpawning = new ItemStack(Material.ZOMBIE_SPAWN_EGG);
            ItemMeta hostileMobSpawningMeta = hostileMobSpawning.getItemMeta();
            hostileMobSpawningMeta.setDisplayName(plugin.getLocale().getMessage("interface.permsettings.hostilemobspawningtitle").getMessage());
            List<String> hostileMobSpawningLore = new ArrayList<>();
            String[] hostileMobSpawningSplit = plugin.getLocale().getMessage("general.interface.current")
                    .processPlaceholder("current", claim.getClaimSettings().isHostileMobSpawning())
                    .getMessage().split("\\|");
            for (String line : hostileMobSpawningSplit) hostileMobSpawningLore.add(line);
            hostileMobSpawningMeta.setLore(hostileMobSpawningLore);
            hostileMobSpawning.setItemMeta(hostileMobSpawningMeta);

            registerClickable(12, (player, inventory, cursor, slot, type) -> {
                claim.getClaimSettings().setHostileMobSpawning(!claim.getClaimSettings().isHostileMobSpawning());
                constructGUI();
            });

            inventory.setItem(12, hostileMobSpawning);
        }

        if (player.hasPermission("ultimateclaims.toggle.firespread")) {
            ItemStack fireSpread = new ItemStack(Material.FLINT_AND_STEEL);
            ItemMeta fireSpreadMeta = fireSpread.getItemMeta();
            fireSpreadMeta.setDisplayName(plugin.getLocale().getMessage("interface.permsettings.firespreadtitle").getMessage());
            List<String> fireSpreadLore = new ArrayList<>();
            String[] fireSpreadSplit = plugin.getLocale().getMessage("general.interface.current")
                    .processPlaceholder("current", claim.getClaimSettings().isFirespread())
                    .getMessage().split("\\|");
            for (String line : fireSpreadSplit) fireSpreadLore.add(line);
            fireSpreadMeta.setLore(fireSpreadLore);
            fireSpread.setItemMeta(fireSpreadMeta);

            registerClickable(13, (player, inventory, cursor, slot, type) -> {
                claim.getClaimSettings().setFirespread(!claim.getClaimSettings().isFirespread());
                constructGUI();
            });

            inventory.setItem(13, fireSpread);
        }


        inventory.setItem(0, exit);
        inventory.setItem(8, exit);

    }

    @Override
    protected void registerClickables() {
        registerClickable(0, (player, inventory, cursor, slot, type) ->
                new GUIMembers(player, claim, back));

        registerClickable(8, (player, inventory, cursor, slot, type) ->
                new GUIMembers(player, claim, back));


    }

    @Override
    protected void registerOnCloses() {

    }
}
