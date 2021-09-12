package com.songoda.ultimateclaims.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.CustomizableGui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.ItemUtils;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BansGui extends CustomizableGui {

    private final UltimateClaims plugin;
    private final Claim claim;

    public BansGui(UltimateClaims plugin, Claim claim) {
        super(plugin, "bans");
        this.claim = claim;
        this.plugin = plugin;
        setRows(6);
        this.setTitle(plugin.getLocale().getMessage("interface.bans.title").getMessage());

        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        // edges will be type 3
        setDefaultItem(glass3);

        // decorate corners
        mirrorFill("mirrorfill_1", 0, 0, true, true, glass2);
        mirrorFill("mirrorfill_2", 1, 0, true, true, glass2);
        mirrorFill("mirrorfill_2", 0, 1, true, true, glass2);

        // exit buttons
        this.setButton("back", 0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE,
                plugin.getLocale().getMessage("general.interface.back").getMessage(),
                plugin.getLocale().getMessage("general.interface.backlore").getMessage()),
                (event) -> guiManager.showGUI(event.player, claim.getPowerCell().getGui(event.player)));
        this.setButton("back",8, this.getItem(0),
                (event) -> guiManager.showGUI(event.player, claim.getPowerCell().getGui(event.player)));

        // Ban information
        this.setItem("information", 4, GuiUtils.createButtonItem(CompatibleMaterial.PAINTING,
                plugin.getLocale().getMessage("interface.bans.infotitle").getMessage(),
                plugin.getLocale().getMessage("interface.bans.infolore")
                        .processPlaceholder("bancount", claim.getBannedPlayers().size()).getMessage().split("\\|")));

        // enable page events
        setNextPage(5, 6, GuiUtils.createButtonItem(CompatibleMaterial.ARROW, plugin.getLocale().getMessage("general.interface.next").getMessage()));
        setPrevPage(5, 2, GuiUtils.createButtonItem(CompatibleMaterial.ARROW, plugin.getLocale().getMessage("general.interface.previous").getMessage()));
        setOnPage((event) -> showPage());
        showPage();
    }

    private void showPage() {
        List<UUID> toDisplay = new ArrayList<>(claim.getBannedPlayers());
        this.pages = (int) Math.max(1, Math.ceil(toDisplay.size() / (7 * 4)));
        this.page = Math.max(page, pages);
        int current = 21 * (page - 1);
        for (int row = 1; row < rows - 1; row++) {
            for (int col = 1; col < 8; col++) {
                if (toDisplay.size() - 1 < current) {
                    this.clearActions(row, col);
                    this.setItem(row, col, AIR);
                    continue;
                }

                OfflinePlayer skullPlayer = Bukkit.getOfflinePlayer(toDisplay.get(current));
                final UUID playerUUID = skullPlayer.getUniqueId();

                this.setButton(row, col, GuiUtils.createButtonItem(ItemUtils.getPlayerSkull(skullPlayer),
                        ChatColor.AQUA + skullPlayer.getName(),
                        plugin.getLocale().getMessage("interface.bans.skulllore").getMessage().split("\\|")),
                        (event) -> {
                            claim.unBanPlayer(playerUUID);
                            showPage();
                        });

                current++;
            }
        }
    }
}
