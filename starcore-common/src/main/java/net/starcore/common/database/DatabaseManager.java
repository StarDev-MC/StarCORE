package net.starcore.common.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.starcore.api.DatabaseService;
import net.starcore.api.TaskScheduler;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager implements DatabaseService {
    private final HikariDataSource dataSource;
    private final TaskScheduler scheduler;

    public DatabaseManager(JavaPlugin plugin, TaskScheduler scheduler) {
        this.scheduler = scheduler;
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setPoolName("StarCorePool");
        config.setJdbcUrl(loadJdbcUrl(plugin));
        config.setDriverClassName("org.sqlite.JDBC");
        config.addDataSourceProperty("journal_mode", "WAL");
        config.addDataSourceProperty("cache_size", "5000");
        this.dataSource = new HikariDataSource(config);
    }

    private String loadJdbcUrl(JavaPlugin plugin) {
        String folder = plugin.getDataFolder().getAbsolutePath().replaceAll("\\\\", "/");
        return "jdbc:sqlite:" + folder + "/starcore.db";
    }

    @Override
    public CompletableFuture<Connection> getConnection() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return dataSource.getConnection();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public CompletableFuture<Void> executeAsync(SqlConsumer<Connection> consumer) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection()) {
                consumer.accept(connection);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public CompletableFuture<Void> executeUpdateAsync(String sql, Object... params) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
                for (int index = 0; index < params.length; index++) {
                    statement.setObject(index + 1, params[index]);
                }
                statement.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public <T> CompletableFuture<T> supplyAsync(DatabaseService.SqlFunction<Connection, T> function) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection()) {
                return function.apply(connection);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
