package net.starcore.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerDataService {
    CompletableFuture<PlayerData> load(UUID uniqueId, String username);

    CompletableFuture<Void> save(PlayerData playerData);

    Optional<PlayerData> getCached(UUID uniqueId);

    List<PlayerData> getCachedList();
}
