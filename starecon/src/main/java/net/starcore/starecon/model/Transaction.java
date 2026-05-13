package net.starcore.starecon.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable transaction record for audit logging and history tracking.
 */
public record Transaction(
    UUID transactionId,
    UUID senderId,
    UUID receiverId,
    double amount,
    TransactionType type,
    String reason,
    Instant timestamp,
    boolean success
) {
    public Transaction {
        if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative");
        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new IllegalArgumentException("Invalid amount value");
        }
    }
}
