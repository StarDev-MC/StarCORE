package net.starcore.starecon.repository;

import net.starcore.starecon.model.Transaction;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.List;

/**
 * Repository for transaction history and audit logs.
 */
public interface TransactionRepository {
    
    /**
     * Record a new transaction.
     */
    CompletableFuture<Boolean> save(Transaction transaction);
    
    /**
     * Get transaction history for a player.
     */
    CompletableFuture<List<Transaction>> getHistory(UUID playerId, int limit);
    
    /**
     * Get transactions sent by a player.
     */
    CompletableFuture<List<Transaction>> getSent(UUID playerId, int limit);
    
    /**
     * Get transactions received by a player.
     */
    CompletableFuture<List<Transaction>> getReceived(UUID playerId, int limit);
    
    /**
     * Get total transactions in system.
     */
    CompletableFuture<Long> getTransactionCount();
    
    /**
     * Clear old transactions (for cleanup).
     */
    CompletableFuture<Integer> deleteOlderThan(long timestampMs);
}
