package com.songoda.ultimateclaims;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.songoda.core.Plugin;
import com.songoda.core.SongodaCore;
import com.songoda.core.library.commands.AbstractCommand;
import com.songoda.core.library.commands.CommandManager;
import com.songoda.core.library.database.DataMigrationManager;
import com.songoda.core.library.database.DatabaseConnector;
import com.songoda.core.library.database.MySQLConnector;
import com.songoda.core.library.database.SQLiteConnector;
import com.songoda.core.library.economy.EconomyManager;
import com.songoda.core.library.economy.economies.Economy;
import com.songoda.core.library.locale.Locale;
import com.songoda.core.modules.common.LocaleModule;
import com.songoda.ultimateclaims.claim.ClaimManager;
import com.songoda.ultimateclaims.commands.*;
import com.songoda.ultimateclaims.database.DataManager;
import com.songoda.ultimateclaims.database.migrations._1_InitialMigration;
import com.songoda.ultimateclaims.database.migrations._2_NewPermissions;
import com.songoda.ultimateclaims.database.migrations._3_MemberNames;
import com.songoda.ultimateclaims.hologram.Hologram;
import com.songoda.ultimateclaims.hologram.HologramHolographicDisplays;
import com.songoda.ultimateclaims.hooks.WorldGuardHook;
import com.songoda.ultimateclaims.listeners.*;
import com.songoda.ultimateclaims.settings.PluginSettings;
import com.songoda.ultimateclaims.tasks.*;
import com.songoda.ultimateclaims.utils.Methods;
import com.songoda.ultimateclaims.utils.Metrics;
import com.songoda.ultimateclaims.utils.settings.Setting;
import com.songoda.ultimateclaims.utils.settings.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class UltimateClaims extends JavaPlugin {

    private static UltimateClaims INSTANCE;

    private ConsoleCommandSender console = Bukkit.getConsoleSender();

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
    public void onLoad() {
        WorldGuardHook.addHook("allow-claims", false);
    }

    @Override
    public void onDisable() {
        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7UltimateClaims " + this.getDescription().getVersion() + " by &5Songoda <3!"));
        console.sendMessage(Methods.formatText("&7Action: &cDisabling&7..."));

        // save all claims data
        this.dataManager.bulkUpdateClaims(this.claimManager.getRegisteredClaims());
        this.databaseConnector.closeConnection();

        // cleanup holograms
        if (getHologram() != null)
            HologramsAPI.getHolograms(this).stream().forEach(x -> x.delete());

        // cleanup boss bars
        if (Setting.CLAIMS_BOSSBAR.getBoolean()) {
            this.claimManager.getRegisteredClaims().forEach(x -> {
                x.getVisitorBossBar().removeAll();
                x.getMemberBossBar().removeAll();
            });
        }

        console.sendMessage(Methods.formatText("&a============================="));
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7UltimateClaims " + this.getDescription().getVersion() + " by &5Songoda <3&7!"));
        console.sendMessage(Methods.formatText("&7Action: &aEnabling&7..."));

        // Load Economy
        EconomyManager.load();

        // Setup Setting Manager
        this.settingsManager = new SettingsManager(this);
        this.settingsManager.setupConfig();

        // Setup Economy
        this.economy = EconomyManager.getEconomy(Setting.ECONOMY.getString());

        // Setup Language
        new Locale(this, "en_US");
        this.locale = Locale.getLocale(getConfig().getString("System.Language Mode"));

        // Running Songoda Core
        Plugin plugin = new Plugin(this, 65);
        plugin.addModule(new LocaleModule());
        SongodaCore.load(plugin);

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

        // Load Commands
        this.commandManager = new CommandManager();
        AbstractCommand commandUltimateClaims = this.commandManager.addCommand(new CommandUltimateClaims(this));
        this.commandManager.setExecutor("UltimateClaims")
                .addCommands(new CommandSettings(this, commandUltimateClaims),
                        new CommandReload(this, commandUltimateClaims),
                        new CommandClaim(this, commandUltimateClaims),
                        new CommandUnClaim(this, commandUltimateClaims),
                        new CommandShow(this, commandUltimateClaims),
                        new CommandInvite(this, commandUltimateClaims),
                        new CommandAccept(this, commandUltimateClaims),
                        new CommandAddMember(this, commandUltimateClaims),
                        new CommandKick(this, commandUltimateClaims),
                        new CommandDissolve(this, commandUltimateClaims),
                        new CommandLeave(this, commandUltimateClaims),
                        new CommandLock(this, commandUltimateClaims),
                        new CommandHome(this, commandUltimateClaims),
                        new CommandSetHome(this, commandUltimateClaims),
                        new CommandBan(this, commandUltimateClaims),
                        new CommandUnBan(this, commandUltimateClaims),
                        new CommandRecipe(this, commandUltimateClaims),
                        new CommandSetSpawn(this, commandUltimateClaims),
                        new CommandName(this, commandUltimateClaims))
                .load(this);


        this.claimManager = new ClaimManager();

        // Tasks
        this.inviteTask = InviteTask.startTask(this);
        AnimateTask.startTask(this);
        PowerCellTask.startTask(this);
        TrackerTask.startTask(this);
        VisualizeTask.startTask(this);

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
        this.dataMigrationManager = new DataMigrationManager(this.databaseConnector, this.dataManager,
                new _1_InitialMigration(),
                new _2_NewPermissions(),
                new _3_MemberNames());
        this.dataMigrationManager.runMigrations();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            this.dataManager.getPluginSettings((pluginSettings) -> this.pluginSettings = pluginSettings);
            this.dataManager.getClaims((claims) -> {
                this.claimManager.addClaims(claims);
                if (this.hologram != null)
                    this.claimManager.getRegisteredClaims().forEach(x -> this.hologram.update(x.getPowerCell()));
            });
        }, 20L);

        console.sendMessage(Methods.formatText("&a============================="));
    }

    public void reload() {
        this.locale = Locale.getLocale(getConfig().getString("System.Language Mode"));
        this.locale.reloadMessages();
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

    public DatabaseConnector getDatabaseConnector() {
        return this.databaseConnector;
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
