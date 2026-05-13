package net.starcore.starecon.manager;

import net.starcore.starecon.api.EconomyAPI;
import net.starcore.starecon.database.DatabaseConnection;
import net.starcore.starecon.event.BalanceChangeEvent;
import net.starcore.starecon.event.PlayerPayEvent;
import net.starcore.starecon.event.EconomyTransactionEvent;
import net.starcore.starecon.model.AccountBalance;
import net.starcore.starecon.model.Transaction;
import net.starcore.starecon.model.TransactionType;
import net.starcore.starecon.repository.BalanceRepository;
import net.starcore.starecon.repository.TransactionRepository;
import net.starcore.starecon.util.CurrencyFormatter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Core economy manager implementing the EconomyAPI.
 * Thread-safe with async database operations.
 */
public class EconomyManager implements EconomyAPI {
    private final BalanceRepository balanceRepo;
    private final TransactionRepository transactionRepo;
    private final double startingBalance;
    private final double maxBalance;

    public EconomyManager(BalanceRepository balanceRepo, TransactionRepository transactionRepo, 
                         double startingBalance, double maxBalance) {
        this.balanceRepo = balanceRepo;
        this.transactionRepo = transactionRepo;
        this.startingBalance = startingBalance;
        this.maxBalance = maxBalance;
    }

    @Override
    public CompletableFuture<Double> getBalance(UUID playerId) {
        return balanceRepo.getOrCreate(playerId, startingBalance)
            .thenApply(AccountBalance::balance);
    }

    @Override
    public CompletableFuture<Boolean> hasBalance(UUID playerId, double amount) {
        return getBalance(playerId)
            .thenApply(balance -> balance >= amount);
    }

    @Override
    public CompletableFuture<Boolean> deposit(UUID playerId, double amount, String reason) {
        if (!isValidAmount(amount)) return CompletableFuture.completedFuture(false);

        return balanceRepo.getOrCreate(playerId, startingBalance)
            .thenCompose(current -> {
                double newBalance = Math.min(current.balance() + amount, maxBalance);
                
                // Fire change event
                BalanceChangeEvent event = new BalanceChangeEvent(playerId, current.balance(), newBalance, reason);
                Bukkit.getPluginManager().callEvent(event);
                newBalance = event.getNewBalance();
                
                // Update balance
                AccountBalance updated = current.withBalance(newBalance).withTransaction();
                return balanceRepo.update(updated)
                    .thenCompose(success -> {
                        if (success) {
                            Transaction tx = new Transaction(
                                UUID.randomUUID(), null, playerId, amount, 
                                TransactionType.DEPOSIT, reason, Instant.now(), true
                            );
                            return transactionRepo.save(tx)
                                .thenApply(txSuccess -> {
                                    if (txSuccess) {
                                        Bukkit.getPluginManager().callEvent(new EconomyTransactionEvent(tx));
                                    }
                                    return true;
                                });
                        }
                        return CompletableFuture.completedFuture(false);
                    });
            });
    }

    @Override
    public CompletableFuture<Boolean> withdraw(UUID playerId, double amount, String reason) {
        if (!isValidAmount(amount)) return CompletableFuture.completedFuture(false);

        return balanceRepo.getOrCreate(playerId, startingBalance)
            .thenCompose(current -> {
                if (current.balance() < amount) {
                    return CompletableFuture.completedFuture(false);
                }
                
                double newBalance = current.balance() - amount;
                
                // Fire change event
                BalanceChangeEvent event = new BalanceChangeEvent(playerId, current.balance(), newBalance, reason);
                Bukkit.getPluginManager().callEvent(event);
                newBalance = event.getNewBalance();
                
                // Update balance
                AccountBalance updated = current.withBalance(newBalance).withTransaction();
                return balanceRepo.update(updated)
                    .thenCompose(success -> {
                        if (success) {
                            Transaction tx = new Transaction(
                                UUID.randomUUID(), playerId, null, amount,
                                TransactionType.WITHDRAW, reason, Instant.now(), true
                            );
                            return transactionRepo.save(tx)
                                .thenApply(txSuccess -> true);
                        }
                        return CompletableFuture.completedFuture(false);
                    });
            });
    }

    @Override
    public CompletableFuture<Boolean> setBalance(UUID playerId, double amount, String reason) {
        if (!isValidAmount(amount)) return CompletableFuture.completedFuture(false);

        return balanceRepo.getOrCreate(playerId, startingBalance)
            .thenCompose(current -> {
                double newBalance = Math.min(amount, maxBalance);
                
                // Fire change event
                BalanceChangeEvent event = new BalanceChangeEvent(playerId, current.balance(), newBalance, reason);
                Bukkit.getPluginManager().callEvent(event);
                newBalance = event.getNewBalance();
                
                AccountBalance updated = current.withBalance(newBalance).withTransaction();
                return balanceRepo.update(updated)
                    .thenCompose(success -> {
                        if (success) {
                            Transaction tx = new Transaction(
                                UUID.randomUUID(), null, playerId, amount,
                                TransactionType.ADMIN_SET, reason, Instant.now(), true
                            );
                            transactionRepo.save(tx);
                        }
                        return CompletableFuture.completedFuture(success);
                    });
            });
    }

    @Override
    public CompletableFuture<Boolean> transfer(UUID fromId, UUID toId, double amount, String reason) {
        if (!isValidAmount(amount)) return CompletableFuture.completedFuture(false);
        if (fromId.equals(toId)) return CompletableFuture.completedFuture(false);

        return withdraw(fromId, amount, "Transfer to " + toId)
            .thenCompose(withdrawSuccess -> {
                if (!withdrawSuccess) return CompletableFuture.completedFuture(false);
                
                return deposit(toId, amount, "Transfer from " + fromId)
                    .thenCompose(depositSuccess -> {
                        if (depositSuccess) {
                            Bukkit.getPluginManager().callEvent(new PlayerPayEvent(fromId, toId, amount));
                        }
                        return CompletableFuture.completedFuture(depositSuccess);
                    });
            });
    }

    @Override
    public CompletableFuture<Boolean> resetBalance(UUID playerId, String reason) {
        return setBalance(playerId, startingBalance, "Reset balance: " + reason);
    }

    @Override
    public CompletableFuture<List<Transaction>> getTransactionHistory(UUID playerId, int limit) {
        return transactionRepo.getHistory(playerId, limit);
    }

    @Override
    public CompletableFuture<List<TopBalance>> getTopBalances(int limit) {
        return balanceRepo.getTopBalances(limit)
            .thenApply(balances -> {
                List<TopBalance> result = new ArrayList<>();
                int rank = 1;
                for (AccountBalance balance : balances) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(balance.playerId());
                    String name = player.getName() != null ? player.getName() : balance.playerId().toString();
                    result.add(new TopBalance(rank++, balance.playerId(), name, balance.balance()));
                }
                return result;
            });
    }

    @Override
    public CompletableFuture<Integer> getBalanceRank(UUID playerId) {
        return balanceRepo.getRank(playerId);
    }

    @Override
    public String formatBalance(double amount) {
        return CurrencyFormatter.format(amount);
    }

    private boolean isValidAmount(double amount) {
        return amount > 0 && !Double.isNaN(amount) && !Double.isInfinite(amount);
    }
}
