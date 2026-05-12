package net.starcore.common.task;

import net.starcore.api.TaskScheduler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;

public class AsyncTaskScheduler implements TaskScheduler {
    private final JavaPlugin plugin;

    public AsyncTaskScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Void> runAsync(Runnable task) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                task.run();
                future.complete(null);
            } catch (Throwable ex) {
                future.completeExceptionally(ex);
            }
        });
        return future;
    }

    @Override
    public BukkitTask runSync(Runnable task) {
        return plugin.getServer().getScheduler().runTask(plugin, task);
    }

    @Override
    public BukkitTask scheduleSync(Runnable task, long delayTicks, long periodTicks) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
    }
}
