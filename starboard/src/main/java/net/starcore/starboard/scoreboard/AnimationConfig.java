package net.starcore.starboard.scoreboard;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AnimationConfig {
    public enum Style {
        GRADIENT,
        SCROLL,
        GLOW
    }

    private final Style style;
    private final int speed;
    private final boolean rgb;
    private final boolean legacyFallback;
    private final List<String> colors;

    public AnimationConfig(JavaPlugin plugin) {
        saveDefault(plugin, "animations.yml");
        File file = new File(plugin.getDataFolder(), "animations.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.style = parseStyle(config.getString("title.style", "gradient"));
        this.speed = Math.max(1, config.getInt("title.speed", 5));
        this.rgb = config.getBoolean("title.rgb", true);
        this.legacyFallback = config.getBoolean("title.legacy-fallback", true);
        this.colors = loadColors(config.getStringList("title.colors"));
    }

    public Style getStyle() {
        return style;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean useRgb() {
        return rgb;
    }

    public boolean useLegacyFallback() {
        return legacyFallback;
    }

    public List<String> getColors() {
        return colors;
    }

    private Style parseStyle(String raw) {
        if (raw == null) {
            return Style.GRADIENT;
        }
        try {
            return Style.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return Style.GRADIENT;
        }
    }

    private List<String> loadColors(List<String> list) {
        if (list == null || list.isEmpty()) {
            return List.of("#ff5f6d", "#ffc371", "#3a86ff");
        }
        List<String> colors = new ArrayList<>();
        for (String raw : list) {
            if (raw == null || raw.isBlank()) {
                continue;
            }
            colors.add(raw.trim());
        }
        return Collections.unmodifiableList(colors);
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
}
