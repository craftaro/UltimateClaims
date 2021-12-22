package com.songoda.ultimateclaims.items;

import com.songoda.core.configuration.Config;
import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.items.loaders.*;
import com.songoda.ultimateclaims.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemManager {

    private final UltimateClaims plugin;
    private final List<ItemLoader> itemLoaders;
    private final List<PowerCellItem> items;
    private final Map<Integer, PowerCellItem> recipe;
    private final Config itemConfig;

    public ItemManager(UltimateClaims plugin) {
        this.plugin = plugin;
        this.itemLoaders = new ArrayList<>();
        this.items = new ArrayList<>();
        this.recipe = new HashMap<>();
        this.itemConfig = new Config(plugin, "items.yml");

        loadLoaders();
        loadItems();
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

    public void loadItems() {
        itemConfig.load();
        items.clear();

        if (!itemConfig.isConfigurationSection("items")) {
            itemConfig.setHeader("This is where you configure the power cell items.",
                    "Supported item types: vanilla, slimefun, itembridge, itemsadder",
                    "Note: Vanilla items should be placed at the bottom to prevent conflicts.");

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
        }

        if (!itemConfig.isConfigurationSection("recipe")) {
            List<String> oldRecipe = Settings.POWERCELL_RECIPE.getStringList();
            if (!oldRecipe.isEmpty()) {
                convertOldRecipe(oldRecipe);
            }else{
                // We use this because putting in the items manually would be a lot of lines.
                convertOldRecipe(Arrays.asList("3:IRON_INGOT", "4:DIAMOND", "5:IRON_INGOT",
                        "12:DIAMOND", "13:IRON_INGOT", "14:DIAMOND",
                        "21:IRON_INGOT", "22:DIAMOND", "23:IRON_INGOT"));
            }
        }


        itemConfig.options().copyDefaults(true);
        itemConfig.save();

        for (String key : itemConfig.getConfigurationSection("items").getKeys(false)) {
            String prefix = "items." + key;

            String type = itemConfig.getString(prefix + ".type");
            String item = itemConfig.getString(prefix + ".item");
            int value = itemConfig.getInt(prefix + ".value");

            itemLoaders.stream().filter(loader -> loader.getName().equalsIgnoreCase(type)).findAny().ifPresent(loader ->
                    items.add(new PowerCellItem(loader.getItem(item), loader.loadItem(item), value)));
        }

        for (String key : itemConfig.getConfigurationSection("recipe").getKeys(false)) {
            String prefix = "recipe." + key;

            String type = itemConfig.getString(prefix + ".type");
            String item = itemConfig.getString(prefix + ".item");
            int slot = itemConfig.getInt(prefix + ".slot");

            itemLoaders.stream().filter(loader -> loader.getName().equalsIgnoreCase(type)).findAny().ifPresent(loader -> {
                recipe.put(slot, new PowerCellItem(loader.getItem(item), loader.loadItem(item), 0));
            });
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

    private void convertOldRecipe(List<String> recipe) {
        int currentItem = 0;
        for (String oldItem : recipe) {
            String[] split = oldItem.split(":");
            if (split.length == 2) {
                itemConfig.set("recipe." + currentItem + ".type", "vanilla");
                itemConfig.set("recipe." + currentItem + ".item", split[1]);
                itemConfig.set("recipe." + currentItem + ".slot", Integer.parseInt(split[0]));
                currentItem++;
            }
        }
    }

    public List<PowerCellItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Map<Integer, PowerCellItem> getRecipe() {
        return Collections.unmodifiableMap(recipe);
    }

    public int getItemValue(ItemStack itemStack) {
        Optional<PowerCellItem> optional = items.stream().filter(powerCellItem -> powerCellItem.isSimilar(itemStack)).findAny();
        if (optional.isPresent()) {
            return optional.get().getValue();
        }

        return 0;
    }
}