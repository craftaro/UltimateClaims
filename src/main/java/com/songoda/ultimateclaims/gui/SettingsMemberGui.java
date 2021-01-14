package com.songoda.ultimateclaims.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.CustomizableGui;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.member.ClaimPerm;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.settings.Settings;
import com.songoda.ultimateclaims.utils.Methods;
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
        this.setTitle(Methods.formatTitle(plugin.getLocale().getMessage("interface.permsettings.title")
                .processPlaceholder("role", Methods.formatText(role.toString().toLowerCase(), true)).getMessage()));

        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        // edges will be type 3
        setDefaultItem(glass3);

        mirrorFill("mirrorfill_1", 0, 0, true, true, glass2);
        mirrorFill("mirrorfill_2", 1, 0, true, true, glass2);
        mirrorFill("mirrorfill_3", 0, 1, true, true, glass2);

        // exit buttons
        this.setButton("back", 0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE,
                plugin.getLocale().getMessage("general.interface.back").getMessage(),
                plugin.getLocale().getMessage("general.interface.exit").getMessage()),
                (event) -> event.player.closeInventory());
        this.setButton("back",8, this.getItem(0), (event) -> guiManager.showGUI(event.player, returnGui));

        // settings
        this.setButton("break", 1, 1, CompatibleMaterial.IRON_PICKAXE.getItem(), (event) -> toggleBreak());
        this.setButton("place", 1, 2, CompatibleMaterial.STONE.getItem(), (event) -> togglePlace());
        this.setButton("interact", 1, 3, CompatibleMaterial.LEVER.getItem(), (event) -> toggleInteract());
        this.setButton("trading", 1, 3, CompatibleMaterial.GOLD_NUGGET.getItem(), (event) -> toggleTrading());
        this.setButton("doors", 1, 5, CompatibleMaterial.OAK_DOOR.getItem(), (event) -> toggleDoors());
        this.setButton("kills", 1, 6, CompatibleMaterial.DIAMOND_SWORD.getItem(), (event) -> toggleKills());
        this.setButton("redstone", 1, 7, CompatibleMaterial.REDSTONE.getItem(), (event) -> toggleRedstone());
        this.setButton("trading", 1, 4, CompatibleMaterial.EMERALD.getItem(), (event) -> toggleTrading());

        refreshDisplay();
    }

    private void refreshDisplay() {
        this.updateItem("break", 1, 1,
                plugin.getLocale().getMessage("interface.permsettings.breaktitle").getMessage(),
                plugin.getLocale().getMessage("general.interface.current")
                        .processPlaceholder("current", role == ClaimRole.MEMBER
                                ? claim.getMemberPermissions().hasPermission(ClaimPerm.BREAK) : claim.getVisitorPermissions().hasPermission(ClaimPerm.BREAK))
                        .getMessage().split("\\|"));
        this.updateItem("place", 1, 2,
                plugin.getLocale().getMessage("interface.permsettings.placetitle").getMessage(),
                plugin.getLocale().getMessage("general.interface.current")
                        .processPlaceholder("current", role == ClaimRole.MEMBER
                                ? claim.getMemberPermissions().hasPermission(ClaimPerm.PLACE) : claim.getVisitorPermissions().hasPermission(ClaimPerm.PLACE))
                        .getMessage().split("\\|"));
        this.updateItem("interact", 1, 3,
                plugin.getLocale().getMessage("interface.permsettings.interacttitle").getMessage(),
                plugin.getLocale().getMessage("general.interface.current")
                        .processPlaceholder("current", role == ClaimRole.MEMBER
                                ? claim.getMemberPermissions().hasPermission(ClaimPerm.INTERACT) : claim.getVisitorPermissions().hasPermission(ClaimPerm.INTERACT))
                        .getMessage().split("\\|"));

        this.updateItem("trading", 1, 4,
                plugin.getLocale().getMessage("interface.permsettings.tradingtitle").getMessage(),
                plugin.getLocale().getMessage("general.interface.current")
                        .processPlaceholder("current", role == ClaimRole.MEMBER
                                ? claim.getMemberPermissions().hasPermission(ClaimPerm.TRADING) : claim.getVisitorPermissions().hasPermission(ClaimPerm.TRADING))
                        .getMessage().split("\\|"));

        this.updateItem("doors", 1, 5,
                plugin.getLocale().getMessage("interface.permsettings.doorstitle").getMessage(),
                plugin.getLocale().getMessage("general.interface.current")
                        .processPlaceholder("current", role == ClaimRole.MEMBER
                                ? claim.getMemberPermissions().hasPermission(ClaimPerm.DOORS) : claim.getVisitorPermissions().hasPermission(ClaimPerm.DOORS))
                        .getMessage().split("\\|"));
        this.updateItem("kills", 1, 6,
                plugin.getLocale().getMessage("interface.permsettings.mobkilltitle").getMessage(),
                plugin.getLocale().getMessage("general.interface.current")
                        .processPlaceholder("current", role == ClaimRole.MEMBER
                                ? claim.getMemberPermissions().hasPermission(ClaimPerm.MOB_KILLING) : claim.getVisitorPermissions().hasPermission(ClaimPerm.MOB_KILLING))
                        .getMessage().split("\\|"));
        this.updateItem("redstone", 1, 7,
                plugin.getLocale().getMessage("interface.permsettings.redstonetitle").getMessage(),
                plugin.getLocale().getMessage("general.interface.current")
                        .processPlaceholder("current", role == ClaimRole.MEMBER
                                ? claim.getMemberPermissions().hasPermission(ClaimPerm.REDSTONE) : claim.getVisitorPermissions().hasPermission(ClaimPerm.REDSTONE))
                        .getMessage().split("\\|"));

    }

    private void toggleBreak() {
        if (role == ClaimRole.MEMBER) {
            claim.getMemberPermissions().setCanBreak(!claim.getMemberPermissions().hasPermission(ClaimPerm.BREAK));
            plugin.getDataManager().updatePermissions(claim, claim.getMemberPermissions(), ClaimRole.MEMBER);
        } else {
            claim.getVisitorPermissions().setCanBreak(!claim.getVisitorPermissions().hasPermission(ClaimPerm.BREAK));
            plugin.getDataManager().updatePermissions(claim, claim.getVisitorPermissions(), ClaimRole.VISITOR);
        }
        refreshDisplay();
    }

    private void togglePlace() {
        if (role == ClaimRole.MEMBER) {
            claim.getMemberPermissions().setCanPlace(!claim.getMemberPermissions().hasPermission(ClaimPerm.PLACE));
            plugin.getDataManager().updatePermissions(claim, claim.getMemberPermissions(), ClaimRole.MEMBER);
        } else {
            claim.getVisitorPermissions().setCanPlace(!claim.getVisitorPermissions().hasPermission(ClaimPerm.PLACE));
            plugin.getDataManager().updatePermissions(claim, claim.getVisitorPermissions(), ClaimRole.VISITOR);
        }
        refreshDisplay();
    }

    private void toggleInteract() {
        if (role == ClaimRole.MEMBER) {
            claim.getMemberPermissions().setCanInteract(!claim.getMemberPermissions().hasPermission(ClaimPerm.INTERACT));
            plugin.getDataManager().updatePermissions(claim, claim.getMemberPermissions(), ClaimRole.MEMBER);
        } else {
            claim.getVisitorPermissions().setCanInteract(!claim.getVisitorPermissions().hasPermission(ClaimPerm.INTERACT));
            plugin.getDataManager().updatePermissions(claim, claim.getVisitorPermissions(), ClaimRole.VISITOR);
        }
        refreshDisplay();
    }

    private void toggleDoors() {
        if (role == ClaimRole.MEMBER) {
            claim.getMemberPermissions().setCanDoors(!claim.getMemberPermissions().hasPermission(ClaimPerm.DOORS));
            plugin.getDataManager().updatePermissions(claim, claim.getMemberPermissions(), ClaimRole.MEMBER);
        } else {
            claim.getVisitorPermissions().setCanDoors(!claim.getVisitorPermissions().hasPermission(ClaimPerm.DOORS));
            plugin.getDataManager().updatePermissions(claim, claim.getVisitorPermissions(), ClaimRole.VISITOR);
        }
        refreshDisplay();
    }

    private void toggleKills() {
        if (role == ClaimRole.MEMBER) {
            claim.getMemberPermissions().setCanMobKill(!claim.getMemberPermissions().hasPermission(ClaimPerm.MOB_KILLING));
            plugin.getDataManager().updatePermissions(claim, claim.getMemberPermissions(), ClaimRole.MEMBER);
        } else {
            claim.getVisitorPermissions().setCanMobKill(!claim.getVisitorPermissions().hasPermission(ClaimPerm.MOB_KILLING));
            plugin.getDataManager().updatePermissions(claim, claim.getVisitorPermissions(), ClaimRole.VISITOR);
        }
        refreshDisplay();
    }

    private void toggleRedstone() {
        if (role == ClaimRole.MEMBER) {
            claim.getMemberPermissions().setCanRedstone(!claim.getMemberPermissions().hasPermission(ClaimPerm.REDSTONE));
            plugin.getDataManager().updatePermissions(claim, claim.getMemberPermissions(), ClaimRole.MEMBER);
        } else {
            claim.getVisitorPermissions().setCanRedstone(!claim.getVisitorPermissions().hasPermission(ClaimPerm.REDSTONE));
            plugin.getDataManager().updatePermissions(claim, claim.getVisitorPermissions(), ClaimRole.VISITOR);
        }
        refreshDisplay();
    }

    private void toggleTrading() {
        if(role == ClaimRole.MEMBER) {
            claim.getMemberPermissions().setCanTrade(!claim.getMemberPermissions().hasPermission(ClaimPerm.TRADING));
            plugin.getDataManager().updatePermissions(claim, claim.getMemberPermissions(), ClaimRole.MEMBER);
        } else {
            claim.getVisitorPermissions().setCanTrade(!claim.getVisitorPermissions().hasPermission(ClaimPerm.TRADING));
            plugin.getDataManager().updatePermissions(claim, claim.getVisitorPermissions(), ClaimRole.VISITOR);
        }
        refreshDisplay();
    }
}
