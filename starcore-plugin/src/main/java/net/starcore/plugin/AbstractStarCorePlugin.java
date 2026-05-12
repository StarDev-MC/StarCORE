package net.starcore.plugin;

import net.starcore.api.CooldownService;
import net.starcore.api.DatabaseService;
import net.starcore.api.EventBus;
import net.starcore.api.GuiProvider;
import net.starcore.api.MessageProvider;
import net.starcore.api.PlaceholderResolver;
import net.starcore.api.PlayerDataService;
import net.starcore.api.ServiceRegistry;
import net.starcore.api.TaskScheduler;
import net.starcore.common.DefaultServiceRegistry;
import net.starcore.common.cache.GuavaCacheStore;
import net.starcore.common.config.ConfigManager;
import net.starcore.common.cooldown.CooldownManager;
import net.starcore.common.data.PlayerDataManager;
import net.starcore.common.database.DatabaseManager;
import net.starcore.common.event.DefaultEventBus;
import net.starcore.common.gui.GuiFactory;
import net.starcore.common.message.LangManager;
import net.starcore.common.placeholder.PlaceholderManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractStarCorePlugin extends JavaPlugin {
    protected final ServiceRegistry services = new DefaultServiceRegistry();
    protected ConfigManager configManager;
    protected MessageProvider messageProvider;
    protected PlaceholderResolver placeholderResolver;
    protected CooldownService cooldownService;
    protected TaskScheduler taskScheduler;
    protected DatabaseService databaseService;
    protected PlayerDataService playerDataService;
    protected GuiProvider guiProvider;
    protected EventBus eventBus;

    @Override
    public void onEnable() {
        initializeCore();
        registerCoreServices();
        enablePlugin();
    }

    @Override
    public void onDisable() {
        disablePlugin();
        shutdownCore();
    }

    private void initializeCore() {
        configManager = new ConfigManager(this);
        messageProvider = new LangManager(configManager);
        placeholderResolver = new PlaceholderManager();
        cooldownService = new CooldownManager();
        taskScheduler = new net.starcore.common.task.AsyncTaskScheduler(this);
        databaseService = new DatabaseManager(this, taskScheduler);
        playerDataService = new PlayerDataManager(databaseService);
        guiProvider = new GuiFactory();
        eventBus = new DefaultEventBus(this);
    }

    private void registerCoreServices() {
        services.register(ConfigManager.class, configManager);
        services.register(MessageProvider.class, messageProvider);
        services.register(PlaceholderResolver.class, placeholderResolver);
        services.register(CooldownService.class, cooldownService);
        services.register(TaskScheduler.class, taskScheduler);
        services.register(DatabaseService.class, databaseService);
        services.register(PlayerDataService.class, playerDataService);
        services.register(GuiProvider.class, guiProvider);
        services.register(EventBus.class, eventBus);
    }

    protected <T> T getService(Class<T> serviceType) {
        return services.get(serviceType).orElse(null);
    }

    protected void registerCommand(net.starcore.api.StarCommand command) {
        PluginCommand pluginCommand = getCommand(command.getName());
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }
    }

    protected abstract void enablePlugin();

    protected void disablePlugin() {
    }

    private void shutdownCore() {
        if (databaseService != null) {
            try {
                databaseService.close();
            } catch (Exception ignored) {
            }
        }
    }
}
