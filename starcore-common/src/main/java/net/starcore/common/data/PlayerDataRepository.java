package net.starcore.common.data;

import net.starcore.api.PlayerData;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerDataRepository {
    CompletableFuture<Optional<PlayerData>> findByUuid(UUID uniqueId);

    CompletableFuture<Void> save(PlayerData data);
}
