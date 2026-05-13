package net.starcore.starecon.repository;

import net.starcore.starecon.model.AccountBalance;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.List;

/**
 * Repository pattern for account balance operations.
 * All operations are asynchronous and thread-safe.
 */
public interface BalanceRepository {
    
    /**
     * Get a player's balance, or create with starting balance if not exists.
     */
    CompletableFuture<AccountBalance> getOrCreate(UUID playerId, double startingBalance);
    
    /**
     * Get a player's balance.
     */
    CompletableFuture<AccountBalance> get(UUID playerId);
    
    /**
     * Update a player's balance atomically.
     */
    CompletableFuture<Boolean> update(AccountBalance balance);
    
    /**
     * Get top N balances by amount.
     */
    CompletableFuture<List<AccountBalance>> getTopBalances(int limit);
    
    /**
     * Get a player's rank by balance amount.
     */
    CompletableFuture<Integer> getRank(UUID playerId);
    
    /**
     * Delete a player account.
     */
    CompletableFuture<Boolean> delete(UUID playerId);
    
    /**
     * Check if player account exists.
     */
    CompletableFuture<Boolean> exists(UUID playerId);
    
    /**
     * Get total accounts in database.
     */
    CompletableFuture<Integer> getAccountCount();
}
