package net.starcore.common.event;

import net.starcore.api.EventBus;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DefaultEventBus implements EventBus {
    private final PluginManager pluginManager;
    private final JavaPlugin plugin;

    public DefaultEventBus(JavaPlugin plugin) {
        this.plugin = plugin;
        this.pluginManager = plugin.getServer().getPluginManager();
    }

    @Override
    public void register(Listener listener) {
        pluginManager.registerEvents(listener, plugin);
    }

    @Override
    public void unregister(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    @Override
    public void publish(Event event) {
        pluginManager.callEvent(event);
    }
}
