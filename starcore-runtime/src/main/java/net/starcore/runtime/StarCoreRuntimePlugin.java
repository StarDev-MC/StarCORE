package net.starcore.runtime;

import org.bukkit.plugin.java.JavaPlugin;

public final class StarCoreRuntimePlugin extends JavaPlugin {
    @Override
    public void onLoad() {
        getLogger().info("StarCore runtime loading...");
    }

    @Override
    public void onEnable() {
        getLogger().info("StarCore runtime enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("StarCore runtime disabled.");
    }
}
