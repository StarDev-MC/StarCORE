package net.starcore.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class GuavaCacheStore implements CacheStore {
    private final Cache<String, Object> cache;

    public GuavaCacheStore() {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public <V> void put(String key, V value, Duration ttl) {
        Cache<String, Object> bucket = CacheBuilder.newBuilder()
                .expireAfterWrite(ttl.toMillis(), TimeUnit.MILLISECONDS)
                .build();
        bucket.put(key, value);
        cache.put(key, bucket);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Optional<V> get(String key, Class<V> type) {
        Object raw = cache.getIfPresent(key);
        if (raw == null) {
            return Optional.empty();
        }
        if (type.isInstance(raw)) {
            return Optional.of((V) raw);
        }
        return Optional.empty();
    }

    @Override
    public void invalidate(String key) {
        cache.invalidate(key);
    }
}
