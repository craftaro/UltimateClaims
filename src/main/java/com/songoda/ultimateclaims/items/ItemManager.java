package com.songoda.ultimateclaims.items;

import com.songoda.core.configuration.Config;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.items.loaders.ItemBridgeLoader;
import com.songoda.ultimateclaims.items.loaders.ItemsAdderLoader;
import com.songoda.ultimateclaims.items.loaders.SlimefunLoader;
import com.songoda.ultimateclaims.items.loaders.VanillaLoader;
import com.songoda.ultimateclaims.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemManager {

    private final UltimateClaims plugin;
    private final List<ItemLoader> itemLoaders;
    private final List<PowerCellItem> items;
    private final Config itemConfig;

    public ItemManager(UltimateClaims plugin) {
        this.plugin = plugin;
        this.itemLoaders = new ArrayList<>();
        this.items = new ArrayList<>();
        this.itemConfig = new Config(plugin, "items.yml");

        loadLoaders();
        loadConfig();
    }

    private void loadLoaders() {
        if (Bukkit.getPluginManager().getPlugin("Slimefun") != null)
            itemLoaders.add(new SlimefunLoader());

        if (Bukkit.getPluginManager().getPlugin("ItemBridge") != null)
            itemLoaders.add(new ItemBridgeLoader());

        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null)
            itemLoaders.add(new ItemsAdderLoader());

        itemLoaders.add(new VanillaLoader());
    }

    private void loadConfig() {
        itemConfig.load();

        if (!itemConfig.isConfigurationSection("items")) {
            itemConfig.setHeader("This is where you configure the power cell items.",
                    "Supported item types: vanilla, slimefun, itembridge, itemsadder");

            List<String> oldItems = Settings.ITEM_VALUES.getStringList();
            if (!oldItems.isEmpty()) {
                convertOldItems(oldItems);
            }else{
                itemConfig.addDefault("items.0.type", "vanilla");
                itemConfig.addDefault("items.0.item", "DIAMOND");
                itemConfig.addDefault("items.0.value", 120);

                itemConfig.addDefault("items.1.type", "vanilla");
                itemConfig.addDefault("items.1.item", "IRON_INGOT");
                itemConfig.addDefault("items.1.value", 30);
            }

            itemConfig.options().copyDefaults(true);
            itemConfig.save();
        }

        for (String key : itemConfig.getConfigurationSection("items").getKeys(false)) {
            String prefix = "items." + key;

            String type = itemConfig.getString(prefix + ".type");
            String item = itemConfig.getString(prefix + ".item");
            int value = itemConfig.getInt(prefix + ".value");

            itemLoaders.stream().filter(loader -> loader.getName().equalsIgnoreCase(type)).findAny().ifPresent(loader ->
                    items.add(new PowerCellItem(loader.loadItem(item), value)));
        }
    }

    private void convertOldItems(List<String> oldItems) {
        int currentItem = 0;
        for (String oldItem : oldItems) {
            String[] split = oldItem.split(":");
            if (split.length == 2) {
                itemConfig.set("items." + currentItem + ".type", "vanilla");
                itemConfig.set("items." + currentItem + ".item", split[0]);
                itemConfig.set("items." + currentItem + ".value", Integer.parseInt(split[1]));
                currentItem++;
            }
        }
    }

    public List<PowerCellItem> getItems() {
        return items;
    }

    public int getItemValue(ItemStack itemStack) {
        Optional<PowerCellItem> optional = items.stream().filter(powerCellItem -> powerCellItem.isSimilar(itemStack)).findAny();
        if (optional.isPresent()) {
            return optional.get().getValue();
        }

        return 0;
    }
}