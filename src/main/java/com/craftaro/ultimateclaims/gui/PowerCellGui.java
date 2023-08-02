package com.craftaro.ultimateclaims.gui;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimateclaims.settings.Settings;
import com.craftaro.core.gui.CustomizableGui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.hooks.EconomyManager;
import com.craftaro.core.input.ChatPrompt;
import com.craftaro.core.utils.NumberUtils;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.core.utils.TimeUtils;
import com.craftaro.ultimateclaims.UltimateClaims;
import com.craftaro.ultimateclaims.claim.Claim;
import com.craftaro.ultimateclaims.claim.PowerCell;
import com.craftaro.ultimateclaims.member.ClaimMember;
import com.craftaro.ultimateclaims.member.ClaimRole;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PowerCellGui extends CustomizableGui {
    private final UltimateClaims plugin;
    private final PowerCell powercell;
    private final Claim claim;
    private boolean fullPerms;

    public PowerCellGui(UltimateClaims plugin, Claim claim, Player player) {
        super(plugin, "powercell");

        this.plugin = plugin;
        this.powercell = claim.getPowerCell();
        this.claim = claim;
        this.fullPerms = claim.getOwner().getUniqueId().equals(player.getUniqueId());

        this.setRows(6);
        this.setTitle(TextUtils.formatText(claim.getName(), true));

        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        // Add access to audit log.
        if (!player.hasPermission("ultimateclaims.admin.nolog"))
            plugin.getAuditManager().addToAuditLog(claim, player.getUniqueId(), System.currentTimeMillis());

        // edges will be type 3
        setDefaultItem(glass3);

        // decorate corners
        mirrorFill("mirrorfill_1", 0, 0, true, true, glass2);
        mirrorFill("mirrorfill_2", 1, 0, true, true, glass2);
        mirrorFill("mirrorfill_3", 0, 1, true, true, glass2);

        if (Settings.ENABLE_FUEL.getBoolean()) {
            // buttons and icons at the top of the screen
            // Add/Display economy amount
            this.setButton("economy", 0, 2, XMaterial.SUNFLOWER.parseItem(),
                    (event) -> {
                        if (event.clickType == ClickType.LEFT) {
                            addEcon(event.player);
                        } else if (event.clickType == ClickType.RIGHT) {
                            takeEcon(event.player);
                        }
                    });

            // Display the total time
            this.setItem("time", 0, 4, XMaterial.CLOCK.parseItem());

            // Display the item amount
            this.setItem("item", 0, 6, XMaterial.DIAMOND.parseItem());
        }

        // buttons at the bottom of the screen
        // Bans
        if (fullPerms)
            this.setButton("bans", 5, 2, GuiUtils.createButtonItem(XMaterial.IRON_AXE,
                            plugin.getLocale().getMessage("interface.powercell.banstitle").getMessage(),
                            plugin.getLocale().getMessage("interface.powercell.banslore").getMessageLines()),
                    (event) -> {
                        closed();
                        event.manager.showGUI(event.player, new BansGui(plugin, claim));
                    });

        // Settings
        if (fullPerms)
            this.setButton("settings", 5, 3, GuiUtils.createButtonItem(XMaterial.REDSTONE,
                            plugin.getLocale().getMessage("interface.powercell.settingstitle").getMessage(),
                            plugin.getLocale().getMessage("interface.powercell.settingslore").getMessageLines()),
                    (event) -> {
                        closed();
                        event.manager.showGUI(event.player, new SettingsGui(plugin, claim, event.player));
                    });

        // Claim info
        this.setButton("information", 5, this.fullPerms ? 5 : 4, GuiUtils.createButtonItem(XMaterial.BOOK,
                plugin.getLocale().getMessage("interface.powercell.infotitle").getMessage(),
                plugin.getLocale().getMessage("interface.powercell.infolore")
                        .processPlaceholder("chunks", claim.getClaimSize())
                        .processPlaceholder("members", claim.getOwnerAndMembers().stream().filter(m -> m.getRole() == ClaimRole.MEMBER || m.getRole() == ClaimRole.OWNER).count())
                        .getMessage().split("\\|")), (event) -> {
            if (Settings.ENABLE_AUDIT_LOG.getBoolean()) {
                closed();
                event.manager.showGUI(event.player, new AuditGui(plugin, claim, event.player));
            }
        });

        // Members
        if (fullPerms)
            this.setButton("members", 5, 6, GuiUtils.createButtonItem(XMaterial.PAINTING,
                            plugin.getLocale().getMessage("interface.powercell.memberstitle").getMessage(),
                            plugin.getLocale().getMessage("interface.powercell.memberslore").getMessageLines()),
                    (event) -> {
                        closed();
                        event.manager.showGUI(event.player, new MembersGui(plugin, claim));
                    });

        ClaimMember member = claim.getMember(player);

        if (member != null && member.getRole() != ClaimRole.VISITOR
                || player.hasPermission("ultimateclaims.powercell.edit")) {
            // open inventory slots
            this.setAcceptsItems(true);
            for (int row = 1; row < rows - 1; ++row) {
                for (int col = 1; col < 8; ++col) {
                    this.setItem(row, col, AIR);
                    this.setUnlocked(row, col);
                }
            }
        }

        refresh();

        // events
        this.setOnOpen((event) -> refresh());
        this.setDefaultAction((event) -> refreshPower());
        this.setOnClose((event) -> closed());
    }

    private long lastUpdate = 0;

    private void refresh() {
        // don't allow spamming this function
        long now = System.currentTimeMillis();
        if (now - 1000 < lastUpdate) {
            return;
        }
        // update display inventory with the powercell's inventory
        updateGuiInventory(powercell.getItems());
        refreshPower();
        lastUpdate = now;
    }

    public void updateGuiInventory(List<ItemStack> items) {
        int j = 0;
        for (int i = 10; i < 44; i++) {
            if (i == 17
                    || i == 18
                    || i == 26
                    || i == 27
                    || i == 35
                    || i == 36) continue;
            if (items.size() <= j) {
                setItem(i, AIR);
                continue;
            }
            setItem(i, items.get(j));
            j++;
        }
    }

    private void refreshPower() {
        // don't allow spamming this function
        long now = System.currentTimeMillis();
        if (now - 2000 < lastUpdate) {
            return;
        }
        lastUpdate = now;

        // Economy amount
        if (Settings.ENABLE_FUEL.getBoolean())
            this.updateItem("economy", 0, 2,
                    plugin.getLocale().getMessage("interface.powercell.economytitle")
                            .processPlaceholder("time", TimeUtils.makeReadable((long) powercell.getEconomyPower() * 60 * 1000))
                            .processPlaceholder("balance", powercell.getEconomyBalance()).getMessage(),
                    plugin.getLocale().getMessage("interface.powercell.economylore")
                            .processPlaceholder("balance", powercell.getEconomyBalance()).getMessage().split("\\|"));

        // Display the total time
        if (Settings.ENABLE_FUEL.getBoolean())
            this.updateItem("time", 0, 4,
                    plugin.getLocale().getMessage("interface.powercell.totaltitle")
                            .processPlaceholder("time", TimeUtils.makeReadable((long) powercell.getTotalPower() * 60 * 1000)).getMessage(),
                    ChatColor.BLACK.toString());

        // Display the item amount
        if (Settings.ENABLE_FUEL.getBoolean())
            this.updateItem("item", 0, 6,
                    plugin.getLocale().getMessage("interface.powercell.valuablestitle")
                            .processPlaceholder("time", TimeUtils.makeReadable((long) powercell.getItemPower() * 60 * 1000)).getMessage(),
                    ChatColor.BLACK.toString());

        // buttons at the bottom of the screen
        // Claim info
        this.updateItem("information", 5, fullPerms ? 5 : 4,
                plugin.getLocale().getMessage("interface.powercell.infotitle").getMessage(),
                plugin.getLocale().getMessage("interface.powercell.infolore")
                        .processPlaceholder("chunks", claim.getClaimSize())
                        .processPlaceholder("members", claim.getOwnerAndMembers().stream().filter(m -> m.getRole() == ClaimRole.MEMBER || m.getRole() == ClaimRole.OWNER).count())
                        .getMessage().split("\\|"));
    }

    private void closed() {
        // update cell's inventory
        this.powercell.updateItemsFromGui(true);

        if (Settings.POWERCELL_HOLOGRAMS.getBoolean()) {
            this.powercell.updateHologram();
        }

        if (this.plugin.getDynmapManager() != null) {
            this.plugin.getDynmapManager().refreshDescription(this.claim);
        }

        this.powercell.rejectUnusable();
    }

    public Inventory getInventory() {
        return inventory;
    }

    private void addEcon(Player player) {
        player.closeInventory();

        ChatPrompt.showPrompt(plugin, player,
                plugin.getLocale().getMessage("interface.powercell.addfunds").getPrefixedMessage(),
                response -> {
                    if (!NumberUtils.isNumeric(response.getMessage())) {
                        plugin.getLocale().getMessage("general.notanumber")
                                .processPlaceholder("value", response.getMessage())
                                .sendPrefixedMessage(player);
                        return;
                    }
                    double amount = Double.parseDouble(response.getMessage().trim());
                    if (amount > 0 && powercell.hasLocation()) {
                        if (EconomyManager.hasBalance(player, amount)) {
                            EconomyManager.withdrawBalance(player, amount);
                            powercell.addEconomy(amount);
                            plugin.getDataHelper().updateClaim(claim);
                        } else {
                            plugin.getLocale().getMessage("general.notenoughfunds").sendPrefixedMessage(player);
                        }
                    }
                }).setOnClose(() -> {
            if (powercell.hasLocation()) {
                plugin.getGuiManager().showGUI(player, this);
            }
        }).setOnCancel(() -> player.sendMessage(ChatColor.RED + "Edit canceled"));
    }

    private void takeEcon(Player player) {
        player.closeInventory();

        ChatPrompt.showPrompt(plugin, player,
                plugin.getLocale().getMessage("interface.powercell.takefunds").getPrefixedMessage(),
                response -> {
                    if (!NumberUtils.isNumeric(response.getMessage())) {
                        plugin.getLocale().getMessage("general.notanumber")
                                .processPlaceholder("value", response.getMessage())
                                .sendPrefixedMessage(player);
                        return;
                    }
                    double amount = Double.parseDouble(response.getMessage().trim());
                    if (amount > 0 && powercell.hasLocation()) {
                        if (powercell.getEconomyBalance() >= amount) {
                            EconomyManager.deposit(player, amount);
                            powercell.removeEconomy(amount);
                            plugin.getDataHelper().updateClaim(claim);
                        } else {
                            plugin.getLocale().getMessage("general.notenoughfundspowercell")
                                    .processPlaceholder("balance", powercell.getEconomyBalance()).sendPrefixedMessage(player);
                        }
                    }
                }).setOnClose(() -> {
            if (powercell.hasLocation()) {
                plugin.getGuiManager().showGUI(player, this);
            }
        }).setOnCancel(() -> player.sendMessage(ChatColor.RED + "Edit canceled"));
    }
}
