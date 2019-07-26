package com.songoda.ultimateclaims;

import com.songoda.ultimateclaims.claim.ClaimManager;
import com.songoda.ultimateclaims.command.CommandManager;
import com.songoda.ultimateclaims.economy.Economy;
import com.songoda.ultimateclaims.economy.PlayerPointsEconomy;
import com.songoda.ultimateclaims.economy.ReserveEconomy;
import com.songoda.ultimateclaims.economy.VaultEconomy;
import com.songoda.ultimateclaims.listeners.BlockListeners;
import com.songoda.ultimateclaims.listeners.EntityListeners;
import com.songoda.ultimateclaims.listeners.InteractListeners;
import com.songoda.ultimateclaims.listeners.InventoryListeners;
import com.songoda.ultimateclaims.tasks.InviteTask;
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
    
    private SettingsManager settingsManager;
    private CommandManager commandManager;
    private ClaimManager claimManager;

    private InviteTask inviteTask;

    public static UltimateClaims getInstance() {
        return INSTANCE;
    }

    @Override
    public void onDisable() {
        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7UltimateStacker " + this.getDescription().getVersion() + " by &5Songoda <3!"));
        console.sendMessage(Methods.formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(Methods.formatText("&a============================="));
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7EpicHoppers " + this.getDescription().getVersion() + " by &5Songoda <3&7!"));
        console.sendMessage(Methods.formatText("&7Action: &aEnabling&7..."));

        this.settingsManager = new SettingsManager(this);
        this.settingsManager.setupConfig();

        // Setup language
        new Locale(this, "en_US");
        this.locale = Locale.getLocale(getConfig().getString("System.Language Mode"));

        // Running Songoda Updater
        Plugin plugin = new Plugin(this, 65);
        plugin.addModule(new LocaleModule());
        SongodaUpdate.load(plugin);

        PluginManager pluginManager = Bukkit.getPluginManager();

        // Listeners
        pluginManager.registerEvents(new EntityListeners(this), this);
        pluginManager.registerEvents(new BlockListeners(this), this);
        pluginManager.registerEvents(new InteractListeners(this), this);
        pluginManager.registerEvents(new InventoryListeners(this), this);

        // Managers
        this.commandManager = new CommandManager(this);
        this.claimManager = new ClaimManager();

        // Tasks
        this.inviteTask = InviteTask.startTask(this);

        // Setup Economy
        if (Setting.VAULT_ECONOMY.getBoolean() && pluginManager.isPluginEnabled("Vault"))
            this.economy = new VaultEconomy();
        else if (Setting.RESERVE_ECONOMY.getBoolean() && pluginManager.isPluginEnabled("Reserve"))
            this.economy = new ReserveEconomy();
        else if (Setting.PLAYER_POINTS_ECONOMY.getBoolean() && pluginManager.isPluginEnabled("PlayerPoints"))
            this.economy = new PlayerPointsEconomy();

        // Start Metrics
        new Metrics(this);

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

    public InviteTask getInviteTask() {
        return inviteTask;
    }
}
