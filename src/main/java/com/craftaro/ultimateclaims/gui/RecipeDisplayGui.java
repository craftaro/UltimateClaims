package com.craftaro.ultimateclaims.gui;

import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.ultimateclaims.UltimateClaims;
import com.craftaro.ultimateclaims.items.PowerCellItem;
import com.craftaro.ultimateclaims.settings.Settings;

import java.util.Map;

public class RecipeDisplayGui extends Gui {
    public RecipeDisplayGui() {
        this.setRows(3);
        this.setTitle(UltimateClaims.getInstance().getLocale().getMessage("interface.recipe.title").getMessage());
        this.setDefaultItem(GuiUtils.getBorderItem(Settings.GLASS_TYPE_1.getMaterial()));

        Map<Integer, PowerCellItem> recipe = UltimateClaims.getInstance().getItemManager().getRecipe();
        for (Map.Entry<Integer, PowerCellItem> item : recipe.entrySet()) {
            setItem(item.getKey(), item.getValue().getDisplayItem());
        }
    }
}
