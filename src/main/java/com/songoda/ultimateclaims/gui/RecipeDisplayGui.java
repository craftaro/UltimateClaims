package com.songoda.ultimateclaims.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.ItemUtils;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.settings.Settings;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class RecipeDisplayGui extends Gui {

    public RecipeDisplayGui() {
        this.setRows(3);
        this.setTitle(UltimateClaims.getInstance().getLocale().getMessage("interface.recipe.title").getMessage());
        this.setDefaultItem(GuiUtils.getBorderItem(Settings.GLASS_TYPE_1.getMaterial()));

        List<String> recipe = Settings.POWERCELL_RECIPE.getStringList();
        for (String line : recipe) {
            String[] split = line.split(":");
            CompatibleMaterial mat;
            if (split.length == 2 && split[0].matches("^[0-9]{1,2}$") && (mat = CompatibleMaterial.getMaterial(split[1])) != null) {
                ItemStack item = mat.getItem();
                setItem(Integer.parseInt(split[0]), GuiUtils.updateItem(item, ItemUtils.getItemName(item)));
            }
        }
    }
}
