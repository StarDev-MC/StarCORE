package net.starcore.starecon.config;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Economy configuration settings.
 */
public class EconomyConfig {
    private final FileConfiguration config;
    
    public static final String CURRENCY_SYMBOL = "currency-symbol";
    public static final String STARTING_BALANCE = "starting-balance";
    public static final String MAX_BALANCE = "max-balance";
    public static final String DATABASE_TYPE = "database.type";
    public static final String DATABASE_FILE = "database.file";
    public static final String MESSAGE_BALANCE = "messages.balance";
    public static final String MESSAGE_PAY = "messages.pay";
    
    public EconomyConfig(FileConfiguration config) {
        this.config = config;
        setDefaults();
    }
    
    private void setDefaults() {
        config.addDefault(CURRENCY_SYMBOL, "$");
        config.addDefault(STARTING_BALANCE, 1000.0);
        config.addDefault(MAX_BALANCE, 999_999_999.0);
        config.addDefault(DATABASE_TYPE, "sqlite");
        config.addDefault(DATABASE_FILE, "plugins/StarEcon/data.db");
        config.addDefault(MESSAGE_BALANCE, "&eYour balance: %balance%");
        config.addDefault(MESSAGE_PAY, "&e%sender% paid you %amount%");
    }
    
    public String getCurrencySymbol() {
        return config.getString(CURRENCY_SYMBOL, "$");
    }
    
    public double getStartingBalance() {
        return config.getDouble(STARTING_BALANCE, 1000.0);
    }
    
    public double getMaxBalance() {
        return config.getDouble(MAX_BALANCE, 999_999_999.0);
    }
    
    public String getDatabaseType() {
        return config.getString(DATABASE_TYPE, "sqlite");
    }
    
    public String getDatabaseFile() {
        return config.getString(DATABASE_FILE, "plugins/StarEcon/data.db");
    }
    
    public String getMessage(String key) {
        return config.getString("messages." + key, "");
    }
}
