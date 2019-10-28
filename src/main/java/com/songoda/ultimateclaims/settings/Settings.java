package com.songoda.ultimateclaims.settings;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.configuration.Config;
import com.songoda.core.configuration.ConfigSetting;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.hooks.HologramManager;
import com.songoda.ultimateclaims.UltimateClaims;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Settings {

    static final Config config = UltimateClaims.getInstance().getCoreConfig();

    public static final ConfigSetting POWERCELL_RECIPE = new ConfigSetting(config, "Main.PowerCell Recipe",
            Arrays.asList("3:IRON_INGOT", "4:DIAMOND", "5:IRON_INGOT",
                    "12:DIAMOND", "13:IRON_INGOT", "14:DIAMOND",
                    "21:IRON_INGOT", "22:DIAMOND", "23:IRON_INGOT"),
            "The recipe players will need to place into a chest",
            "in order to create a powercell.");

    public static final ConfigSetting ITEM_VALUES = new ConfigSetting(config, "Main.PowerCell Item Values",
            Arrays.asList("DIAMOND:120", "IRON_INGOT:30"),
            "The value in minutes of each item put into the powercell.");

    public static final ConfigSetting ECONOMY = new ConfigSetting(config, "Main.Economy", 
            EconomyManager.getEconomy() == null ? "Vault" : EconomyManager.getEconomy().getName(),
            "Which economy plugin should be used?",
            "You can choose from \"" + EconomyManager.getManager().getRegisteredPlugins().stream().collect(Collectors.joining("\", \"")) + "\".");

    public static final ConfigSetting HOLOGRAM = new ConfigSetting(config, "Main.Hologram", 
            HologramManager.getHolograms() == null ? "HolographicDisplays" : HologramManager.getHolograms().getName(),
            "Which hologram plugin should be used?",
            "You can choose from \"" + HologramManager.getManager().getRegisteredPlugins().stream().collect(Collectors.joining(", ")) + "\".");

    public static final ConfigSetting ECONOMY_VALUE = new ConfigSetting(config, "Main.PowerCell Economy Value", 100,
            "How much money should constitute one minute?");

    public static final ConfigSetting MINIMUM_POWER = new ConfigSetting(config, "Main.Minimum PowerCell power", -30,
            "The minimum amount of power allowed before a claim",
            "auto dissolves.");

    public static final ConfigSetting POWERCELL_HOLOGRAMS = new ConfigSetting(config, "Main.Powercell Holograms", true,
            "Should holograms be placed above powercells?");

    public static final ConfigSetting CLAIMS_BOSSBAR = new ConfigSetting(config, "Main.Claims Use Boss Bar", false,
            "Display a boss bar to players while they're in a claim?",
            "Default behavior is to show a title on entry/exit.");

    public static final ConfigSetting CHUNKS_MUST_TOUCH = new ConfigSetting(config, "Main.Chunks Must Touch", true,
            "Should chunks have to touch to be claimed?",
            "This prevents people from claiming little pieces all over the place.");

    public static final ConfigSetting INVITE_TIMEOUT = new ConfigSetting(config, "Main.Invite Timeout", 30,
            "The amount of time before an invite times out.");

    public static final ConfigSetting STARTING_POWER = new ConfigSetting(config, "Main.Starting Power", 10,
            "The starting amount of power in minutes a claim gets.",
            "This time should be used to create a powercell.");

    public static final ConfigSetting MAX_CHUNKS = new ConfigSetting(config, "Main.Max Chunks", 10,
            "The maximum amount of chunks a claim can have.");

    public static final ConfigSetting MAX_MEMBERS = new ConfigSetting(config, "Main.Max Members", 10,
            "The maximum amount of members a claim can have.");

    public static final ConfigSetting DISABLED_WORLDS = new ConfigSetting(config, "Main.Disabled Worlds",
            Arrays.asList("disabled_world"), "The worlds that claims are disabled in.");

    public static final ConfigSetting COST_EQUATION = new ConfigSetting(config, "Main.Cost Equation",
            "DEFAULT", "The equation used for calcuation the cost of a claim.",
            "NONE - The claim size does not affect the cost.",
            "DEFAULT - Multiplies the cost by the claim size.",
            "LINEAR [value] - Multiplies the cost by the claim size multiplied by the value.");

    public static final ConfigSetting GLASS_TYPE_1 = new ConfigSetting(config, "Interfaces.Glass Type 1", "GRAY_STAINED_GLASS_PANE");
    public static final ConfigSetting GLASS_TYPE_2 = new ConfigSetting(config, "Interfaces.Glass Type 2", "BLUE_STAINED_GLASS_PANE");
    public static final ConfigSetting GLASS_TYPE_3 = new ConfigSetting(config, "Interfaces.Glass Type 3", "LIGHT_BLUE_STAINED_GLASS_PANE");

    public static final ConfigSetting LANGUGE_MODE = new ConfigSetting(config, "System.Language Mode", "en_US",
            "The enabled language file.",
            "More language files (if available) can be found in the plugins data folder.");

    public static final ConfigSetting MYSQL_ENABLED = new ConfigSetting(config, "MySQL.Enabled", false, "Set to 'true' to use MySQL instead of SQLite for data storage.");
    public static final ConfigSetting MYSQL_HOSTNAME = new ConfigSetting(config, "MySQL.Hostname", "localhost");
    public static final ConfigSetting MYSQL_PORT = new ConfigSetting(config, "MySQL.Port", 3306);
    public static final ConfigSetting MYSQL_DATABASE = new ConfigSetting(config, "MySQL.Database", "your-database");
    public static final ConfigSetting MYSQL_USERNAME = new ConfigSetting(config, "MySQL.Username", "user");
    public static final ConfigSetting MYSQL_PASSWORD = new ConfigSetting(config, "MySQL.Password", "pass");
    public static final ConfigSetting MYSQL_USE_SSL = new ConfigSetting(config, "MySQL.Use SSL", false);

    /**
     * In order to set dynamic economy comment correctly, this needs to be
     * called after EconomyManager load
     */
    public static void setupConfig() {
        config.load();
        config.setAutoremove(true).setAutosave(true);

        // convert glass pane settings
        int color;
        if ((color = GLASS_TYPE_1.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_1.getKey(), CompatibleMaterial.getGlassPaneColor(color).name());
        }
        if ((color = GLASS_TYPE_2.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_2.getKey(), CompatibleMaterial.getGlassPaneColor(color).name());
        }
        if ((color = GLASS_TYPE_3.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_3.getKey(), CompatibleMaterial.getGlassPaneColor(color).name());
        }

        // convert economy settings
        if (config.getBoolean("Economy.Use Vault Economy") && EconomyManager.getManager().isEnabled("Vault")) {
            config.set("Main.Economy", "Vault");
        } else if (config.getBoolean("Economy.Use Reserve Economy") && EconomyManager.getManager().isEnabled("Reserve")) {
            config.set("Main.Economy", "Reserve");
        } else if (config.getBoolean("Economy.Use Player Points Economy") && EconomyManager.getManager().isEnabled("PlayerPoints")) {
            config.set("Main.Economy", "PlayerPoints");
        }

        config.saveChanges();
    }
}
