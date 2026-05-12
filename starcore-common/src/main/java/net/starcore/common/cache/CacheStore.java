package net.starcore.common.cache;

import java.time.Duration;
import java.util.Optional;

public interface CacheStore {
    <V> void put(String key, V value, Duration ttl);
    <V> Optional<V> get(String key, Class<V> type);
    void invalidate(String key);
}
