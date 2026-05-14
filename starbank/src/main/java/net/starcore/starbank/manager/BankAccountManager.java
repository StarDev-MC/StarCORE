package net.starcore.starbank.manager;

import net.starcore.api.BankService;
import net.starcore.api.DatabaseService;
import net.starcore.api.EconomyService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BankAccountManager implements BankService {
    private final DatabaseService databaseService;
    private final EconomyService economyService;

    public BankAccountManager(DatabaseService databaseService, EconomyService economyService) {
        this.databaseService = databaseService;
        this.economyService = economyService;
        initializeTables();
    }

    private void initializeTables() {
        databaseService.executeAsync(connection -> {
            String sql = "CREATE TABLE IF NOT EXISTS bank_account (uuid TEXT PRIMARY KEY, balance INTEGER)";
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

    public CompletableFuture<Boolean> deposit(UUID playerId, long amount) {
        if (amount <= 0) {
            return CompletableFuture.completedFuture(false);
        }
        return economyService.withdraw(playerId, amount).thenCompose(success -> {
            if (!success) {
                return CompletableFuture.completedFuture(false);
            }
            return changeBalance(playerId, amount);
        });
    }

    public CompletableFuture<Boolean> withdraw(UUID playerId, long amount) {
        if (amount <= 0) {
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
                return economyService.deposit(playerId, amount);
            });
        });
    }

    private CompletableFuture<Boolean> changeBalance(UUID playerId, long delta) {
        return getBalance(playerId).thenCompose(balance -> {
            long next = balance + delta;
            return databaseService.executeAsync(connection -> {
                String sql = "INSERT INTO bank_account (uuid, balance) VALUES (?, ?) "
                        + "ON CONFLICT(uuid) DO UPDATE SET balance = excluded.balance";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, playerId.toString());
                    statement.setLong(2, next);
                    statement.executeUpdate();
                }
            }).thenApply(unused -> true);
        });
    }
}
