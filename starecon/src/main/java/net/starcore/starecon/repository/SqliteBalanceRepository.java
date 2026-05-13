package net.starcore.starecon.repository;

import net.starcore.starecon.model.AccountBalance;
import net.starcore.starecon.database.DatabaseConnection;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * SQLite implementation of BalanceRepository.
 */
public class SqliteBalanceRepository implements BalanceRepository {
    private final DatabaseConnection database;
    private static final String TABLE = "starecon_balances";

    public SqliteBalanceRepository(DatabaseConnection database) {
        this.database = database;
    }

    /**
     * Initialize the balances table.
     */
    public CompletableFuture<Void> createTable() {
        return database.getConnection().thenAcceptAsync(conn -> {
            try (Statement stmt = conn.createStatement()) {
                String createTable = String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        player_id TEXT PRIMARY KEY,
                        balance REAL NOT NULL,
                        last_modified INTEGER NOT NULL,
                        transaction_count INTEGER NOT NULL
                    );
                """, TABLE);
                stmt.executeUpdate(createTable);
                
                // Create index for faster lookups
                String createIndex = String.format("""
                    CREATE INDEX IF NOT EXISTS idx_%s_balance 
                    ON %s(balance DESC);
                """, TABLE, TABLE);
                stmt.executeUpdate(createIndex);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to create balances table", e);
            } finally {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        });
    }

    @Override
    public CompletableFuture<AccountBalance> getOrCreate(UUID playerId, double startingBalance) {
        return database.getConnection().thenApplyAsync(conn -> {
            try {
                String selectSQL = "SELECT * FROM " + TABLE + " WHERE player_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(selectSQL)) {
                    stmt.setString(1, playerId.toString());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return mapResultSet(rs);
                        }
                    }
                }
                
                // Insert new account
                String insertSQL = "INSERT INTO " + TABLE + " VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                    stmt.setString(1, playerId.toString());
                    stmt.setDouble(2, startingBalance);
                    stmt.setLong(3, System.currentTimeMillis());
                    stmt.setLong(4, 0);
                    stmt.executeUpdate();
                }
                
                return new AccountBalance(playerId, startingBalance, Instant.now(), 0);
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            } finally {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        });
    }

    @Override
    public CompletableFuture<AccountBalance> get(UUID playerId) {
        return database.getConnection().thenApplyAsync(conn -> {
            try {
                String sql = "SELECT * FROM " + TABLE + " WHERE player_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, playerId.toString());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return mapResultSet(rs);
                        }
                    }
                }
                return null;
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            } finally {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> update(AccountBalance balance) {
        return database.getConnection().thenApplyAsync(conn -> {
            try {
                String sql = "UPDATE " + TABLE + " SET balance = ?, last_modified = ?, transaction_count = ? " +
                            "WHERE player_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setDouble(1, balance.balance());
                    stmt.setLong(2, System.currentTimeMillis());
                    stmt.setLong(3, balance.transactionCount());
                    stmt.setString(4, balance.playerId().toString());
                    return stmt.executeUpdate() > 0;
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            } finally {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        });
    }

    @Override
    public CompletableFuture<List<AccountBalance>> getTopBalances(int limit) {
        return database.getConnection().thenApplyAsync(conn -> {
            List<AccountBalance> topBalances = new ArrayList<>();
            try {
                String sql = "SELECT * FROM " + TABLE + " ORDER BY balance DESC LIMIT ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, limit);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            topBalances.add(mapResultSet(rs));
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            } finally {
                try { conn.close(); } catch (SQLException ignored) {}
            }
            return topBalances;
        });
    }

    @Override
    public CompletableFuture<Integer> getRank(UUID playerId) {
        return database.getConnection().thenApplyAsync(conn -> {
            try {
                String sql = "SELECT COUNT(*) as rank FROM " + TABLE + 
                            " WHERE balance > (SELECT balance FROM " + TABLE + " WHERE player_id = ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, playerId.toString());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return rs.getInt("rank") + 1; // +1 because rank starts at 1
                        }
                    }
                }
                return -1; // Not found
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            } finally {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> delete(UUID playerId) {
        return database.getConnection().thenApplyAsync(conn -> {
            try {
                String sql = "DELETE FROM " + TABLE + " WHERE player_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, playerId.toString());
                    return stmt.executeUpdate() > 0;
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            } finally {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> exists(UUID playerId) {
        return database.getConnection().thenApplyAsync(conn -> {
            try {
                String sql = "SELECT 1 FROM " + TABLE + " WHERE player_id = ? LIMIT 1";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, playerId.toString());
                    try (ResultSet rs = stmt.executeQuery()) {
                        return rs.next();
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            } finally {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        });
    }

    @Override
    public CompletableFuture<Integer> getAccountCount() {
        return database.getConnection().thenApplyAsync(conn -> {
            try {
                String sql = "SELECT COUNT(*) as count FROM " + TABLE;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    if (rs.next()) {
                        return rs.getInt("count");
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            } finally {
                try { conn.close(); } catch (SQLException ignored) {}
            }
            return 0;
        });
    }

    private AccountBalance mapResultSet(ResultSet rs) throws SQLException {
        return new AccountBalance(
            UUID.fromString(rs.getString("player_id")),
            rs.getDouble("balance"),
            Instant.ofEpochMilli(rs.getLong("last_modified")),
            rs.getLong("transaction_count")
        );
    }
}
