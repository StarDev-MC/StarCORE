package net.starcore.common.placeholder;

import net.starcore.api.PlaceholderResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlaceholderManager implements PlaceholderResolver {
    private final Map<String, Placeholder> placeholders = new ConcurrentHashMap<>();

    public PlaceholderManager() {
        register("player", sender -> sender == null ? "" : sender.getName());
        register("online", sender -> String.valueOf(sender == null ? 0 : sender.getServer().getOnlinePlayers().size()));
    }

    public void register(String key, Placeholder placeholder) {
        placeholders.put(key.toLowerCase(), placeholder);
    }

    @Override
    public String resolve(String source, CommandSender sender) {
        String text = source;
        for (Map.Entry<String, Placeholder> entry : placeholders.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue().resolve(sender));
        }
        if (sender instanceof Player player) {
            text = text.replace("{player_uuid}", player.getUniqueId().toString());
        }
        return text;
    }

    public interface Placeholder {
        String resolve(CommandSender sender);
    }
}
