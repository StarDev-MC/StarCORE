package net.starcore.api;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface EconomyService {
    CompletableFuture<Long> getBalance(UUID playerId);
    CompletableFuture<Boolean> deposit(UUID playerId, long amount);
    CompletableFuture<Boolean> withdraw(UUID playerId, long amount);
    CompletableFuture<Boolean> transfer(UUID from, UUID to, long amount);
    String format(long amount);
}
