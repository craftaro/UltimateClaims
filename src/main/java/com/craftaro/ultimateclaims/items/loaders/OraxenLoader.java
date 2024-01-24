package com.craftaro.ultimateclaims.items.loaders;

import com.craftaro.ultimateclaims.items.ItemLoader;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class OraxenLoader implements ItemLoader {
    @Override
    public String getName() {
        return "oraxen";
    }

    @Override
    public Function<ItemStack, Boolean> loadItem(String item) {
        return itemStack -> {
            ItemStack oraxenItem = OraxenItems.getItemById(item).build();
            if (oraxenItem == null) {
                return false;
            }

            return oraxenItem.isSimilar(itemStack);
        };
    }

    @Override
    public ItemStack getItem(String key) {
        return OraxenItems.getItemById(key).build();
    }
}
