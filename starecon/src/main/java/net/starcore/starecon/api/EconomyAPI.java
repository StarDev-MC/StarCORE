package net.starcore.starecon.api;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import net.starcore.starecon.model.Transaction;

/**
 * Public API for economy operations. Other plugins should use this interface.
 * All operations are async and thread-safe.
 */
public interface EconomyAPI {
    
    /**
     * Get a player's balance asynchronously.
     */
    CompletableFuture<Double> getBalance(UUID playerId);
    
    /**
     * Check if a player has at least the specified amount.
     */
    CompletableFuture<Boolean> hasBalance(UUID playerId, double amount);
    
    /**
     * Deposit money to a player's account.
     */
    CompletableFuture<Boolean> deposit(UUID playerId, double amount, String reason);
    
    /**
     * Withdraw money from a player's account.
     */
    CompletableFuture<Boolean> withdraw(UUID playerId, double amount, String reason);
    
    /**
     * Set a player's balance directly (admin operation).
     */
    CompletableFuture<Boolean> setBalance(UUID playerId, double amount, String reason);
    
    /**
     * Transfer money between two players.
     */
    CompletableFuture<Boolean> transfer(UUID fromId, UUID toId, double amount, String reason);
    
    /**
     * Reset a player's balance to the starting amount.
     */
    CompletableFuture<Boolean> resetBalance(UUID playerId, String reason);
    
    /**
     * Get transaction history for a player.
     */
    CompletableFuture<List<Transaction>> getTransactionHistory(UUID playerId, int limit);
    
    /**
     * Get top N players by balance.
     */
    CompletableFuture<List<TopBalance>> getTopBalances(int limit);
    
    /**
     * Get a player's rank by balance.
     */
    CompletableFuture<Integer> getBalanceRank(UUID playerId);
    
    /**
     * Format a balance for display.
     */
    String formatBalance(double amount);
    
    /**
     * Record defines top balance entry with rank.
     */
    record TopBalance(int rank, UUID playerId, String playerName, double balance) {}
}
