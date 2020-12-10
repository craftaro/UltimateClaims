package com.songoda.ultimateclaims.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.ItemUtils;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.member.ClaimMember;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.settings.Settings;
import com.songoda.ultimateclaims.utils.Methods;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class MembersGui extends Gui {

    private UltimateClaims plugin;
    private Claim claim;
    private ClaimRole displayedRole = ClaimRole.OWNER;
    private SortType sortType = SortType.DEFAULT;

    public MembersGui(Claim claim, Gui returnGui) {
        super(returnGui);
        this.claim = claim;
        this.plugin = UltimateClaims.getInstance();
        this.setRows(6);
        this.setTitle(Methods.formatTitle(plugin.getLocale().getMessage("interface.members.title").getMessage()));

        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        // edges will be type 3
        setDefaultItem(glass3);

        // decorate corners
        mirrorFill(0, 0, true, true, glass2);
        mirrorFill(1, 0, true, true, glass2);
        mirrorFill(0, 1, true, true, glass2);

        // exit buttons
        this.setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE,
                plugin.getLocale().getMessage("general.interface.back").getMessage(),
                plugin.getLocale().getMessage("general.interface.exit").getMessage()),
                (event) -> event.player.closeInventory());
        this.setButton(8, this.getItem(0), (event) -> event.player.closeInventory());

        // Member Stats (update on refresh)
        this.setItem(4, CompatibleMaterial.PAINTING.getItem());

        // Filters
        this.setButton(3, CompatibleMaterial.HOPPER.getItem(), (event) -> toggleFilterType());
        this.setButton(5, CompatibleMaterial.HOPPER.getItem(), (event) -> toggleSort());

        // Settings shortcuts
        this.setButton(5, 3, GuiUtils.createButtonItem(CompatibleMaterial.OAK_SIGN,
                plugin.getLocale().getMessage("interface.members.visitorsettingstitle").getMessage(),
                plugin.getLocale().getMessage("interface.members.visitorsettingslore").getMessage().split("\\|")),
                (event) -> event.manager.showGUI(event.player, new SettingsMemberGui(claim, this, ClaimRole.VISITOR)));

        this.setButton(5, 5, GuiUtils.createButtonItem(CompatibleMaterial.PAINTING,
                plugin.getLocale().getMessage("interface.members.membersettingstitle").getMessage(),
                plugin.getLocale().getMessage("interface.members.membersettingslore").getMessage().split("\\|")),
                (event) -> event.manager.showGUI(event.player, new SettingsMemberGui(claim, this, ClaimRole.MEMBER)));

        // enable page events
        setNextPage(5, 6, GuiUtils.createButtonItem(CompatibleMaterial.ARROW, plugin.getLocale().getMessage("general.interface.next").getMessage()));
        setPrevPage(5, 2, GuiUtils.createButtonItem(CompatibleMaterial.ARROW, plugin.getLocale().getMessage("general.interface.previous").getMessage()));
        setOnPage((event) -> showPage());
        showPage();
    }

    void showPage() {
        // refresh stats
        this.setItem(4, GuiUtils.updateItem(this.getItem(4),
                plugin.getLocale().getMessage("interface.members.statstitle").getMessage(),
                plugin.getLocale().getMessage("interface.members.statslore")
                        .processPlaceholder("totalmembers", claim.getOwnerAndMembers().size())
                        .processPlaceholder("maxmembers", Settings.MAX_MEMBERS.getInt())
                        .processPlaceholder("members", claim.getMembers().size()).getMessage().split("\\|")));

        // Filters
        this.setItem(3, GuiUtils.updateItem(this.getItem(3),
                plugin.getLocale().getMessage("interface.members.changetypetitle").getMessage(),
                plugin.getLocale().getMessage("general.interface.current")
                        .processPlaceholder("current",
                                Methods.formatText(displayedRole == ClaimRole.OWNER ? "ALL" : displayedRole.toString(), true))
                        .getMessage().split("\\|")));
        this.setItem(5, GuiUtils.updateItem(this.getItem(5),
                plugin.getLocale().getMessage("interface.members.changesorttitle").getMessage(),
                plugin.getLocale().getMessage("general.interface.current")
                        .processPlaceholder("current",
                                Methods.formatText(sortType.toString().replace('_', ' '), true))
                        .getMessage().split("\\|")));

        // show members
        List<ClaimMember> toDisplay = new ArrayList<>(claim.getOwnerAndMembers());
        toDisplay = toDisplay.stream()
                .filter(m -> m.getRole() == displayedRole || displayedRole == ClaimRole.OWNER)
                .sorted(Comparator.comparingInt(claimMember -> claimMember.getRole().getIndex()))
                .collect(Collectors.toList());

        if (sortType == SortType.PLAYTIME) {
            toDisplay = toDisplay.stream().sorted(Comparator.comparingLong(ClaimMember::getPlayTime))
                    .collect(Collectors.toList());
        }
        if (sortType == SortType.MEMBER_SINCE) {
            toDisplay = toDisplay.stream().sorted(Comparator.comparingLong(ClaimMember::getPlayTime))
                    .collect(Collectors.toList());
        }

        Collections.reverse(toDisplay);
        this.pages = (int) Math.max(1, Math.ceil(toDisplay.size() / (7 * 4)));
        this.page = Math.max(page, pages);

        int currentMember = 21 * (page - 1);
        for (int row = 1; row < rows - 1; row++) {
            for (int col = 1; col < 8; col++) {
                if (toDisplay.size() - 1 < currentMember) {
                    this.clearActions(row, col);
                    this.setItem(row, col, AIR);
                    continue;
                }

                ClaimMember claimMember = toDisplay.get(currentMember);
                final UUID playerUUID = toDisplay.get(currentMember).getUniqueId();
                OfflinePlayer skullPlayer = Bukkit.getOfflinePlayer(playerUUID);

                this.setItem(row, col, GuiUtils.createButtonItem(ItemUtils.getPlayerSkull(skullPlayer),
                        ChatColor.AQUA + skullPlayer.getName(),
                        plugin.getLocale().getMessage("interface.members.skulllore")
                                .processPlaceholder("role",
                                        Methods.formatText(toDisplay.get(currentMember).getRole().toString().toLowerCase(), true))
                                .processPlaceholder("playtime", Methods.makeReadable(claimMember.getPlayTime()))
                                .processPlaceholder("membersince",
                                        new SimpleDateFormat("dd/MM/yyyy").format(new Date(claimMember.getMemberSince())))
                                .getMessage().split("\\|")));

                currentMember++;
            }
        }
    }

    void toggleFilterType() {
        switch (displayedRole) {
            case OWNER:
                displayedRole = ClaimRole.VISITOR;
                break;
            case MEMBER:
                displayedRole = ClaimRole.OWNER;
                break;
            case VISITOR:
                displayedRole = ClaimRole.MEMBER;
                break;
        }
        showPage();
    }

    void toggleSort() {
        switch (sortType) {
            case DEFAULT:
                sortType = SortType.PLAYTIME;
                break;
            case PLAYTIME:
                sortType = SortType.MEMBER_SINCE;
                break;
            case MEMBER_SINCE:
                sortType = SortType.DEFAULT;
                break;
        }
        showPage();
    }

    public static enum SortType {
        DEFAULT,
        PLAYTIME,
        MEMBER_SINCE
    }

}
