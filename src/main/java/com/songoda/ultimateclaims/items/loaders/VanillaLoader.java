package com.songoda.ultimateclaims.items.loaders;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.ultimateclaims.items.ItemLoader;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class VanillaLoader implements ItemLoader {
    @Override
    public String getName() {
        return "vanilla";
    }

    @Override
    public Function<ItemStack, Boolean> loadItem(String item) {
        CompatibleMaterial compatibleMaterial = CompatibleMaterial.getMaterial(item);
        if (compatibleMaterial == null) {
            return null;
        }

        return itemStack -> CompatibleMaterial.getMaterial(itemStack) == compatibleMaterial;
    }
}