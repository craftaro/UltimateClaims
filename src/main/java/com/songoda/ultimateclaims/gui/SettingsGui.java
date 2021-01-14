package com.songoda.ultimateclaims.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.CustomizableGui;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.claim.Claim;
import com.songoda.ultimateclaims.member.ClaimRole;
import com.songoda.ultimateclaims.settings.Settings;
import com.songoda.ultimateclaims.utils.Methods;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SettingsGui extends CustomizableGui {

    private final UltimateClaims plugin;
    private final Claim claim;
    private final boolean hostilemobspawning, firespread, pvp, mobgriefing, leafdecay;
    
    public SettingsGui(UltimateClaims plugin, Claim claim, Gui returnGui, Player player) {
        super(plugin, "settings");
        this.claim = claim;
        this.plugin = plugin;
        this.setRows(3);
        this.setTitle(Methods.formatTitle(plugin.getLocale().getMessage("interface.settings.title").getMessage()));

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
        this.setButton("back",8, this.getItem(0), (event) -> guiManager.showGUI(event.player, returnGui));

        // shortcuts for member settings
        this.setButton("visitors", rows - 1, 3, GuiUtils.createButtonItem(CompatibleMaterial.OAK_SIGN,
                plugin.getLocale().getMessage("interface.members.visitorsettingstitle").getMessage(),
                plugin.getLocale().getMessage("interface.members.visitorsettingslore").getMessage().split("\\|")),
                (event) -> event.manager.showGUI(event.player, new SettingsMemberGui(plugin, claim, this, ClaimRole.VISITOR)));

        this.setButton("visitors", rows - 1, 5, GuiUtils.createButtonItem(CompatibleMaterial.PAINTING,
                plugin.getLocale().getMessage("interface.members.membersettingstitle").getMessage(),
                plugin.getLocale().getMessage("interface.members.membersettingslore").getMessage().split("\\|")),
                (event) -> event.manager.showGUI(event.player, new SettingsMemberGui(plugin, claim, this, ClaimRole.MEMBER)));

        this.setItem(1, 1, AIR);
        if (hostilemobspawning = player.hasPermission("ultimateclaims.toggle.hostilemobspawning")) {
            this.setButton("hostilemobspawning", 1, 2, CompatibleMaterial.ZOMBIE_SPAWN_EGG.getItem(), (event) -> toggleSpawn());
        }
        if (firespread = player.hasPermission("ultimateclaims.toggle.firespread")) {
            this.setButton("flintandsteal", 1, 3, CompatibleMaterial.FLINT_AND_STEEL.getItem(), (event) -> toggleFire());
        }
        if (pvp = player.hasPermission("ultimateclaims.toggle.pvp")) {
            this.setButton("pvp", 1, 4, CompatibleMaterial.DIAMOND_SWORD.getItem(), (event) -> togglePVP());
        }
        if (mobgriefing = player.hasPermission("ultimateclaims.toggle.mobgriefing")) {
            this.setButton("mobgriefing", 1, 5, CompatibleMaterial.GUNPOWDER.getItem(), (event) -> toggleMobGrief());
        }
        if (leafdecay = player.hasPermission("ultimateclaims.toggle.leafdecay")) {
            this.setButton("leafdecay", 1, 6, CompatibleMaterial.OAK_LEAVES.getItem(), (event) -> toggleLeafDecay());
        }
        this.setItem(1, 7, AIR);
        refreshDisplay();
    }

    private void refreshDisplay() {
        if (hostilemobspawning) {
            this.updateItem("hostilemobspawning", 1, 2,
                    plugin.getLocale().getMessage("interface.settings.hostilemobspawningtitle").getMessage(),
                    plugin.getLocale().getMessage("general.interface.current")
                            .processPlaceholder("current", claim.getClaimSettings().isHostileMobSpawning())
                            .getMessage().split("\\|"));
        }
        if (firespread) {
            this.updateItem("flintandsteal", 1, 3,
                    plugin.getLocale().getMessage("interface.settings.firespreadtitle").getMessage(),
                    plugin.getLocale().getMessage("general.interface.current")
                            .processPlaceholder("current", claim.getClaimSettings().isFireSpread())
                            .getMessage().split("\\|"));
        }
        if (pvp) {
            this.updateItem("pvp", 1, 4,
                    plugin.getLocale().getMessage("interface.settings.pvptitle").getMessage(),
                    plugin.getLocale().getMessage("general.interface.current")
                            .processPlaceholder("current", claim.getClaimSettings().isPvp())
                            .getMessage().split("\\|"));
        }
        if (mobgriefing) {
            this.updateItem("mobgriefing", 1, 5,
                    plugin.getLocale().getMessage("interface.settings.mobgriefingtitle").getMessage(),
                    plugin.getLocale().getMessage("general.interface.current")
                            .processPlaceholder("current", claim.getClaimSettings().isMobGriefingAllowed())
                            .getMessage().split("\\|"));
        }
        if (leafdecay) {
            this.updateItem("leafdecay", 1, 6,
                    plugin.getLocale().getMessage("interface.settings.leafdecaytitle").getMessage(),
                    plugin.getLocale().getMessage("general.interface.current")
                            .processPlaceholder("current", claim.getClaimSettings().isLeafDecay())
                            .getMessage().split("\\|"));
        }
    }

    private void toggleSpawn() {
        claim.getClaimSettings().setHostileMobSpawning(!claim.getClaimSettings().isHostileMobSpawning());
        plugin.getDataManager().updateSettings(claim, claim.getClaimSettings());
        refreshDisplay();
    }

    private void toggleFire() {
        claim.getClaimSettings().setFireSpread(!claim.getClaimSettings().isFireSpread());
        plugin.getDataManager().updateSettings(claim, claim.getClaimSettings());
        refreshDisplay();
    }

    private void togglePVP() {
        claim.getClaimSettings().setPvp(!claim.getClaimSettings().isPvp());
        plugin.getDataManager().updateSettings(claim, claim.getClaimSettings());
        refreshDisplay();
    }

    private void toggleMobGrief() {
        claim.getClaimSettings().setMobGriefingAllowed(!claim.getClaimSettings().isMobGriefingAllowed());
        plugin.getDataManager().updateSettings(claim, claim.getClaimSettings());
        refreshDisplay();
    }

    private void toggleLeafDecay() {
        claim.getClaimSettings().setLeafDecay(!claim.getClaimSettings().isLeafDecay());
        plugin.getDataManager().updateSettings(claim, claim.getClaimSettings());
        refreshDisplay();
    }
}
