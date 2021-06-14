package com.songoda.ultimateclaims.items;

import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class PowerCellItem {

    private final Function<ItemStack, Boolean> similar;
    private final int value;

    public PowerCellItem(Function<ItemStack, Boolean> similar, int value) {
        this.similar = similar;
        this.value = value;
    }

    public boolean isSimilar(ItemStack itemStack) {
        return similar.apply(itemStack);
    }

    public int getValue() {
        return value;
    }
}