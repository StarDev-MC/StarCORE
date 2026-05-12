package net.starcore.common.data;

import net.starcore.api.PlayerData;
import net.starcore.api.PlayerDataService;
import net.starcore.api.DatabaseService;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager implements PlayerDataService {
    private final PlayerDataRepository repository;
    private final Map<UUID, PlayerData> cache = new ConcurrentHashMap<>();

    public PlayerDataManager(DatabaseService databaseService) {
        this.repository = new SqlPlayerDataRepository(databaseService);
    }

    @Override
    public CompletableFuture<PlayerData> load(UUID uniqueId, String username) {
        return repository.findByUuid(uniqueId).thenCompose(optional -> {
            if (optional.isPresent()) {
                PlayerData data = optional.get();
                cache.put(uniqueId, data);
                return CompletableFuture.completedFuture(data);
            }
            PlayerData generated = new PlayerData(uniqueId, username, Instant.now().toEpochMilli(), 0L);
            cache.put(uniqueId, generated);
            return repository.save(generated).thenApply(unused -> generated);
        });
    }

    @Override
    public CompletableFuture<Void> save(PlayerData playerData) {
        cache.put(playerData.uniqueId(), playerData);
        return repository.save(playerData);
    }

    @Override
    public Optional<PlayerData> getCached(UUID uniqueId) {
        return Optional.ofNullable(cache.get(uniqueId));
    }

    @Override
    public List<PlayerData> getCachedList() {
        return List.copyOf(cache.values());
    }
}
