package com.songoda.ultimateclaims.gui;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.PowerCell;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.utils.AbstractChatConfirm;
import com.songoda.ultimateclaims.utils.Methods;
import com.songoda.ultimateclaims.utils.ServerVersion;
import com.songoda.ultimateclaims.utils.gui.AbstractGUI;
import com.songoda.ultimateclaims.utils.gui.Range;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIPowerCell extends AbstractGUI {

    private UltimateClaims plugin;
    private PowerCell powercell;
    private Claim claim;

    private int task = -1;

    public GUIPowerCell(Player player, Claim claim) {
        super(player);
        this.powercell = claim.getPowerCell();
        this.claim = claim;
        plugin = UltimateClaims.getInstance();

        if (powercell.getOpened() == null) {
            init(Methods.formatTitle(claim.getName()), 54);
        } else {
            this.inventory = powercell.getOpened();
            player.openInventory(inventory);
            constructGUI();
            registerOnCloses();
        }
        runTask();
    }

    @Override
    protected void constructGUI() {
        if (powercell.getOpened() == null)
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

        this.createButtons();

        powercell.updateInventory(inventory);
        if (powercell.getOpened() == null)
            this.powercell.setOpened(this.inventory);
    }

    private void createButtons() {
        ItemStack economy = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.SUNFLOWER : Material.valueOf("DOUBLE_PLANT"));
        ItemMeta economyMeta = economy.getItemMeta();
        economyMeta.setDisplayName(plugin.getLocale().getMessage("interface.powercell.economytitle")
                .processPlaceholder("time", Methods.makeReadable((long) powercell.getEconomyPower() * 60 * 1000)).getMessage());
        List<String> economyLore = new ArrayList<>();
        String[] economySplit = plugin.getLocale().getMessage("interface.powercell.economylore").getMessage().split("\\|");
        for (String line : economySplit) economyLore.add(Methods.formatText(line));
        economyMeta.setLore(economyLore);
        economy.setItemMeta(economyMeta);

        ItemStack total = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.CLOCK : Material.valueOf("WATCH"));
        ItemMeta totalMeta = total.getItemMeta();
        totalMeta.setDisplayName(plugin.getLocale().getMessage("interface.powercell.totaltitle")
                .processPlaceholder("time", Methods.makeReadable((long) powercell.getTotalPower() * 60 * 1000)).getMessage());
        total.setItemMeta(totalMeta);

        ItemStack valuables = new ItemStack(Material.DIAMOND);
        ItemMeta valuablesMeta = valuables.getItemMeta();
        valuablesMeta.setDisplayName(plugin.getLocale().getMessage("interface.powercell.valuablestitle")
                .processPlaceholder("time", Methods.makeReadable((long) powercell.getItemPower() * 60 * 1000)).getMessage());
        valuables.setItemMeta(valuablesMeta);

        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(plugin.getLocale().getMessage("interface.powercell.infotitle").getMessage());
        List<String> infoLore = new ArrayList<>();
        String[] infoSplit = plugin.getLocale().getMessage("interface.powercell.infolore")
                .processPlaceholder("chunks", claim.getClaimSize())
                .processPlaceholder("members",
                        claim.getOwnerAndMembers().stream().filter(m -> m.getRole() == ClaimRole.MEMBER || m.getRole() == ClaimRole.OWNER).count())
                .getMessage().split("\\|");
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

        ItemStack banned = new ItemStack(Material.IRON_AXE);
        ItemMeta bannedMeta = banned.getItemMeta();
        bannedMeta.setDisplayName(plugin.getLocale().getMessage("interface.powercell.banstitle").getMessage());
        List<String> bannedLore = new ArrayList<>();
        String[] bannedSplit = plugin.getLocale().getMessage("interface.powercell.banslore").getMessage().split("\\|");
        for (String line : bannedSplit) bannedLore.add(line);
        bannedMeta.setLore(bannedLore);
        banned.setItemMeta(bannedMeta);

        ItemStack settings = new ItemStack(Material.REDSTONE);
        ItemMeta settingsMeta = settings.getItemMeta();
        settingsMeta.setDisplayName(plugin.getLocale().getMessage("interface.powercell.settingstitle").getMessage());
        List<String> settingsLore = new ArrayList<>();
        String[] settingsSplit = plugin.getLocale().getMessage("interface.powercell.settingslore").getMessage().split("\\|");
        for (String line : settingsSplit) settingsLore.add(line);
        settingsMeta.setLore(settingsLore);
        settings.setItemMeta(settingsMeta);

        inventory.setItem(2, economy);
        inventory.setItem(4, total);
        inventory.setItem(6, valuables);
        inventory.setItem(50, info);
        inventory.setItem(51, members);
        inventory.setItem(47, banned);
        inventory.setItem(48, settings);
    }

    private void runTask() {
        this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (!inventory.getViewers().contains(player))
                Bukkit.getScheduler().cancelTask(task);
            this.powercell.updateItems();
            this.createButtons();
            if (plugin.getHologram() != null)
                plugin.getHologram().update(powercell);
        }, 5L, 5L);
    }

    @Override
    protected void registerClickables() {
        addDraggable(new Range(10, 16, null, true), true);
        addDraggable(new Range(19, 25, null, true), true);
        addDraggable(new Range(28, 34, null, true), true);
        addDraggable(new Range(37, 43, null, true), true);
        registerClickable(2, (player, inventory, cursor, slot, type) -> {
            plugin.getLocale().getMessage("interface.powercell.addfunds").sendPrefixedMessage(player);
            AbstractChatConfirm abstractChatConfirm = new AbstractChatConfirm(player, event ->
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (!Methods.isNumeric(event.getMessage())) {
                            plugin.getLocale().getMessage("general.notanumber")
                                    .processPlaceholder("value", event.getMessage())
                                    .sendPrefixedMessage(player);
                            return;
                        }
                        double amount = Double.parseDouble(event.getMessage());
                        if (amount < 1) return;
                        if (plugin.getEconomy().hasBalance(player, amount)) {
                            plugin.getEconomy().withdrawBalance(player, amount);
                            powercell.addEconomy(amount);
                            plugin.getDataManager().updateClaim(claim);
                        } else {
                            plugin.getLocale().getMessage("general.notenoughfunds").sendPrefixedMessage(player);
                        }
                    }, 0L));

            abstractChatConfirm.setOnClose(() -> new GUIPowerCell(player, claim));
        });

        registerClickable(51, (player, inventory, cursor, slot, type)
                -> new GUIMembers(player, claim, true));

        registerClickable(47, (player, inventory, cursor, slot, type)
                -> new GUIBans(player, claim, true));

        registerClickable(48, (player, inventory, cursor, slot, type)
                -> new GUISettings(player, claim, true));
    }

    @Override
    protected void registerOnCloses() {
        registerOnClose((player, inventory) -> this.powercell.rejectUnusable());
    }
}
