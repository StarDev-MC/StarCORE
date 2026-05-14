package net.starcore.starboard.scoreboard;

import net.md_5.bungee.api.ChatColor;
import net.starcore.api.BankService;
import net.starcore.api.EconomyService;
import net.starcore.api.MessageProvider;
import net.starcore.api.PlaceholderResolver;
import net.starcore.api.RankService;
import net.starcore.api.TaskScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreboardManager {
    private static final String OBJECTIVE_NAME = "starboard";
    private static final String ZERO_WIDTH_SPACE = "\u200B";
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%(\\w+)%");

    private final TaskScheduler scheduler;
    private final MessageProvider messages;
    private final PlaceholderResolver placeholders;
    private final EconomyService economyService;
    private final BankService bankService;
    private final RankService rankService;
    private final ScoreboardConfig scoreboardConfig;
    private final AnimationConfig animationConfig;
    private final OptionalPlaceholderApi placeholderApi;

    private final Map<UUID, PlayerState> states = new ConcurrentHashMap<>();
    private final Map<UUID, CachedValue<Long>> economyCache = new ConcurrentHashMap<>();
    private final Map<UUID, CachedValue<Long>> bankCache = new ConcurrentHashMap<>();
    private final Map<UUID, CachedValue<String>> rankCache = new ConcurrentHashMap<>();
    private final long cacheDurationMs = 5000L;
    private long tick;

    public ScoreboardManager(TaskScheduler scheduler, MessageProvider messages,
                             PlaceholderResolver placeholders, EconomyService economyService,
                             BankService bankService, RankService rankService, JavaPlugin plugin) {
        this.scheduler = scheduler;
        this.messages = messages;
        this.placeholders = placeholders;
        this.economyService = economyService;
        this.bankService = bankService;
        this.rankService = rankService;
        this.scoreboardConfig = new ScoreboardConfig(plugin);
        this.animationConfig = new AnimationConfig(plugin);
        this.placeholderApi = OptionalPlaceholderApi.create();
    }

    public void start() {
        scheduler.scheduleSync(this::refreshAll, 20L, scoreboardConfig.getRefreshInterval());
    }

    public void refreshAll() {
        Bukkit.getOnlinePlayers().forEach(this::refreshPlayer);
    }

    private void refreshPlayer(Player player) {
        PlayerState state = states.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerState());
        state.ensureBoard(player, animationConfig, scoreboardConfig, messages);

        String rank = getCachedRank(player);
        String title = buildAnimatedTitle(player, rank);
        if (!title.equals(state.lastTitle)) {
            state.objective.setDisplayName(title);
            state.lastTitle = title;
        }

        List<String> lines = buildLines(player, rank);
        if (!state.updateLines(lines)) {
            return;
        }
    }

    private List<String> buildLines(Player player, String rank) {
        List<String> lines = new ArrayList<>();
        for (ScoreboardConfig.LineTemplate template : scoreboardConfig.getLines(player.getWorld().getName(), rank)) {
            String line = replacePlaceholders(player, template.text());
            if (template.condition() != null && !template.condition().isBlank()) {
                String condition = replacePlaceholders(player, template.condition());
                if (!evaluateCondition(condition)) {
                    continue;
                }
            }
            if (line == null || line.isBlank()) {
                continue;
            }
            lines.add(line);
        }
        if (lines.isEmpty()) {
            lines.add(messages.colorize("&7"));
        }
        return lines;
    }

    private String replacePlaceholders(Player player, String text) {
        if (text == null) {
            return "";
        }
        String result = text;
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(result);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String token = matcher.group(1).toLowerCase(Locale.ROOT);
            String replacement = resolveToken(player, token);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(buffer);
        result = buffer.toString();

        if (placeholderApi.isAvailable()) {
            result = placeholderApi.apply(player, result);
        }
        result = placeholders.resolve(result, player);
        return messages.colorize(result);
    }

    private String resolveToken(Player player, String token) {
        return switch (token) {
            case "player_name" -> player.getName();
            case "player_kills" -> String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS));
            case "player_deaths" -> String.valueOf(player.getStatistic(Statistic.DEATHS));
            case "starecon_balance_formatted" -> getEconomyBalance(player);
            case "starbank_balance_formatted" -> getBankBalance(player);
            case "starrole_rank" -> getRankValue(player);
            default -> "%" + token + "%";
        };
    }

    private String getEconomyBalance(Player player) {
        if (economyService == null) {
            return messages.colorize("&7N/A");
        }
        CachedValue<Long> cached = economyCache.computeIfAbsent(player.getUniqueId(), uuid -> new CachedValue<>());
        if (cached.isStale(cacheDurationMs)) {
            requestEconomy(player, cached);
        }
        Long balance = cached.getValue();
        return balance == null ? messages.colorize("&7Loading...") : economyService.format(balance);
    }

    private String getBankBalance(Player player) {
        if (bankService == null) {
            return messages.colorize("&7N/A");
        }
        CachedValue<Long> cached = bankCache.computeIfAbsent(player.getUniqueId(), uuid -> new CachedValue<>());
        if (cached.isStale(cacheDurationMs)) {
            requestBank(player, cached);
        }
        Long balance = cached.getValue();
        return balance == null ? messages.colorize("&7Loading...") : economyService.format(balance);
    }

    private String getRankValue(Player player) {
        if (rankService == null) {
            return "Member";
        }
        CachedValue<String> cached = rankCache.computeIfAbsent(player.getUniqueId(), uuid -> new CachedValue<>());
        if (cached.isStale(cacheDurationMs)) {
            requestRank(player, cached);
        }
        String rank = cached.getValue();
        return rank == null ? "Member" : rank;
    }

    private void requestEconomy(Player player, CachedValue<Long> cached) {
        if (cached.isPending()) {
            return;
        }
        cached.markPending();
        economyService.getBalance(player.getUniqueId()).whenComplete((balance, error) -> {
            cached.update(balance);
            cached.clearPending();
            if (player.isOnline()) {
                scheduler.runSync(() -> refreshPlayer(player));
            }
        });
    }

    private void requestBank(Player player, CachedValue<Long> cached) {
        if (cached.isPending()) {
            return;
        }
        cached.markPending();
        bankService.getBalance(player.getUniqueId()).whenComplete((balance, error) -> {
            cached.update(balance);
            cached.clearPending();
            if (player.isOnline()) {
                scheduler.runSync(() -> refreshPlayer(player));
            }
        });
    }

    private void requestRank(Player player, CachedValue<String> cached) {
        if (cached.isPending()) {
            return;
        }
        cached.markPending();
        rankService.getRank(player.getUniqueId()).whenComplete((rankValue, error) -> {
            cached.update(rankValue);
            cached.clearPending();
            if (player.isOnline()) {
                scheduler.runSync(() -> refreshPlayer(player));
            }
        });
    }

    private String getCachedRank(Player player) {
        CachedValue<String> cached = rankCache.get(player.getUniqueId());
        return cached == null ? null : cached.getValue();
    }

    private boolean evaluateCondition(String expression) {
        if (expression == null || expression.isBlank()) {
            return true;
        }
        String trimmed = expression.trim();
        if (trimmed.contains("!=")) {
            String[] parts = trimmed.split("!=", 2);
            return !parts[0].trim().equals(parts[1].trim());
        }
        if (trimmed.contains("==")) {
            String[] parts = trimmed.split("==", 2);
            return parts[0].trim().equals(parts[1].trim());
        }
        if (trimmed.matches("^[0-9.]+\\s*[><]=?\\s*[0-9.]+$")) {
            return compareNumbers(trimmed);
        }
        return !trimmed.equalsIgnoreCase("false");
    }

    private boolean compareNumbers(String expression) {
        String normalized = expression.replace(" ", "");
        try {
            if (normalized.contains(">=")) {
                String[] parts = normalized.split(">=");
                return Double.parseDouble(parts[0]) >= Double.parseDouble(parts[1]);
            }
            if (normalized.contains("<=")) {
                String[] parts = normalized.split("<=" );
                return Double.parseDouble(parts[0]) <= Double.parseDouble(parts[1]);
            }
            if (normalized.contains(">")) {
                String[] parts = normalized.split(">");
                return Double.parseDouble(parts[0]) > Double.parseDouble(parts[1]);
            }
            if (normalized.contains("<")) {
                String[] parts = normalized.split("<");
                return Double.parseDouble(parts[0]) < Double.parseDouble(parts[1]);
            }
        } catch (NumberFormatException ignored) {
        }
        return false;
    }

    private String buildAnimatedTitle(Player player, String rank) {
        String raw = scoreboardConfig.getTitle(player.getWorld().getName(), rank);
        if (raw == null || raw.isBlank()) {
            raw = "StarMC";
        }
        String base = messages.colorize(raw);
        if (!animationConfig.useRgb() || animationConfig.getColors().isEmpty()) {
            return base;
        }
        List<RgbColor> colors = parseColors(animationConfig.getColors());
        if (colors.isEmpty()) {
            return base;
        }
        switch (animationConfig.getStyle()) {
            case SCROLL:
                return animateScroll(base, colors, tick);
            case GLOW:
                return animateGlow(base, colors, tick);
            default:
                return animateGradient(base, colors, tick);
        }
    }

    private List<RgbColor> parseColors(List<String> colorStrings) {
        List<RgbColor> parsed = new ArrayList<>();
        for (String raw : colorStrings) {
            if (raw == null || raw.isBlank()) {
                continue;
            }
            RgbColor color = RgbColor.fromString(raw.trim());
            if (color != null) {
                parsed.add(color);
            }
        }
        return parsed;
    }

    private String animateGradient(String text, List<RgbColor> palette, long frame) {
        StringBuilder builder = new StringBuilder();
        double offset = (frame * animationConfig.getSpeed()) / 20.0;
        int visibleLength = text.length();
        for (int index = 0; index < visibleLength; index++) {
            char symbol = text.charAt(index);
            double position = (index + offset) / Math.max(1, visibleLength);
            RgbColor color = interpolateColor(palette, position);
            builder.append(color.toChatColor()).append(symbol);
        }
        tick++;
        return builder.toString();
    }

    private String animateScroll(String text, List<RgbColor> palette, long frame) {
        StringBuilder builder = new StringBuilder();
        int shift = (int) (frame * animationConfig.getSpeed()) % palette.size();
        for (int index = 0; index < text.length(); index++) {
            char symbol = text.charAt(index);
            RgbColor color = palette.get((index + shift + palette.size()) % palette.size());
            builder.append(color.toChatColor()).append(symbol);
        }
        tick++;
        return builder.toString();
    }

    private String animateGlow(String text, List<RgbColor> palette, long frame) {
        StringBuilder builder = new StringBuilder();
        double phase = frame * animationConfig.getSpeed() / 10.0;
        for (int index = 0; index < text.length(); index++) {
            char symbol = text.charAt(index);
            double progress = (Math.sin((index * 0.6) + phase) + 1.0) / 2.0;
            RgbColor base = interpolateColor(palette, (double) index / Math.max(1, text.length()));
            RgbColor glow = base.mix(RgbColor.WHITE, 0.3 + 0.7 * progress);
            builder.append(glow.toChatColor()).append(symbol);
        }
        tick++;
        return builder.toString();
    }

    private RgbColor interpolateColor(List<RgbColor> palette, double position) {
        if (palette.isEmpty()) {
            return RgbColor.WHITE;
        }
        position = position - Math.floor(position);
        if (palette.size() == 1) {
            return palette.get(0);
        }
        double segment = position * (palette.size() - 1);
        int index = Math.min(palette.size() - 2, (int) Math.floor(segment));
        double fraction = segment - index;
        return palette.get(index).interpolate(palette.get(index + 1), fraction);
    }

    private static String uniqueEntry(String text, int duplicateIndex) {
        if (text.isBlank()) {
            text = " ";
        }
        String suffix = ZERO_WIDTH_SPACE.repeat(Math.max(1, duplicateIndex + 1));
        return text + suffix;
    }

    private static final class PlayerState {
        private Scoreboard scoreboard;
        private Objective objective;
        private String lastTitle = "";
        private final Map<Integer, String> entries = new HashMap<>();

        public void ensureBoard(Player player, AnimationConfig animationConfig, ScoreboardConfig boardConfig, MessageProvider messages) {
            Scoreboard current = player.getScoreboard();
            if (current == null || !isManaged(current)) {
                current = Bukkit.getScoreboardManager().getNewScoreboard();
                this.objective = current.registerNewObjective(OBJECTIVE_NAME, "dummy", messages.colorize(boardConfig.getTitle(player.getWorld().getName(), null)));
                this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                player.setScoreboard(current);
                this.scoreboard = current;
                this.lastTitle = "";
                this.entries.clear();
                return;
            }
            this.scoreboard = current;
            if (this.objective == null) {
                this.objective = current.getObjective(OBJECTIVE_NAME);
                if (this.objective == null) {
                    this.objective = current.registerNewObjective(OBJECTIVE_NAME, "dummy", messages.colorize(boardConfig.getTitle(player.getWorld().getName(), null)));
                    this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                }
            }
        }

        private boolean isManaged(Scoreboard board) {
            return board.getObjective(OBJECTIVE_NAME) != null;
        }

        public boolean updateLines(List<String> lines) {
            boolean changed = false;
            Map<String, Integer> duplicateCounts = new HashMap<>();
            for (int index = 0; index < lines.size(); index++) {
                String line = lines.get(index);
                int duplicateIndex = duplicateCounts.getOrDefault(line, 0);
                duplicateCounts.put(line, duplicateIndex + 1);
                String entry = uniqueEntry(line, duplicateIndex);
                int score = lines.size() - index;
                String existing = entries.get(index);
                if (existing != null && existing.equals(entry)) {
                    continue;
                }
                if (existing != null) {
                    scoreboard.resetScores(existing);
                }
                objective.getScore(entry).setScore(score);
                entries.put(index, entry);
                changed = true;
            }
            int previousSize = entries.size();
            for (int index = lines.size(); index < previousSize; index++) {
                String oldEntry = entries.remove(index);
                if (oldEntry != null) {
                    scoreboard.resetScores(oldEntry);
                    changed = true;
                }
            }
            return changed;
        }
    }

    private static final class CachedValue<T> {
        private volatile T value;
        private volatile long lastUpdate;
        private volatile boolean pending;

        public T getValue() {
            return value;
        }

        public void update(T value) {
            this.value = value;
            this.lastUpdate = System.currentTimeMillis();
        }

        public boolean isStale(long thresholdMs) {
            return System.currentTimeMillis() - lastUpdate >= thresholdMs;
        }

        public boolean isPending() {
            return pending;
        }

        public void markPending() {
            pending = true;
        }

        public void clearPending() {
            pending = false;
        }
    }

    private static final class RgbColor {
        private static final RgbColor WHITE = new RgbColor(255, 255, 255);
        private final int red;
        private final int green;
        private final int blue;

        private RgbColor(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public ChatColor toChatColor() {
            try {
                return ChatColor.of(new Color(red, green, blue));
            } catch (Exception ex) {
                return ChatColor.WHITE;
            }
        }

        public RgbColor interpolate(RgbColor other, double ratio) {
            double clamped = Math.max(0.0, Math.min(1.0, ratio));
            int r = (int) Math.round(red + (other.red - red) * clamped);
            int g = (int) Math.round(green + (other.green - green) * clamped);
            int b = (int) Math.round(blue + (other.blue - blue) * clamped);
            return new RgbColor(r, g, b);
        }

        public RgbColor mix(RgbColor other, double ratio) {
            return interpolate(other, ratio);
        }

        public static RgbColor fromString(String raw) {
            if (raw == null || raw.isBlank()) {
                return null;
            }
            String value = raw.trim();
            if (value.startsWith("#")) {
                try {
                    Color color = Color.decode(value);
                    return new RgbColor(color.getRed(), color.getGreen(), color.getBlue());
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
            try {
                ChatColor chatColor = ChatColor.of(value);
                java.awt.Color color = chatColor.getColor();
                return new RgbColor(color.getRed(), color.getGreen(), color.getBlue());
            } catch (Exception ex) {
                try {
                    ChatColor legacy = ChatColor.valueOf(value.toUpperCase(Locale.ROOT));
                    java.awt.Color color = legacy.getColor();
                    return new RgbColor(color.getRed(), color.getGreen(), color.getBlue());
                } catch (Exception ignore) {
                    return null;
                }
            }
        }
    }

    private static final class OptionalPlaceholderApi {
        private final boolean available;
        private final java.lang.reflect.Method setPlaceholdersMethod;

        private OptionalPlaceholderApi(boolean available, java.lang.reflect.Method setPlaceholdersMethod) {
            this.available = available;
            this.setPlaceholdersMethod = setPlaceholdersMethod;
        }

        public static OptionalPlaceholderApi create() {
            try {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
                    return new OptionalPlaceholderApi(false, null);
                }
                Class<?> placeholderApi = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
                java.lang.reflect.Method method = placeholderApi.getMethod("setPlaceholders", org.bukkit.entity.Player.class, String.class);
                return new OptionalPlaceholderApi(true, method);
            } catch (Exception ex) {
                return new OptionalPlaceholderApi(false, null);
            }
        }

        public boolean isAvailable() {
            return available;
        }

        public String apply(Player player, String text) {
            if (!available || player == null || text == null) {
                return text;
            }
            try {
                return (String) setPlaceholdersMethod.invoke(null, player, text);
            } catch (Exception ignored) {
                return text;
            }
        }
    }
}
