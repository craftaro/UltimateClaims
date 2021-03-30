package com.songoda.ultimateclaims.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.CustomizableGui;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.claim.ClaimSetting;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.settings.Settings;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SettingsGui extends CustomizableGui {

    private final UltimateClaims plugin;
    private final Claim claim;
    private final boolean hostilemobspawning, firespread, pvp, mobgriefing, leafdecay, tnt, fly;
    
    public SettingsGui(UltimateClaims plugin, Claim claim, Player player) {
        super(plugin, "settings");
        this.claim = claim;
        this.plugin = plugin;
        this.setRows(3);
        this.setTitle(plugin.getLocale().getMessage("interface.settings.title").getMessage());

        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        // edges will be type 3
        setDefaultItem(glass3);

        // decorate corners
        mirrorFill("mirrorfill_1", 0, 0, true, true, glass2);
        mirrorFill("mirrorfill_2", 1, 0, true, true, glass2);
        mirrorFill("mirrorfill_3", 0, 1, true, true, glass2);

        // exit buttons
        this.setButton("back", 0, GuiUtils.createButtonItem(CompatibleMaterial.OAK_FENCE_GATE,
                plugin.getLocale().getMessage("general.interface.back").getMessage(),
                plugin.getLocale().getMessage("general.interface.exit").getMessage()),
                (event) -> event.player.closeInventory());
        this.setButton("back",8, this.getItem(0),
                (event) -> guiManager.showGUI(event.player, claim.getPowerCell().getGui(event.player)));

        // shortcuts for member settings
        this.setButton("visitors", rows - 1, 3, GuiUtils.createButtonItem(CompatibleMaterial.OAK_SIGN,
                plugin.getLocale().getMessage("interface.members.visitorsettingstitle").getMessage(),
                plugin.getLocale().getMessage("interface.members.visitorsettingslore").getMessage().split("\\|")),
                (event) -> event.manager.showGUI(event.player, new SettingsMemberGui(plugin, claim, this, ClaimRole.VISITOR)));

        this.setButton("visitors", rows - 1, 5, GuiUtils.createButtonItem(CompatibleMaterial.PAINTING,
                plugin.getLocale().getMessage("interface.members.membersettingstitle").getMessage(),
                plugin.getLocale().getMessage("interface.members.membersettingslore").getMessage().split("\\|")),
                (event) -> event.manager.showGUI(event.player, new SettingsMemberGui(plugin, claim, this, ClaimRole.MEMBER)));

        this.setItem(1, 4, AIR);
        if (hostilemobspawning = player.hasPermission("ultimateclaims.toggle.hostilemobspawning")) {
            this.setButton("hostilemobspawning", 1, 1, CompatibleMaterial.ZOMBIE_SPAWN_EGG.getItem(), (event) -> toggle(ClaimSetting.HOSTILE_MOB_SPAWNING));
        }
        if (firespread = player.hasPermission("ultimateclaims.toggle.firespread")) {
            this.setButton("flintandsteal", 1, 2, CompatibleMaterial.FLINT_AND_STEEL.getItem(), (event) -> toggle(ClaimSetting.FIRE_SPREAD));
        }
        if (pvp = player.hasPermission("ultimateclaims.toggle.pvp")) {
            this.setButton("pvp", 1, 3, CompatibleMaterial.DIAMOND_SWORD.getItem(), (event) -> toggle(ClaimSetting.PVP));
        }
        if (mobgriefing = player.hasPermission("ultimateclaims.toggle.mobgriefing")) {
            this.setButton("mobgriefing", 1, 4, CompatibleMaterial.GUNPOWDER.getItem(), (event) -> toggle(ClaimSetting.MOB_GRIEFING));
        }
        if (leafdecay = player.hasPermission("ultimateclaims.toggle.leafdecay")) {
            this.setButton("leafdecay", 1, 5, CompatibleMaterial.OAK_LEAVES.getItem(), (event) -> toggle(ClaimSetting.LEAF_DECAY));
        }
        if (tnt = player.hasPermission("ultimateclaims.toggle.tnt")) {
            this.setButton("tnt", 1, 6, CompatibleMaterial.TNT.getItem(), (event) -> toggle(ClaimSetting.TNT));
        }
        if (fly = player.hasPermission("ultimateclaims.toggle.fly")) {
            this.setButton("tnt", 1, 7, CompatibleMaterial.ELYTRA.getItem(), (event) -> toggle(ClaimSetting.FLY));
        }

        refreshDisplay();
    }

    private void refreshDisplay() {
        if (hostilemobspawning) {
            this.updateItem("hostilemobspawning", 1, 1,
                    plugin.getLocale().getMessage("interface.settings.hostilemobspawningtitle").getMessage(),
                    plugin.getLocale().getMessage("general.interface.current")
                            .processPlaceholder("current", claim.getClaimSettings().isEnabled(ClaimSetting.HOSTILE_MOB_SPAWNING))
                            .getMessage().split("\\|"));
        }
        if (firespread) {
            this.updateItem("flintandsteal", 1, 2,
                    plugin.getLocale().getMessage("interface.settings.firespreadtitle").getMessage(),
                    plugin.getLocale().getMessage("general.interface.current")
                            .processPlaceholder("current", claim.getClaimSettings().isEnabled(ClaimSetting.FIRE_SPREAD))
                            .getMessage().split("\\|"));
        }
        if (pvp) {
            this.updateItem("pvp", 1, 3,
                    plugin.getLocale().getMessage("interface.settings.pvptitle").getMessage(),
                    plugin.getLocale().getMessage("general.interface.current")
                            .processPlaceholder("current", claim.getClaimSettings().isEnabled(ClaimSetting.PVP))
                            .getMessage().split("\\|"));
        }
        if (mobgriefing) {
            this.updateItem("mobgriefing", 1, 4,
                    plugin.getLocale().getMessage("interface.settings.mobgriefingtitle").getMessage(),
                    plugin.getLocale().getMessage("general.interface.current")
                            .processPlaceholder("current", claim.getClaimSettings().isEnabled(ClaimSetting.MOB_GRIEFING))
                            .getMessage().split("\\|"));
        }
        if (leafdecay) {
            this.updateItem("leafdecay", 1, 5,
                    plugin.getLocale().getMessage("interface.settings.leafdecaytitle").getMessage(),
                    plugin.getLocale().getMessage("general.interface.current")
                            .processPlaceholder("current", claim.getClaimSettings().isEnabled(ClaimSetting.LEAF_DECAY))
                            .getMessage().split("\\|"));
        }
        if (tnt) {
            this.updateItem("tnt", 1, 6,
                    plugin.getLocale().getMessage("interface.settings.tnttitle").getMessage(),
                    plugin.getLocale().getMessage("general.interface.current")
                            .processPlaceholder("current", claim.getClaimSettings().isEnabled(ClaimSetting.TNT))
                            .getMessage().split("\\|"));
        }
        if (fly) {
            this.updateItem("tnt", 1, 7,
                    plugin.getLocale().getMessage("interface.settings.flytitle").getMessage(),
                    plugin.getLocale().getMessage("general.interface.current")
                            .processPlaceholder("current", claim.getClaimSettings().isEnabled(ClaimSetting.FLY))
                            .getMessage().split("\\|"));
        }
    }

    private void toggle(ClaimSetting setting) {
        claim.getClaimSettings().setEnabled(setting, !claim.getClaimSettings().isEnabled(setting));
        plugin.getDataManager().updateSettings(claim, claim.getClaimSettings());
        refreshDisplay();
    }
}
