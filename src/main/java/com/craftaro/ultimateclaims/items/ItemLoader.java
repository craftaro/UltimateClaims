package com.craftaro.ultimateclaims.items;

import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

/**
 * An interface for custom item loaders.
 * @author Fernando Pettinelli (l3st4t)
 */
public interface ItemLoader {

    // Gets the name of the item loader implementation.
    String getName();

    // Takes an item as specified in the config, returns a function to evaluate item stacks.
    Function<ItemStack, Boolean> loadItem(String item);

    // Returns an item based on a key.
    ItemStack getItem(String key);
}
