package com.craftaro.ultimateclaims.items.loaders;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimateclaims.items.ItemLoader;
import com.craftaro.core.compatibility.CompatibleMaterial;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Function;

public class VanillaLoader implements ItemLoader {
    @Override
    public String getName() {
        return "vanilla";
    }

    @Override
    public Function<ItemStack, Boolean> loadItem(String item) {
        Optional<XMaterial> material = XMaterial.matchXMaterial(item);
        if (material == null) {
            return null;
        }
        return itemStack -> CompatibleMaterial.getMaterial(itemStack.getType()) == material;
    }

    @Override
    public ItemStack getItem(String key) {
        return CompatibleMaterial.getMaterial(key).get().parseItem();
    }
}