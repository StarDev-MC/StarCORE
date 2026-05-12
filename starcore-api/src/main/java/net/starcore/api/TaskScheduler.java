package net.starcore.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;

public interface TaskScheduler {
    CompletableFuture<Void> runAsync(Runnable task);

    BukkitTask runSync(Runnable task);

    BukkitTask scheduleSync(Runnable task, long delayTicks, long periodTicks);

    static TaskScheduler forPlugin(JavaPlugin plugin) {
        throw new UnsupportedOperationException("Use implementation from StarCore common module");
    }
}
