package net.starcore.starboard.scoreboard;

import net.starcore.api.EconomyService;
import net.starcore.api.MessageProvider;
import net.starcore.api.PlaceholderResolver;
import net.starcore.api.TaskScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {
    private final EconomyService economyService;
    private final TaskScheduler scheduler;
    private final MessageProvider messages;
    private final PlaceholderResolver placeholders;

    public ScoreboardManager(TaskScheduler scheduler, MessageProvider messages,
                             PlaceholderResolver placeholders, EconomyService economyService) {
        this.scheduler = scheduler;
        this.messages = messages;
        this.placeholders = placeholders;
        this.economyService = economyService;
    }

    public void start() {
        scheduler.scheduleSync(this::refreshAll, 20L, 40L);
    }

    public void refreshAll() {
        Bukkit.getOnlinePlayers().forEach(this::applyScoreboard);
    }

    private void applyScoreboard(Player player) {
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) {
            return;
        }
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("starboard", "dummy", messages.colorize("§aStarBoard"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        updateLine(objective, 5, "§7Ping: §f" + player.getPing());
        updateLine(objective, 4, "§7TPS: §f" + getServerTps());
        updateLine(objective, 3, "§7Money: §fLoading...");
        updateLine(objective, 2, "§7Bank: §fLoading...");
        updateLine(objective, 1, "§7World: §f" + player.getWorld().getName());
        player.setScoreboard(board);
    }

    private void updateLine(Objective objective, int score, String line) {
        objective.getScore(line).setScore(score);
    }

    private String getServerTps() {
        if (Bukkit.getTPS().length > 0) {
            return String.format("%.2f", Bukkit.getTPS()[0]);
        }
        return "0.0";
    }
}
