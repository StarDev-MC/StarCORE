package net.starcore.starecon.repository;

import net.starcore.starecon.model.Transaction;
import net.starcore.starecon.model.TransactionType;
import net.starcore.starecon.database.DatabaseConnection;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * SQLite implementation of TransactionRepository.
 */
public class SqliteTransactionRepository implements TransactionRepository {
    private final DatabaseConnection database;
    private static final String TABLE = "starecon_transactions";

    public SqliteTransactionRepository(DatabaseConnection database) {
        this.database = database;
    }

    public CompletableFuture<Void> createTable() {
        return database.getConnection().thenAcceptAsync(conn -> {
            try (Statement stmt = conn.createStatement()) {
                String createTable = String.format("""
                    CREATE TABLE IF NOT EXISTS %s (
                        id TEXT PRIMARY KEY,
                        sender_id TEXT,
                        receiver_id TEXT,
                        amount REAL NOT NULL,
                        type TEXT NOT NULL,
                        reason TEXT,
                        timestamp INTEGER NOT NULL,
                        success INTEGER NOT NULL
                    );
                """, TABLE);
                stmt.executeUpdate(createTable);
                
                String createIndex = String.format("""
                    CREATE INDEX IF NOT EXISTS idx_%s_timestamp 
                    ON %s(timestamp DESC);
                """, TABLE, TABLE);
                stmt.executeUpdate(createIndex);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to create transactions table", e);
            } finally {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> save(Transaction transaction) {
        return database.getConnection().thenApplyAsync(conn -> {
            try {
                String sql = String.format("""
                    INSERT INTO %s (id, sender_id, receiver_id, amount, type, reason, timestamp, success)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """, TABLE);
                
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, transaction.transactionId().toString());
                    stmt.setString(2, transaction.senderId() != null ? transaction.senderId().toString() : null);
                    stmt.setString(3, transaction.receiverId() != null ? transaction.receiverId().toString() : null);
                    stmt.setDouble(4, transaction.amount());
                    stmt.setString(5, transaction.type().name());
                    stmt.setString(6, transaction.reason());
                    stmt.setLong(7, transaction.timestamp().toEpochMilli());
                    stmt.setInt(8, transaction.success() ? 1 : 0);
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
    public CompletableFuture<List<Transaction>> getHistory(UUID playerId, int limit) {
        return database.getConnection().thenApplyAsync(conn -> {
            List<Transaction> history = new ArrayList<>();
            try {
                String sql = String.format("""
                    SELECT * FROM %s 
                    WHERE sender_id = ? OR receiver_id = ?
                    ORDER BY timestamp DESC LIMIT ?;
                """, TABLE);
                
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    String playerIdStr = playerId.toString();
                    stmt.setString(1, playerIdStr);
                    stmt.setString(2, playerIdStr);
                    stmt.setInt(3, limit);
                    
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            history.add(mapResultSet(rs));
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            } finally {
                try { conn.close(); } catch (SQLException ignored) {}
            }
            return history;
        });
    }

    @Override
    public CompletableFuture<List<Transaction>> getSent(UUID playerId, int limit) {
        return database.getConnection().thenApplyAsync(conn -> {
            List<Transaction> sent = new ArrayList<>();
            try {
                String sql = String.format("""
                    SELECT * FROM %s WHERE sender_id = ? ORDER BY timestamp DESC LIMIT ?;
                """, TABLE);
                
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, playerId.toString());
                    stmt.setInt(2, limit);
                    
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            sent.add(mapResultSet(rs));
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            } finally {
                try { conn.close(); } catch (SQLException ignored) {}
            }
            return sent;
        });
    }

    @Override
    public CompletableFuture<List<Transaction>> getReceived(UUID playerId, int limit) {
        return database.getConnection().thenApplyAsync(conn -> {
            List<Transaction> received = new ArrayList<>();
            try {
                String sql = String.format("""
                    SELECT * FROM %s WHERE receiver_id = ? ORDER BY timestamp DESC LIMIT ?;
                """, TABLE);
                
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, playerId.toString());
                    stmt.setInt(2, limit);
                    
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            received.add(mapResultSet(rs));
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            } finally {
                try { conn.close(); } catch (SQLException ignored) {}
            }
            return received;
        });
    }

    @Override
    public CompletableFuture<Long> getTransactionCount() {
        return database.getConnection().thenApplyAsync(conn -> {
            try {
                String sql = "SELECT COUNT(*) as count FROM " + TABLE;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    if (rs.next()) {
                        return (long) rs.getInt("count");
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            } finally {
                try { conn.close(); } catch (SQLException ignored) {}
            }
            return 0L;
        });
    }

    @Override
    public CompletableFuture<Integer> deleteOlderThan(long timestampMs) {
        return database.getConnection().thenApplyAsync(conn -> {
            try {
                String sql = "DELETE FROM " + TABLE + " WHERE timestamp < ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setLong(1, timestampMs);
                    return stmt.executeUpdate();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            } finally {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        });
    }

    private Transaction mapResultSet(ResultSet rs) throws SQLException {
        String senderIdStr = rs.getString("sender_id");
        String receiverIdStr = rs.getString("receiver_id");
        
        return new Transaction(
            UUID.fromString(rs.getString("id")),
            senderIdStr != null ? UUID.fromString(senderIdStr) : null,
            receiverIdStr != null ? UUID.fromString(receiverIdStr) : null,
            rs.getDouble("amount"),
            TransactionType.valueOf(rs.getString("type")),
            rs.getString("reason"),
            Instant.ofEpochMilli(rs.getLong("timestamp")),
            rs.getInt("success") == 1
        );
    }
}
