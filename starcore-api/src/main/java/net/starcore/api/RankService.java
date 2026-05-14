package net.starcore.api;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface RankService {
    CompletableFuture<String> getRank(UUID playerId);
}
