package com.songoda.ultimateclaims.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.CustomizableGui;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.member.ClaimPerm;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.settings.Settings;
import org.bukkit.inventory.ItemStack;

public class SettingsMemberGui extends CustomizableGui {

    private final UltimateClaims plugin;
    private final Claim claim;
    private final ClaimRole role;

    public SettingsMemberGui(UltimateClaims plugin, Claim claim, Gui returnGui, ClaimRole type) {
        super(plugin, "membersettings");
        this.claim = claim;
        this.role = type;
        this.plugin = plugin;
        this.setRows(3);
        this.setTitle(plugin.getLocale().getMessage("interface.permsettings.title")
                .processPlaceholder("role", TextUtils.formatText(role.toString().toLowerCase(), true)).getMessage());

        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        // edges will be type 3
        setDefaultItem(glass3);

        mirrorFill("mirrorfill_1", 0, 0, true, true, glass2);
        mirrorFill("mirrorfill_2", 1, 0, true, true, glass2);
        mirrorFill("mirrorfill_3", 0, 1, true, true, glass2);

        // close button
        this.setButton("close", 8, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE,
                plugin.getLocale().getMessage("general.interface.close").getMessage(),
                plugin.getLocale().getMessage("general.interface.closedescription").getMessage()),
                (event) -> event.player.closeInventory());

        // back button
        this.setButton("back", 0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE,
                plugin.getLocale().getMessage("general.interface.back").getMessage(),
                plugin.getLocale().getMessage("general.interface.backdescription").getMessage()),
                (event) -> guiManager.showGUI(event.player, claim.getPowerCell().getGui(event.player)));

        // settings
        this.setButton("break", 1, 1, CompatibleMaterial.IRON_PICKAXE.getItem(), (event) -> toggle(ClaimPerm.BREAK));
        this.setButton("place", 1, 2, CompatibleMaterial.STONE.getItem(), (event) -> toggle(ClaimPerm.PLACE));
        this.setButton("interact", 1, 3, CompatibleMaterial.LEVER.getItem(), (event) -> toggle(ClaimPerm.INTERACT));
        this.setButton("trading", 1, 4, CompatibleMaterial.EMERALD.getItem(), (event) -> toggle(ClaimPerm.TRADING));
        this.setButton("doors", 1, 5, CompatibleMaterial.OAK_DOOR.getItem(), (event) -> toggle(ClaimPerm.DOORS));
        this.setButton("kills", 1, 6, CompatibleMaterial.DIAMOND_SWORD.getItem(), (event) -> toggle(ClaimPerm.MOB_KILLING));
        this.setButton("redstone", 1, 7, CompatibleMaterial.REDSTONE.getItem(), (event) -> toggle(ClaimPerm.REDSTONE));
        refreshDisplay();
    }

    private void refreshDisplay() {
        this.updateItem("break", 1, 1,
                plugin.getLocale().getMessage("interface.permsettings.breaktitle").getMessage(),
                plugin.getLocale().getMessage("general.interface.current")
                        .processPlaceholder("current", role == ClaimRole.MEMBER
                                ? claim.getMemberPermissions().getStatus(ClaimPerm.BREAK) : claim.getVisitorPermissions().getStatus(ClaimPerm.BREAK))
                        .getMessage().split("\\|"));
        this.updateItem("place", 1, 2,
                plugin.getLocale().getMessage("interface.permsettings.placetitle").getMessage(),
                plugin.getLocale().getMessage("general.interface.current")
                        .processPlaceholder("current", role == ClaimRole.MEMBER
                                ? claim.getMemberPermissions().getStatus(ClaimPerm.PLACE) : claim.getVisitorPermissions().getStatus(ClaimPerm.PLACE))
                        .getMessage().split("\\|"));
        this.updateItem("interact", 1, 3,
                plugin.getLocale().getMessage("interface.permsettings.interacttitle").getMessage(),
                plugin.getLocale().getMessage("general.interface.current")
                        .processPlaceholder("current", role == ClaimRole.MEMBER
                                ? claim.getMemberPermissions().getStatus(ClaimPerm.INTERACT) : claim.getVisitorPermissions().getStatus(ClaimPerm.INTERACT))
                        .getMessage().split("\\|"));

        this.updateItem("trading", 1, 4,
                plugin.getLocale().getMessage("interface.permsettings.tradingtitle").getMessage(),
                plugin.getLocale().getMessage("general.interface.current")
                        .processPlaceholder("current", role == ClaimRole.MEMBER
                                ? claim.getMemberPermissions().getStatus(ClaimPerm.TRADING) : claim.getVisitorPermissions().getStatus(ClaimPerm.TRADING))
                        .getMessage().split("\\|"));

        this.updateItem("doors", 1, 5,
                plugin.getLocale().getMessage("interface.permsettings.doorstitle").getMessage(),
                plugin.getLocale().getMessage("general.interface.current")
                        .processPlaceholder("current", role == ClaimRole.MEMBER
                                ? claim.getMemberPermissions().getStatus(ClaimPerm.DOORS) : claim.getVisitorPermissions().getStatus(ClaimPerm.DOORS))
                        .getMessage().split("\\|"));
        this.updateItem("kills", 1, 6,
                plugin.getLocale().getMessage("interface.permsettings.mobkilltitle").getMessage(),
                plugin.getLocale().getMessage("general.interface.current")
                        .processPlaceholder("current", role == ClaimRole.MEMBER
                                ? claim.getMemberPermissions().getStatus(ClaimPerm.MOB_KILLING) : claim.getVisitorPermissions().getStatus(ClaimPerm.MOB_KILLING))
                        .getMessage().split("\\|"));
        this.updateItem("redstone", 1, 7,
                plugin.getLocale().getMessage("interface.permsettings.redstonetitle").getMessage(),
                plugin.getLocale().getMessage("general.interface.current")
                        .processPlaceholder("current", role == ClaimRole.MEMBER
                                ? claim.getMemberPermissions().getStatus(ClaimPerm.REDSTONE) : claim.getVisitorPermissions().getStatus(ClaimPerm.REDSTONE))
                        .getMessage().split("\\|"));

    }

    private void toggle(ClaimPerm perm) {
        if (role == ClaimRole.MEMBER) {
            claim.getMemberPermissions().setAllowed(perm, !claim.getMemberPermissions().hasPermission(perm));
            plugin.getDataManager().updatePermissions(claim, claim.getMemberPermissions(), ClaimRole.MEMBER);
        } else {
            claim.getVisitorPermissions().setAllowed(perm, !claim.getVisitorPermissions().hasPermission(perm));
            plugin.getDataManager().updatePermissions(claim, claim.getVisitorPermissions(), ClaimRole.VISITOR);
        }
        refreshDisplay();
    }
}
