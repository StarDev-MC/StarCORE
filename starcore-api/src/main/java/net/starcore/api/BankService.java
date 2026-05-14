package net.starcore.api;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface BankService {
    CompletableFuture<Long> getBalance(UUID playerId);
}
