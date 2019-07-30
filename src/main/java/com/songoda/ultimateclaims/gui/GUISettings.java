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

        init(plugin.getLocale().getMessage("interface.settings.title").getMessage(), 27);
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
        inventory.setItem(19, Methods.getBackgroundGlass(true));
        inventory.setItem(25, Methods.getBackgroundGlass(true));

        inventory.setItem(2, Methods.getBackgroundGlass(false));
        inventory.setItem(3, Methods.getBackgroundGlass(false));
        inventory.setItem(4, Methods.getBackgroundGlass(false));
        inventory.setItem(5, Methods.getBackgroundGlass(false));
        inventory.setItem(6, Methods.getBackgroundGlass(false));
        inventory.setItem(20, Methods.getBackgroundGlass(false));
        inventory.setItem(21, Methods.getBackgroundGlass(false));
        inventory.setItem(22, Methods.getBackgroundGlass(false));
        inventory.setItem(23, Methods.getBackgroundGlass(false));
        inventory.setItem(24, Methods.getBackgroundGlass(false));


        ItemStack exit = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.OAK_FENCE_GATE : Material.valueOf("FENCE_GATE"));
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.setDisplayName(plugin.getLocale().getMessage("general.interface.back").getMessage());
        exit.setItemMeta(exitMeta);

        int i = 11;
        if (player.hasPermission("ultimateclaims.toggle.hostilemobspawning")) {
            ItemStack hostileMobSpawning = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.ZOMBIE_SPAWN_EGG : Material.valueOf("MONSTER_EGG"));
            ItemMeta hostileMobSpawningMeta = hostileMobSpawning.getItemMeta();
            hostileMobSpawningMeta.setDisplayName(plugin.getLocale().getMessage("interface.settings.hostilemobspawningtitle").getMessage());
            List<String> hostileMobSpawningLore = new ArrayList<>();
            String[] hostileMobSpawningSplit = plugin.getLocale().getMessage("general.interface.current")
                    .processPlaceholder("current", claim.getClaimSettings().isHostileMobSpawning())
                    .getMessage().split("\\|");
            for (String line : hostileMobSpawningSplit) hostileMobSpawningLore.add(line);
            hostileMobSpawningMeta.setLore(hostileMobSpawningLore);
            hostileMobSpawning.setItemMeta(hostileMobSpawningMeta);

            registerClickable(i, (player, inventory, cursor, slot, type) -> {
                claim.getClaimSettings().setHostileMobSpawning(!claim.getClaimSettings().isHostileMobSpawning());
                constructGUI();
            });

            inventory.setItem(i, hostileMobSpawning);
            i++;
        }

        if (player.hasPermission("ultimateclaims.toggle.firespread")) {
            ItemStack fireSpread = new ItemStack(Material.FLINT_AND_STEEL);
            ItemMeta fireSpreadMeta = fireSpread.getItemMeta();
            fireSpreadMeta.setDisplayName(plugin.getLocale().getMessage("interface.settings.firespreadtitle").getMessage());
            List<String> fireSpreadLore = new ArrayList<>();
            String[] fireSpreadSplit = plugin.getLocale().getMessage("general.interface.current")
                    .processPlaceholder("current", claim.getClaimSettings().isFireSpread())
                    .getMessage().split("\\|");
            for (String line : fireSpreadSplit) fireSpreadLore.add(line);
            fireSpreadMeta.setLore(fireSpreadLore);
            fireSpread.setItemMeta(fireSpreadMeta);

            registerClickable(i, (player, inventory, cursor, slot, type) -> {
                claim.getClaimSettings().setFireSpread(!claim.getClaimSettings().isFireSpread());
                constructGUI();
            });

            inventory.setItem(i, fireSpread);
            i++;
        }

        if (player.hasPermission("ultimateclaims.toggle.pvp")) {
            ItemStack pvp = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta pvpMeta = pvp.getItemMeta();
            pvpMeta.setDisplayName(plugin.getLocale().getMessage("interface.settings.pvptitle").getMessage());
            List<String> pvpLore = new ArrayList<>();
            String[] pvpSplit = plugin.getLocale().getMessage("general.interface.current")
                    .processPlaceholder("current", claim.getClaimSettings().isPvp())
                    .getMessage().split("\\|");
            for (String line : pvpSplit) pvpLore.add(line);
            pvpMeta.setLore(pvpLore);
            pvp.setItemMeta(pvpMeta);

            registerClickable(i, (player, inventory, cursor, slot, type) -> {
                claim.getClaimSettings().setPvp(!claim.getClaimSettings().isPvp());
                constructGUI();
            });

            inventory.setItem(i, pvp);
            i++;
        }

        if (player.hasPermission("ultimateclaims.toggle.mobgriefing")) {
            ItemStack mobGriefing = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.GUNPOWDER : Material.valueOf("SULPHUR"));
            ItemMeta mobGriefingMeta = mobGriefing.getItemMeta();
            mobGriefingMeta.setDisplayName(plugin.getLocale().getMessage("interface.settings.mobgriefingtitle").getMessage());
            List<String> mobGriefingLore = new ArrayList<>();
            String[] mobGriefingSplit = plugin.getLocale().getMessage("general.interface.current")
                    .processPlaceholder("current", claim.getClaimSettings().isMobGriefing())
                    .getMessage().split("\\|");
            for (String line : mobGriefingSplit) mobGriefingLore.add(line);
            mobGriefingMeta.setLore(mobGriefingLore);
            mobGriefing.setItemMeta(mobGriefingMeta);

            registerClickable(i, (player, inventory, cursor, slot, type) -> {
                claim.getClaimSettings().setMobGriefing(!claim.getClaimSettings().isMobGriefing());
                constructGUI();
            });

            inventory.setItem(i, mobGriefing);
            i++;
        }

        if (player.hasPermission("ultimateclaims.toggle.leafdecay")) {
            ItemStack leafDecay = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.OAK_LEAVES : Material.valueOf("LEAVES"));
            ItemMeta leafDecayMeta = leafDecay.getItemMeta();
            leafDecayMeta.setDisplayName(plugin.getLocale().getMessage("interface.settings.leafdecaytitle").getMessage());
            List<String> leafDecayLore = new ArrayList<>();
            String[] leafDecaySplit = plugin.getLocale().getMessage("general.interface.current")
                    .processPlaceholder("current", claim.getClaimSettings().isLeafDecay())
                    .getMessage().split("\\|");
            for (String line : leafDecaySplit) leafDecayLore.add(line);
            leafDecayMeta.setLore(leafDecayLore);
            leafDecay.setItemMeta(leafDecayMeta);

            registerClickable(i, (player, inventory, cursor, slot, type) -> {
                claim.getClaimSettings().setLeafDecay(!claim.getClaimSettings().isLeafDecay());
                constructGUI();
            });

            inventory.setItem(i, leafDecay);
            i++;
        }


        inventory.setItem(0, exit);
        inventory.setItem(8, exit);

    }

    @Override
    protected void registerClickables() {
        registerClickable(0, (player, inventory, cursor, slot, type) ->
                new GUIPowerCell(player, claim));

        registerClickable(8, (player, inventory, cursor, slot, type) ->
                new GUIPowerCell(player, claim));


    }

    @Override
    protected void registerOnCloses() {

    }
}
