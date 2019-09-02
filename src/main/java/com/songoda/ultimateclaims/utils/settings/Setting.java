package com.songoda.ultimateclaims.utils.settings;


import com.songoda.ultimateclaims.UltimateClaims;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Setting {

    POWERCELL_RECIPE("Main.PowerCell Recipe",
            Arrays.asList("3:IRON_INGOT", "4:DIAMOND", "5:IRON_INGOT",
                    "12:DIAMOND", "13:IRON_INGOT", "14:DIAMOND",
                    "21:IRON_INGOT", "22:DIAMOND", "23:IRON_INGOT"),
            "The recipe players will need to place into a chest",
            "in order to create a powercell."),

    ITEM_VALUES("Main.PowerCell Item Values",
            Arrays.asList("DIAMOND:120", "IRON_INGOT:30"),
                    "The value in minutes of each item put into the powercell."),

    ECONOMY_VALUE("Main.PowerCell Economy Value", 100,
            "How much money should constitute one minute?"),

    MINIMUM_POWER("Main.Minimum PowerCell power", -30,
            "The minimum amount of power allowed before a claim",
            "auto dissolves."),

    POWERCELL_HOLOGRAMS("Main.Powercell Holograms", true,
            "Should holograms be placed above powercells?"),

    CLAIMS_BOSSBAR("Main.Claims Use Boss Bar", false,
            "Display a boss bar to players while they're in a claim?",
            "Default behavior is to show a title on entry/exit."),

    CHUNKS_MUST_TOUCH("Main.Chunks Must Touch", true,
            "Should chunks have to touch to be claimed?",
            "This prevents people from claiming little pieces all over the place."),

    INVITE_TIMEOUT("Main.Invite Timeout", 30,
            "The amount of time before an invite times out."),

    STARTING_POWER("Main.Starting Power", 10,
            "The starting amount of power in minutes a claim gets.",
            "This time should be used to create a powercell."),

    MAX_CHUNKS("Main.Max Chunks", 10,
            "The maximum amount of chunks a claim can have."),

    MAX_MEMBERS("Main.Max Members", 10,
            "The maximum amount of members a claim can have."),

    VAULT_ECONOMY("Economy.Use Vault Economy", true,
            "Should Vault be used?"),

    RESERVE_ECONOMY("Economy.Use Reserve Economy", true,
            "Should Reserve be used?"),

    PLAYER_POINTS_ECONOMY("Economy.Use Player Points Economy", false,
            "Should PlayerPoints be used?"),

    GLASS_TYPE_1("Interfaces.Glass Type 1", 7),
    GLASS_TYPE_2("Interfaces.Glass Type 2", 11),
    GLASS_TYPE_3("Interfaces.Glass Type 3", 3),

    LANGUGE_MODE("System.Language Mode", "en_US",
            "The enabled language file.",
            "More language files (if available) can be found in the plugins data folder."),

    MYSQL_ENABLED("MySQL.Enabled", false, "Set to 'true' to use MySQL instead of SQLite for data storage."),
    MYSQL_HOSTNAME("MySQL.Hostname", "localhost"),
    MYSQL_PORT("MySQL.Port", 3306),
    MYSQL_DATABASE("MySQL.Database", "your-database"),
    MYSQL_USERNAME("MySQL.Username", "user"),
    MYSQL_PASSWORD("MySQL.Password", "pass"),
    MYSQL_USE_SSL("MySQL.Use SSL", false);

    private String setting;
    private Object option;
    private String[] comments;

    Setting(String setting, Object option, String... comments) {
        this.setting = setting;
        this.option = option;
        this.comments = comments;
    }

    Setting(String setting, Object option) {
        this.setting = setting;
        this.option = option;
        this.comments = null;
    }

    public static Setting getSetting(String setting) {
        List<Setting> settings = Arrays.stream(values()).filter(setting1 -> setting1.setting.equals(setting)).collect(Collectors.toList());
        if (settings.isEmpty()) return null;
        return settings.get(0);
    }

    public String getSetting() {
        return setting;
    }

    public Object getOption() {
        return option;
    }

    public String[] getComments() {
        return comments;
    }

    public List<Integer> getIntegerList() {
        return UltimateClaims.getInstance().getConfig().getIntegerList(setting);
    }

    public List<String> getStringList() {
        return UltimateClaims.getInstance().getConfig().getStringList(setting);
    }

    public boolean getBoolean() {
        return UltimateClaims.getInstance().getConfig().getBoolean(setting);
    }

    public int getInt() {
        return UltimateClaims.getInstance().getConfig().getInt(setting);
    }

    public long getLong() {
        return UltimateClaims.getInstance().getConfig().getLong(setting);
    }

    public String getString() {
        return UltimateClaims.getInstance().getConfig().getString(setting);
    }

    public char getChar() {
        return UltimateClaims.getInstance().getConfig().getString(setting).charAt(0);
    }

    public double getDouble() {
        return UltimateClaims.getInstance().getConfig().getDouble(setting);
    }

    public Material getMaterial() {
        String materialStr = UltimateClaims.getInstance().getConfig().getString(setting);
        Material material = Material.getMaterial(materialStr);

        if (material == null) {
            System.out.println(String.format("Config value \"%s\" has an invalid material name: \"%s\"", setting, materialStr));
        }

        return material;
    }
}