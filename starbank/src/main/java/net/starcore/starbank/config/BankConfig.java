package net.starcore.starbank.config;

import net.starcore.plugin.AbstractStarCorePlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class BankConfig {
    private final AbstractStarCorePlugin plugin;
    private FileConfiguration config;

    public BankConfig(AbstractStarCorePlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public long getStartingBalance() {
        return config.getLong("bank.starting-balance", 0);
    }

    public long getMaxBalance() {
        return config.getLong("bank.max-balance", 1000000000L);
    }

    public long getTransactionLimit() {
        return config.getLong("bank.transaction-limit", 1000000L);
    }

    public String getMessage(String key) {
        return config.getString("messages." + key, "Message not found: " + key);
    }

    public String getGuiTitle() {
        return config.getString("gui.title", "Bank");
    }

    public int getGuiSize() {
        return config.getInt("gui.size", 27);
    }
}