package net.starcore.starecon.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a player's account balance with metadata.
 */
public record AccountBalance(
    UUID playerId,
    double balance,
    Instant lastModified,
    long transactionCount
) {
    public AccountBalance {
        if (balance < 0) throw new IllegalArgumentException("Balance cannot be negative");
        if (Double.isNaN(balance) || Double.isInfinite(balance)) {
            throw new IllegalArgumentException("Invalid balance value");
        }
    }

    /**
     * Creates a new balance with updated value.
     */
    public AccountBalance withBalance(double newBalance) {
        return new AccountBalance(playerId, newBalance, Instant.now(), transactionCount);
    }

    /**
     * Creates a new balance with incremented transaction count.
     */
    public AccountBalance withTransaction() {
        return new AccountBalance(playerId, balance, Instant.now(), transactionCount + 1);
    }
}
