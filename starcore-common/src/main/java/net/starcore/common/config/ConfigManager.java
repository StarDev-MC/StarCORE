package net.starcore.common.config;

import net.starcore.api.ConfigProvider;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager implements ConfigProvider {
    private final JavaPlugin plugin;
    private final Map<String, FileConfiguration> configurations = new ConcurrentHashMap<>();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    @Override
    public void load() {
        registerFile("config.yml");
        registerFile("lang.yml");
        registerFile("shop.yml");
    }

    private void registerFile(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            if (plugin.getResource(fileName) != null) {
                plugin.saveResource(fileName, false);
            } else {
                try {
                    if (file.getParentFile() != null) {
                        file.getParentFile().mkdirs();
                    }
                    file.createNewFile();
                } catch (IOException ex) {
                    plugin.getLogger().severe("Failed to create " + fileName + ": " + ex.getMessage());
                }
            }
        }
        configurations.put(fileName, YamlConfiguration.loadConfiguration(file));
    }

    @Override
    public void save() {
        configurations.forEach((name, configuration) -> {
            try {
                configuration.save(new File(plugin.getDataFolder(), name));
            } catch (IOException e) {
                plugin.getLogger().severe("Unable to save " + name + ": " + e.getMessage());
            }
        });
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public Optional<String> getString(String path) {
        return Optional.ofNullable(configurations.get("config.yml").getString(path));
    }

    @Override
    public Optional<Integer> getInt(String path) {
        return Optional.ofNullable(configurations.get("config.yml").getInt(path));
    }

    public FileConfiguration get(String fileName) {
        return configurations.get(fileName);
    }
}
