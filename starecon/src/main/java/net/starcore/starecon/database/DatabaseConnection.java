package net.starcore.starecon.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

/**
 * Database connection manager using HikariCP for pooling.
 * Supports SQLite, MySQL, and MariaDB.
 */
public class DatabaseConnection {
    private HikariDataSource dataSource;
    private final DatabaseConfig config;

    public DatabaseConnection(DatabaseConfig config) {
        this.config = config;
    }

    /**
     * Initialize the connection pool.
     */
    public void initialize() throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();
        
        switch (config.getDatabaseType().toLowerCase()) {
            case "sqlite" -> {
                hikariConfig.setJdbcUrl("jdbc:sqlite:" + config.getFilePath());
                hikariConfig.setMaximumPoolSize(5);
            }
            case "mysql", "mariadb" -> {
                hikariConfig.setJdbcUrl("jdbc:mysql://" + config.getHost() + ":" + 
                    config.getPort() + "/" + config.getDatabase());
                hikariConfig.setUsername(config.getUsername());
                hikariConfig.setPassword(config.getPassword());
                hikariConfig.setMaximumPoolSize(20);
            }
            default -> throw new IllegalArgumentException("Unknown database type: " + config.getDatabaseType());
        }
        
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setConnectionTimeout(10000);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setAutoCommit(true);
        
        dataSource = new HikariDataSource(hikariConfig);
    }

    /**
     * Get a connection from the pool asynchronously.
     */
    public CompletableFuture<Connection> getConnection() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to get database connection", e);
            }
        });
    }

    /**
     * Shutdown the connection pool.
     */
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    /**
     * Check if connected and healthy.
     */
    public boolean isHealthy() {
        try (Connection conn = dataSource.getConnection()) {
            return !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
