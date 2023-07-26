package com.craftaro.ultimateclaims.gui;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimateclaims.settings.Settings;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.gui.CustomizableGui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.core.utils.TimeUtils;
import com.craftaro.ultimateclaims.UltimateClaims;
import com.craftaro.ultimateclaims.claim.Audit;
import com.craftaro.ultimateclaims.claim.Claim;

import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class AuditGui extends CustomizableGui {
    private final UltimateClaims plugin;
    private final Claim claim;
    private final Player player;

    public AuditGui(UltimateClaims plugin, Claim claim, Player player) {
        super(plugin, "audits");
        this.plugin = plugin;
        this.claim = claim;
        this.player = player;
        this.setRows(6);
        this.setTitle(plugin.getLocale().getMessage("interface.audits.title").getMessage());
        this.showPage();
    }

    private void showPage() {
        reset();
        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial(XMaterial.BLUE_STAINED_GLASS_PANE));
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE));

        mirrorFill("mirrorfill_1", 0, 2, true, true, glass3);
        mirrorFill("mirrorfill_2", 1, 1, true, true, glass3);
        mirrorFill("mirrorfill_3", 0, 0, true, true, glass2);
        mirrorFill("mirrorfill_4", 1, 0, true, true, glass2);
        mirrorFill("mirrorfill_5", 0, 1, true, true, glass2);

        plugin.getAuditManager().getAudits(claim, audits -> {
            pages = (int) Math.max(1, Math.ceil(audits.size() / ((double) 28)));
            setNextPage(5, 7, GuiUtils.createButtonItem(XMaterial.ARROW, plugin.getLocale().getMessage("general.interface.next").getMessage()));
            setPrevPage(5, 1, GuiUtils.createButtonItem(XMaterial.ARROW, plugin.getLocale().getMessage("general.interface.previous").getMessage()));
            setOnPage(event -> showPage());
            setButton("exit", 8, GuiUtils.createButtonItem(XMaterial.OAK_DOOR, plugin.getLocale().getMessage("general.interface.exit").getMessage()), (event) -> player.closeInventory());
            List<Audit> entries = audits.stream().skip((page - 1) * 28).limit(28L).collect(Collectors.toList());
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                int num = 11;
                for (Audit entry : entries) {
                    if (num == 16 || num == 36)
                        num += 2;

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getWho());
                    ItemStack itemStack = GuiUtils.createButtonItem(XMaterial.PLAYER_HEAD, plugin.getLocale().getMessage("interface.audits.entryname")
                                    .processPlaceholder("who", offlinePlayer.getName())
                                    .processPlaceholder("when", TimeUtils.makeReadable(System.currentTimeMillis() - entry.getWhen()))
                                    .getMessage(),
                            plugin.getLocale().getMessage("interface.audits.entrylore")
                                    .processPlaceholder("who", offlinePlayer.getName())
                                    .processPlaceholder("when", TimeUtils.makeReadable(System.currentTimeMillis() - entry.getWhen()))
                                    .getMessage().split("\\|"));

                    SkullMeta meta = (SkullMeta)itemStack.getItemMeta();
                    if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13))
                        meta.setOwningPlayer(offlinePlayer);
                    else
                        meta.setOwner(offlinePlayer.getName());

                    itemStack.setItemMeta(meta);
                    setItem(num, itemStack);
                    num++;
                }
                update();
            });
        });
    }
}
