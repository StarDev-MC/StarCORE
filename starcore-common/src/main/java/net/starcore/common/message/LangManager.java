package net.starcore.common.message;

import net.starcore.api.MessageProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import net.starcore.common.config.ConfigManager;

public class LangManager implements MessageProvider {
    private final ConfigManager configManager;

    public LangManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public String translate(String key) {
        return translate(key, null);
    }

    @Override
    public String translate(String key, CommandSender receiver) {
        String value = configManager.get("lang.yml").getString(key, key);
        if (receiver == null) {
            return colorize(value);
        }
        return colorize(value.replace("{player}", receiver.getName()));
    }

    @Override
    public String colorize(String input) {
        if (input == null) {
            return "";
        }
        return input.replace('&', '§');
    }
}
