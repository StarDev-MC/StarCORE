package net.starcore.starboard.scoreboard;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ScoreboardConfig {
    private final FileConfiguration config;

    public ScoreboardConfig(JavaPlugin plugin) {
        saveDefault(plugin, "scoreboards.yml");
        File file = new File(plugin.getDataFolder(), "scoreboards.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    public String getDefaultTitle() {
        return config.getString("default.title", "StarMC");
    }

    public int getRefreshInterval() {
        return Math.max(20, config.getInt("default.refresh-interval", 40));
    }

    public List<LineTemplate> getLines(String worldName, String rankName) {
        if (worldName != null && config.contains("per-world." + worldName + ".lines")) {
            return loadLines("per-world." + worldName + ".lines");
        }
        if (rankName != null && config.contains("per-rank." + rankName.toLowerCase(Locale.ROOT) + ".lines")) {
            return loadLines("per-rank." + rankName.toLowerCase(Locale.ROOT) + ".lines");
        }
        return loadLines("default.lines");
    }

    public String getTitle(String worldName, String rankName) {
        if (worldName != null && config.contains("per-world." + worldName + ".title")) {
            return config.getString("per-world." + worldName + ".title");
        }
        if (rankName != null && config.contains("per-rank." + rankName.toLowerCase(Locale.ROOT) + ".title")) {
            return config.getString("per-rank." + rankName.toLowerCase(Locale.ROOT) + ".title");
        }
        return config.getString("default.title", "StarMC");
    }

    private List<LineTemplate> loadLines(String path) {
        if (!config.contains(path)) {
            return Collections.emptyList();
        }
        Object raw = config.getList(path);
        if (!(raw instanceof List<?> items)) {
            return Collections.emptyList();
        }

        List<LineTemplate> lines = new ArrayList<>();
        for (Object item : items) {
            if (item instanceof String text) {
                lines.add(new LineTemplate(text, ""));
            } else if (item instanceof Map<?, ?> map) {
                Object textValue = map.get("text");
                Object conditionValue = map.get("condition");
                if (textValue != null) {
                    lines.add(new LineTemplate(String.valueOf(textValue), conditionValue == null ? "" : String.valueOf(conditionValue)));
                }
            } else if (item instanceof ConfigurationSection section) {
                String text = section.getString("text", "");
                String condition = section.getString("condition", "");
                if (!text.isBlank()) {
                    lines.add(new LineTemplate(text, condition));
                }
            }
        }
        return Collections.unmodifiableList(lines);
    }

    private void saveDefault(JavaPlugin plugin, String fileName) {
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
                } catch (Exception ignored) {
                }
            }
        }
    }

    public record LineTemplate(String text, String condition) {
    }
}
