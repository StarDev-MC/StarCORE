package net.starcore.starbank.manager;

import net.starcore.api.BankService;
import net.starcore.api.BankTransaction;
import net.starcore.api.DatabaseService;
import net.starcore.api.EconomyService;
import net.starcore.starbank.config.BankConfig;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BankAccountManager implements BankService {
    private final DatabaseService databaseService;
    private final EconomyService economyService;
    private final BankConfig config;

    public BankAccountManager(DatabaseService databaseService, EconomyService economyService, BankConfig config) {
        this.databaseService = databaseService;
        this.economyService = economyService;
        this.config = config;
        initializeTables();
    }

    private void initializeTables() {
        databaseService.executeAsync(connection -> {
            String sql = "CREATE TABLE IF NOT EXISTS bank_account (uuid TEXT PRIMARY KEY, balance INTEGER DEFAULT 0)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
            }
        });
        databaseService.executeAsync(connection -> {
            String sql = "CREATE TABLE IF NOT EXISTS bank_transaction (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "uuid TEXT NOT NULL, " +
                    "type TEXT NOT NULL, " +
                    "amount INTEGER NOT NULL, " +
                    "source TEXT, " +
                    "timestamp INTEGER NOT NULL, " +
                    "reason TEXT)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
            }
        });
    }

    public CompletableFuture<Long> getBalance(UUID playerId) {
        return databaseService.supplyAsync(connection -> {
            String sql = "SELECT balance FROM bank_account WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerId.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getLong("balance");
                    }
                }
            }
            return 0L;
        });
    }

    @Override
    public CompletableFuture<Boolean> deposit(UUID playerId, long amount, String reason) {
        if (amount <= 0 || amount > config.getTransactionLimit()) {
            return CompletableFuture.completedFuture(false);
        }
        return economyService.withdraw(playerId, amount).thenCompose(success -> {
            if (!success) {
                return CompletableFuture.completedFuture(false);
            }
            return getBalance(playerId).thenCompose(balance -> {
                if (balance + amount > config.getMaxBalance()) {
                    // Refund
                    economyService.deposit(playerId, amount);
                    return CompletableFuture.completedFuture(false);
                }
                return changeBalance(playerId, amount).thenCompose(success2 -> {
                    if (success2) {
                        return logTransaction(playerId, BankTransaction.TransactionType.DEPOSIT, amount, "wallet", reason).thenApply(unused -> true);
                    }
                    // Rollback
                    economyService.deposit(playerId, amount);
                    return CompletableFuture.completedFuture(false);
                });
            });
        });
    }

    @Override
    public CompletableFuture<Boolean> withdraw(UUID playerId, long amount, String reason) {
        if (amount <= 0 || amount > config.getTransactionLimit()) {
            return CompletableFuture.completedFuture(false);
        }
        return getBalance(playerId).thenCompose(balance -> {
            if (balance < amount) {
                return CompletableFuture.completedFuture(false);
            }
            return changeBalance(playerId, -amount).thenCompose(success -> {
                if (!success) {
                    return CompletableFuture.completedFuture(false);
                }
                return economyService.deposit(playerId, amount).thenCompose(success2 -> {
                    if (success2) {
                        return logTransaction(playerId, BankTransaction.TransactionType.WITHDRAW, amount, "wallet", reason).thenApply(unused -> true);
                    }
                    // Rollback
                    changeBalance(playerId, amount);
                    return CompletableFuture.completedFuture(false);
                });
            });
        });
    }

    @Override
    public CompletableFuture<Boolean> transfer(UUID playerId, long amount, String reason) {
        // For now, transfer is not implemented, perhaps for future cross-server
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<List<BankTransaction>> getTransactions(UUID playerId, int limit, int offset) {
        return databaseService.supplyAsync(connection -> {
            String sql = "SELECT type, amount, source, timestamp, reason FROM bank_transaction WHERE uuid = ? ORDER BY timestamp DESC LIMIT ? OFFSET ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerId.toString());
                statement.setInt(2, limit);
                statement.setInt(3, offset);
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<BankTransaction> transactions = new ArrayList<>();
                    while (resultSet.next()) {
                        BankTransaction.TransactionType type = BankTransaction.TransactionType.valueOf(resultSet.getString("type"));
                        long amount = resultSet.getLong("amount");
                        String source = resultSet.getString("source");
                        Instant timestamp = Instant.ofEpochMilli(resultSet.getLong("timestamp"));
                        String reason = resultSet.getString("reason");
                        transactions.add(new BankTransaction(playerId, type, amount, source, timestamp, reason));
                    }
                    return transactions;
                }
            }
        });
    }

    @Override
    public boolean hasAccount(UUID playerId) {
        // For simplicity, assume all players have accounts, created on first use
        return true;
    }

    @Override
    public CompletableFuture<Void> createAccount(UUID playerId) {
        return databaseService.executeAsync(connection -> {
            String sql = "INSERT OR IGNORE INTO bank_account (uuid, balance) VALUES (?, 0)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerId.toString());
                statement.executeUpdate();
            }
        });
    }

    private CompletableFuture<Void> logTransaction(UUID playerId, BankTransaction.TransactionType type, long amount, String source, String reason) {
        return databaseService.executeAsync(connection -> {
            String sql = "INSERT INTO bank_transaction (uuid, type, amount, source, timestamp, reason) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerId.toString());
                statement.setString(2, type.name());
                statement.setLong(3, amount);
                statement.setString(4, source);
                statement.setLong(5, Instant.now().toEpochMilli());
                statement.setString(6, reason);
                statement.executeUpdate();
            }
        });
    }

    private CompletableFuture<Boolean> changeBalance(UUID playerId, long delta) {
        return databaseService.executeAsync(connection -> {
            // First ensure account exists
            String insertSql = "INSERT OR IGNORE INTO bank_account (uuid, balance) VALUES (?, 0)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                insertStmt.setString(1, playerId.toString());
                insertStmt.executeUpdate();
            }
            // Then update balance
            String updateSql = "UPDATE bank_account SET balance = balance + ? WHERE uuid = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                updateStmt.setLong(1, delta);
                updateStmt.setString(2, playerId.toString());
                updateStmt.executeUpdate();
            }
        }).thenApply(unused -> true);
    }
}
