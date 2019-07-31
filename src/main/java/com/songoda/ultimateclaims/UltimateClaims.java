package com.songoda.ultimateclaims;

import com.songoda.ultimateclaims.claim.ClaimManager;
import com.songoda.ultimateclaims.command.CommandManager;
import com.songoda.ultimateclaims.database.DataManager;
import com.songoda.ultimateclaims.database.DataMigrationManager;
import com.songoda.ultimateclaims.database.DatabaseConnector;
import com.songoda.ultimateclaims.database.MySQLConnector;
import com.songoda.ultimateclaims.database.SQLiteConnector;
import com.songoda.ultimateclaims.economy.Economy;
import com.songoda.ultimateclaims.economy.PlayerPointsEconomy;
import com.songoda.ultimateclaims.economy.ReserveEconomy;
import com.songoda.ultimateclaims.economy.VaultEconomy;
import com.songoda.ultimateclaims.hologram.Hologram;
import com.songoda.ultimateclaims.hologram.HologramHolographicDisplays;
import com.songoda.ultimateclaims.listeners.*;
import com.songoda.ultimateclaims.settings.PluginSettings;
import com.songoda.ultimateclaims.tasks.AnimateTask;
import com.songoda.ultimateclaims.tasks.InviteTask;
import com.songoda.ultimateclaims.tasks.PowerCellTask;
import com.songoda.ultimateclaims.tasks.TrackerTask;
import com.songoda.ultimateclaims.utils.Metrics;
import com.songoda.ultimateclaims.utils.ServerVersion;
import com.songoda.ultimateclaims.utils.locale.Locale;
import com.songoda.ultimateclaims.utils.settings.Setting;
import com.songoda.ultimateclaims.utils.settings.SettingsManager;
import com.songoda.ultimateclaims.utils.updateModules.LocaleModule;
import com.songoda.update.Plugin;
import com.songoda.update.SongodaUpdate;
import com.songoda.update.utils.Methods;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class UltimateClaims extends JavaPlugin {

    private static UltimateClaims INSTANCE;

    private ConsoleCommandSender console = Bukkit.getConsoleSender();

    private ServerVersion serverVersion = ServerVersion.fromPackageName(Bukkit.getServer().getClass().getPackage().getName());

    private Locale locale;
    private Economy economy;
    private Hologram hologram;
    private PluginSettings pluginSettings;
    private DatabaseConnector databaseConnector;

    private SettingsManager settingsManager;
    private CommandManager commandManager;
    private ClaimManager claimManager;

    private DataMigrationManager dataMigrationManager;
    private DataManager dataManager;

    private InviteTask inviteTask;

    public static UltimateClaims getInstance() {
        return INSTANCE;
    }

    @Override
    public void onDisable() {
        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7UltimateClaims " + this.getDescription().getVersion() + " by &5Songoda <3!"));
        console.sendMessage(Methods.formatText("&7Action: &cDisabling&7..."));

        this.dataManager.bulkUpdateClaims(this.claimManager.getRegisteredClaims());

        this.databaseConnector.closeConnection();

        console.sendMessage(Methods.formatText("&a============================="));
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7UltimateClaims " + this.getDescription().getVersion() + " by &5Songoda <3&7!"));
        console.sendMessage(Methods.formatText("&7Action: &aEnabling&7..."));

        // Setup Setting Manager
        this.settingsManager = new SettingsManager(this);
        this.settingsManager.setupConfig();

        // Setup Language
        new Locale(this, "en_US");
        this.locale = Locale.getLocale(getConfig().getString("System.Language Mode"));

        // Running Songoda Updater
        Plugin plugin = new Plugin(this, 65);
        plugin.addModule(new LocaleModule());
        SongodaUpdate.load(plugin);

        PluginManager pluginManager = Bukkit.getPluginManager();

        // Register Hologram Plugin
        if (Setting.POWERCELL_HOLOGRAMS.getBoolean()
                && pluginManager.isPluginEnabled("HolographicDisplays"))
            hologram = new HologramHolographicDisplays(this);

        // Listeners
        pluginManager.registerEvents(new EntityListeners(this), this);
        pluginManager.registerEvents(new BlockListeners(this), this);
        pluginManager.registerEvents(new InteractListeners(this), this);
        pluginManager.registerEvents(new InventoryListeners(this), this);
        pluginManager.registerEvents(new LoginListeners(this), this);

        // Managers
        this.commandManager = new CommandManager(this);
        this.claimManager = new ClaimManager();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            this.dataManager.getPluginSettings((pluginSettings) -> this.pluginSettings = pluginSettings);
            this.dataManager.getClaims((claims) -> {
                this.claimManager.addClaims(claims);
                if (this.hologram != null)
                    this.claimManager.getRegisteredClaims().forEach(x -> this.hologram.update(x.getPowerCell()));
            });
        }, 20L);

        // Tasks
        this.inviteTask = InviteTask.startTask(this);
        AnimateTask.startTask(this);
        PowerCellTask.startTask(this);
        TrackerTask.startTask(this);

        // Setup Economy
        if (Setting.VAULT_ECONOMY.getBoolean() && pluginManager.isPluginEnabled("Vault"))
            this.economy = new VaultEconomy();
        else if (Setting.RESERVE_ECONOMY.getBoolean() && pluginManager.isPluginEnabled("Reserve"))
            this.economy = new ReserveEconomy();
        else if (Setting.PLAYER_POINTS_ECONOMY.getBoolean() && pluginManager.isPluginEnabled("PlayerPoints"))
            this.economy = new PlayerPointsEconomy();

        // Start Metrics
        new Metrics(this);

        // Database stuff, go!
        try {
            if (Setting.MYSQL_ENABLED.getBoolean()) {
                String hostname = Setting.MYSQL_HOSTNAME.getString();
                int port = Setting.MYSQL_PORT.getInt();
                String database = Setting.MYSQL_DATABASE.getString();
                String username = Setting.MYSQL_USERNAME.getString();
                String password = Setting.MYSQL_PASSWORD.getString();
                boolean useSSL = Setting.MYSQL_USE_SSL.getBoolean();

                this.databaseConnector = new MySQLConnector(this, hostname, port, database, username, password, useSSL);
                this.getLogger().info("Data handler connected using MySQL.");
            } else {
                this.databaseConnector = new SQLiteConnector(this);
                this.getLogger().info("Data handler connected using SQLite.");
            }
        } catch (Exception ex) {
            this.getLogger().severe("Fatal error trying to connect to database. Please make sure all your connection settings are correct and try again. Plugin has been disabled.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        this.dataManager = new DataManager(this.databaseConnector, this);
        this.dataMigrationManager = new DataMigrationManager(this.databaseConnector, this.dataManager);
        this.dataMigrationManager.runMigrations();

        console.sendMessage(Methods.formatText("&a============================="));
    }

    public void reload() {
        this.locale = Locale.getLocale(getConfig().getString("System.Language Mode"));
        this.locale.reloadMessages();
    }

    public ServerVersion getServerVersion() {
        return serverVersion;
    }

    public boolean isServerVersion(ServerVersion version) {
        return serverVersion == version;
    }

    public boolean isServerVersion(ServerVersion... versions) {
        return ArrayUtils.contains(versions, serverVersion);
    }

    public boolean isServerVersionAtLeast(ServerVersion version) {
        return serverVersion.ordinal() >= version.ordinal();
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public Economy getEconomy() {
        return this.economy;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public ClaimManager getClaimManager() {
        return claimManager;
    }

    public DataMigrationManager getDataMigrationManager() {
        return this.dataMigrationManager;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public InviteTask getInviteTask() {
        return inviteTask;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public PluginSettings getPluginSettings() {
        return pluginSettings;
    }
}
