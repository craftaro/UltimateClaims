package com.songoda.ultimateclaims;

import com.songoda.core.SongodaCore;
import com.songoda.core.library.commands.CommandManager;
import com.songoda.core.library.database.DataMigrationManager;
import com.songoda.core.library.database.DatabaseConnector;
import com.songoda.core.library.database.MySQLConnector;
import com.songoda.core.library.database.SQLiteConnector;
import com.songoda.core.library.economy.EconomyManager;
import com.songoda.core.library.hologram.HologramManager;
import com.songoda.core.library.hooks.WorldGuardHook;
import com.songoda.core.library.locale.Locale;
import com.songoda.ultimateclaims.claim.ClaimManager;
import com.songoda.ultimateclaims.commands.*;
import com.songoda.ultimateclaims.database.DataManager;
import com.songoda.ultimateclaims.database.migrations._1_InitialMigration;
import com.songoda.ultimateclaims.database.migrations._2_NewPermissions;
import com.songoda.ultimateclaims.database.migrations._3_MemberNames;
import com.songoda.ultimateclaims.hologram.Hologram;
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
        HologramManager.removeAllHolograms();

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

        // Load Hologram
        HologramManager.load(this);

        // Setup Setting Manager
        this.settingsManager = new SettingsManager(this);
        this.settingsManager.setupConfig();

        // Setup Economy
        EconomyManager.setPreferredEconomy(Setting.ECONOMY.getString());

        // Setup Hologram
        HologramManager.setPreferredHologramPlugin(Setting.HOLOGRAM.getString());

        // Setup Language
        new Locale(this, "en_US");
        this.locale = Locale.getLocale(getConfig().getString("System.Language Mode"));

        // Register in Songoda Core
        SongodaCore.registerPlugin(this, 65);

        PluginManager pluginManager = Bukkit.getPluginManager();

        // Register Hologram Plugin
        if (Setting.POWERCELL_HOLOGRAMS.getBoolean())
            hologram = new Hologram(this);

        // Listeners
        pluginManager.registerEvents(new EntityListeners(this), this);
        pluginManager.registerEvents(new BlockListeners(this), this);
        pluginManager.registerEvents(new InteractListeners(this), this);
        pluginManager.registerEvents(new InventoryListeners(this), this);
        pluginManager.registerEvents(new LoginListeners(this), this);

        // Load Commands
        this.commandManager = new CommandManager(this);
        this.commandManager.addCommand(new CommandUltimateClaims(this))
                .addSubCommands(
                        new CommandSettings(this),
                        new CommandReload(this),
                        new CommandClaim(this),
                        new CommandUnClaim(this),
                        new CommandShow(this),
                        new CommandInvite(this),
                        new CommandAccept(this),
                        new CommandAddMember(this),
                        new CommandKick(this),
                        new CommandDissolve(this),
                        new CommandLeave(this),
                        new CommandLock(this),
                        new CommandHome(this),
                        new CommandSetHome(this),
                        new CommandBan(this),
                        new CommandUnBan(this),
                        new CommandRecipe(this),
                        new CommandSetSpawn(this),
                        new CommandName(this)
                );

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
