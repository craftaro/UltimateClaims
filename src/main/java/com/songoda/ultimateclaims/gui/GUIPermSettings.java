package com.songoda.ultimateclaims.gui;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.member.ClaimPerm;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.utils.Methods;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.ultimateclaims.utils.gui.AbstractGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GUIPermSettings extends AbstractGUI {

    private UltimateClaims plugin;
    private Player player;
    private Claim claim;
    private ClaimRole role;
    private boolean back;

    public GUIPermSettings(Player player, Claim claim, ClaimRole role, boolean back) {
        super(player);
        this.player = player;
        this.claim = claim;
        this.role = role;
        this.plugin = UltimateClaims.getInstance();
        this.back = back;

        init(Methods.formatTitle(plugin.getLocale().getMessage("interface.permsettings.title")
                .processPlaceholder("role", Methods.formatText(role.toString().toLowerCase(), true)).getMessage()), 27);
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


        ItemStack exit = new ItemStack(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.OAK_FENCE_GATE : Material.valueOf("FENCE_GATE"));
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.setDisplayName(plugin.getLocale().getMessage("general.interface.back").getMessage());
        exit.setItemMeta(exitMeta);

        ItemStack blockBreak = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta breakMeta = blockBreak.getItemMeta();
        breakMeta.setDisplayName(plugin.getLocale().getMessage("interface.permsettings.breaktitle").getMessage());
        List<String> breakLore = new ArrayList<>();
        String[] breakSplit = plugin.getLocale().getMessage("general.interface.current")
                .processPlaceholder("current", role == ClaimRole.MEMBER
                        ? claim.getMemberPermissions().hasPermission(ClaimPerm.BREAK) : claim.getVisitorPermissions().hasPermission(ClaimPerm.BREAK))
                .getMessage().split("\\|");
        breakLore.addAll(Arrays.asList(breakSplit));
        breakMeta.setLore(breakLore);
        blockBreak.setItemMeta(breakMeta);

        ItemStack place = new ItemStack(Material.STONE);
        ItemMeta placeMeta = place.getItemMeta();
        placeMeta.setDisplayName(plugin.getLocale().getMessage("interface.permsettings.placetitle").getMessage());
        List<String> placeLore = new ArrayList<>();
        String[] placeSplit = plugin.getLocale().getMessage("general.interface.current")
                .processPlaceholder("current", role == ClaimRole.MEMBER
                        ? claim.getMemberPermissions().hasPermission(ClaimPerm.PLACE) : claim.getVisitorPermissions().hasPermission(ClaimPerm.PLACE))
                .getMessage().split("\\|");
        placeLore.addAll(Arrays.asList(placeSplit));
        placeMeta.setLore(placeLore);
        place.setItemMeta(placeMeta);

        ItemStack interact = new ItemStack(Material.LEVER);
        ItemMeta interactMeta = interact.getItemMeta();
        interactMeta.setDisplayName(plugin.getLocale().getMessage("interface.permsettings.interacttitle").getMessage());
        List<String> interactLore = new ArrayList<>();
        String[] interactSplit = plugin.getLocale().getMessage("general.interface.current")
                .processPlaceholder("current", role == ClaimRole.MEMBER
                        ? claim.getMemberPermissions().hasPermission(ClaimPerm.INTERACT) : claim.getVisitorPermissions().hasPermission(ClaimPerm.INTERACT))
                .getMessage().split("\\|");
        interactLore.addAll(Arrays.asList(interactSplit));
        interactMeta.setLore(interactLore);
        interact.setItemMeta(interactMeta);

        ItemStack doors = new ItemStack(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.OAK_DOOR : Material.valueOf("WOOD_DOOR"));
        ItemMeta doorsMeta = doors.getItemMeta();
        doorsMeta.setDisplayName(plugin.getLocale().getMessage("interface.permsettings.doorstitle").getMessage());
        List<String> doorsLore = new ArrayList<>();
        String[] doorsSplit = plugin.getLocale().getMessage("general.interface.current")
                .processPlaceholder("current", role == ClaimRole.MEMBER
                        ? claim.getMemberPermissions().hasPermission(ClaimPerm.DOORS) : claim.getVisitorPermissions().hasPermission(ClaimPerm.DOORS))
                .getMessage().split("\\|");
        doorsLore.addAll(Arrays.asList(doorsSplit));
        doorsMeta.setLore(doorsLore);
        doors.setItemMeta(doorsMeta);

        ItemStack mobKill = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta mobKillMeta = mobKill.getItemMeta();
        mobKillMeta.setDisplayName(plugin.getLocale().getMessage("interface.permsettings.mobkilltitle").getMessage());
        List<String> mobKillLore = new ArrayList<>();
        String[] mobKillSplit = plugin.getLocale().getMessage("general.interface.current")
                .processPlaceholder("current", role == ClaimRole.MEMBER
                        ? claim.getMemberPermissions().hasPermission(ClaimPerm.MOB_KILLING) : claim.getVisitorPermissions().hasPermission(ClaimPerm.MOB_KILLING))
                .getMessage().split("\\|");
        mobKillLore.addAll(Arrays.asList(mobKillSplit));
        mobKillMeta.setLore(mobKillLore);
        mobKill.setItemMeta(mobKillMeta);

        ItemStack redstone = new ItemStack(Material.REDSTONE);
        ItemMeta redstoneMeta = redstone.getItemMeta();
        redstoneMeta.setDisplayName(plugin.getLocale().getMessage("interface.permsettings.redstonetitle").getMessage());
        List<String> redstoneLore = new ArrayList<>();
        String[] redstoneSplit = plugin.getLocale().getMessage("general.interface.current")
                .processPlaceholder("current", role == ClaimRole.MEMBER
                        ? claim.getMemberPermissions().hasPermission(ClaimPerm.REDSTONE) : claim.getVisitorPermissions().hasPermission(ClaimPerm.REDSTONE))
                .getMessage().split("\\|");
        redstoneLore.addAll(Arrays.asList(redstoneSplit));
        redstoneMeta.setLore(redstoneLore);
        redstone.setItemMeta(redstoneMeta);

        inventory.setItem(0, exit);
        inventory.setItem(8, exit);
        inventory.setItem(10, blockBreak);
        inventory.setItem(11, place);
        inventory.setItem(12, interact);
        inventory.setItem(14, doors);
        inventory.setItem(15, mobKill);
        inventory.setItem(16, redstone);
    }

    @Override
    protected void registerClickables() {
        registerClickable(0, (player, inventory, cursor, slot, type) ->
                new GUIMembers(player, claim, back));

        registerClickable(8, (player, inventory, cursor, slot, type) ->
                new GUIMembers(player, claim, back));

        registerClickable(10, (player, inventory, cursor, slot, type) -> {
            // Toggle block break perms
            if (role == ClaimRole.MEMBER) {
                claim.getMemberPermissions().setCanBreak(!claim.getMemberPermissions().hasPermission(ClaimPerm.BREAK));
                plugin.getDataManager().updatePermissions(claim, claim.getMemberPermissions(), ClaimRole.MEMBER);
            } else {
                claim.getVisitorPermissions().setCanBreak(!claim.getVisitorPermissions().hasPermission(ClaimPerm.BREAK));
                plugin.getDataManager().updatePermissions(claim, claim.getVisitorPermissions(), ClaimRole.VISITOR);
            }
            constructGUI();
        });

        registerClickable(11, (player, inventory, cursor, slot, type) -> {
            // Toggle block place perms
            if (role == ClaimRole.MEMBER) {
                claim.getMemberPermissions().setCanPlace(!claim.getMemberPermissions().hasPermission(ClaimPerm.PLACE));
                plugin.getDataManager().updatePermissions(claim, claim.getMemberPermissions(), ClaimRole.MEMBER);
            } else {
                claim.getVisitorPermissions().setCanPlace(!claim.getVisitorPermissions().hasPermission(ClaimPerm.PLACE));
                plugin.getDataManager().updatePermissions(claim, claim.getVisitorPermissions(), ClaimRole.VISITOR);
            }
            constructGUI();
        });

        registerClickable(12, (player, inventory, cursor, slot, type) -> {
            // Toggle block interact perms
            if (role == ClaimRole.MEMBER) {
                claim.getMemberPermissions().setCanInteract(!claim.getMemberPermissions().hasPermission(ClaimPerm.INTERACT));
                plugin.getDataManager().updatePermissions(claim, claim.getMemberPermissions(), ClaimRole.MEMBER);
            } else {
                claim.getVisitorPermissions().setCanInteract(!claim.getVisitorPermissions().hasPermission(ClaimPerm.INTERACT));
                plugin.getDataManager().updatePermissions(claim, claim.getVisitorPermissions(), ClaimRole.VISITOR);
            }
            constructGUI();
        });

        registerClickable(14, (player, inventory, cursor, slot, type) -> {
            // Toggle block interact perms
            if (role == ClaimRole.MEMBER) {
                claim.getMemberPermissions().setCanDoors(!claim.getMemberPermissions().hasPermission(ClaimPerm.DOORS));
                plugin.getDataManager().updatePermissions(claim, claim.getMemberPermissions(), ClaimRole.MEMBER);
            } else {
                claim.getVisitorPermissions().setCanDoors(!claim.getVisitorPermissions().hasPermission(ClaimPerm.DOORS));
                plugin.getDataManager().updatePermissions(claim, claim.getVisitorPermissions(), ClaimRole.VISITOR);
            }
            constructGUI();
        });

        registerClickable(15, (player, inventory, cursor, slot, type) -> {
            // Toggle block interact perms
            if (role == ClaimRole.MEMBER) {
                claim.getMemberPermissions().setCanMobKill(!claim.getMemberPermissions().hasPermission(ClaimPerm.MOB_KILLING));
                plugin.getDataManager().updatePermissions(claim, claim.getMemberPermissions(), ClaimRole.MEMBER);
            } else {
                claim.getVisitorPermissions().setCanMobKill(!claim.getVisitorPermissions().hasPermission(ClaimPerm.MOB_KILLING));
                plugin.getDataManager().updatePermissions(claim, claim.getVisitorPermissions(), ClaimRole.VISITOR);
            }
            constructGUI();
        });

        registerClickable(16, (player, inventory, cursor, slot, type) -> {
            // Toggle block interact perms
            if (role == ClaimRole.MEMBER) {
                claim.getMemberPermissions().setCanRedstone(!claim.getMemberPermissions().hasPermission(ClaimPerm.REDSTONE));
                plugin.getDataManager().updatePermissions(claim, claim.getMemberPermissions(), ClaimRole.MEMBER);
            } else {
                claim.getVisitorPermissions().setCanRedstone(!claim.getVisitorPermissions().hasPermission(ClaimPerm.REDSTONE));
                plugin.getDataManager().updatePermissions(claim, claim.getVisitorPermissions(), ClaimRole.VISITOR);
            }
            constructGUI();
        });
    }

    @Override
    protected void registerOnCloses() {

    }
}
