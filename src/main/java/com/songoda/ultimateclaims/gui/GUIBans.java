package com.songoda.ultimateclaims.gui;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.utils.Methods;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.ultimateclaims.utils.gui.AbstractGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GUIBans extends AbstractGUI {

    private UltimateClaims plugin;
    private Claim claim;
    private boolean back;
    private int page = 1;

    public GUIBans(Player player, Claim claim, boolean back) {
        super(player);
        this.claim = claim;
        this.plugin = UltimateClaims.getInstance();
        this.back = back;

        init(Methods.formatTitle(plugin.getLocale().getMessage("interface.bans.title").getMessage()), 54);
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
        inventory.setItem(3, Methods.getBackgroundGlass(false));
        inventory.setItem(5, Methods.getBackgroundGlass(false));
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

        ItemStack exit = new ItemStack(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.OAK_FENCE_GATE : Material.valueOf("FENCE_GATE"));
        ItemMeta exitMeta = exit.getItemMeta();
        if (back) exitMeta.setDisplayName(plugin.getLocale().getMessage("general.interface.back").getMessage());
        else exitMeta.setDisplayName(plugin.getLocale().getMessage("general.interface.exit").getMessage());
        exit.setItemMeta(exitMeta);

        ItemStack info = new ItemStack(Material.PAINTING);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(plugin.getLocale().getMessage("interface.bans.infotitle").getMessage());
        List<String> infoLore = new ArrayList<>();
        String[] infoSplit = plugin.getLocale().getMessage("interface.bans.infolore")
                .processPlaceholder("bancount", claim.getBannedPlayers().size()).getMessage().split("\\|");
        for (String line : infoSplit) infoLore.add(line);
        infoMeta.setLore(infoLore);
        info.setItemMeta(infoMeta);

        ItemStack previous = new ItemStack(Material.MAP);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.setDisplayName(plugin.getLocale().getMessage("general.interface.previous").getMessage());
        previous.setItemMeta(previousMeta);

        ItemStack next = new ItemStack(Material.PAPER);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(plugin.getLocale().getMessage("general.interface.next").getMessage());
        next.setItemMeta(nextMeta);

        inventory.setItem(0, exit);
        inventory.setItem(4, info);
        inventory.setItem(8, exit);

        List<UUID> toDisplay = new ArrayList<>(claim.getBannedPlayers());

        if (page > 1) {
            inventory.setItem(37, previous);
            registerClickable(37, (player, inventory, cursor, slot, type) -> {
                page--;
                constructGUI();
            });
        }
        if (page < (int) Math.ceil(toDisplay.size() / 21) + 1) {
            inventory.setItem(43, next);
            registerClickable(43, (player, inventory, cursor, slot, type) -> {
                page++;
                constructGUI();
            });
        }

        int current = page == 1 ? 0 : 21 * (page - 1);
        for (int i = 1; i < 4; i++) {
            for (int j = 1; j < 8; j++) {
                if (toDisplay.size() - 1 < current) return;

                OfflinePlayer skullPlayer = Bukkit.getOfflinePlayer(toDisplay.get(current));

                ItemStack skull = new ItemStack(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13) ?
                        Material.PLAYER_HEAD : Material.valueOf("SKULL"));
                if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) skull.setDurability((short) 3);
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13))
                    skullMeta.setOwningPlayer(skullPlayer);
                else
                    skullMeta.setOwner(skullPlayer.getName());
                skullMeta.setDisplayName(Methods.formatText("&b") + skullPlayer.getName());
                List<String> lore = new ArrayList<>();
                String[] skullSplit = plugin.getLocale().getMessage("interface.bans.skulllore")
                        .getMessage().split("\\|");
                for (String line : skullSplit) lore.add(line);
                skullMeta.setLore(lore);
                skull.setItemMeta(skullMeta);

                inventory.setItem((9*i) + j, skull);
                current++;

                final UUID playerUUID = skullPlayer.getUniqueId();

                registerClickable((9 * i) + j, (player, inventory, cursor, slot, type) -> {
                    claim.unBanPlayer(playerUUID);
                    constructGUI();
                });
            }
        }
    }

    @Override
    protected void registerClickables() {
        registerClickable(0, (player, inventory, cursor, slot, type) -> {
            if (back)
                new GUIPowerCell(player, claim);
            else
                player.closeInventory();
        });

        registerClickable(8, (player, inventory, cursor, slot, type) -> {
            if (back)
                new GUIPowerCell(player, claim);
            else
                player.closeInventory();
        });
    }

    @Override
    protected void registerOnCloses() {

    }
}
