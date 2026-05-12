package net.starcore.common.cooldown;

import net.starcore.api.CooldownService;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager implements CooldownService {
    private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    @Override
    public void setCooldown(UUID actor, String key, Duration duration) {
        cooldowns.computeIfAbsent(actor, it -> new ConcurrentHashMap<>())
                .put(key.toLowerCase(), System.currentTimeMillis() + duration.toMillis());
    }

    @Override
    public Optional<Long> getRemaining(UUID actor, String key) {
        long expires = cooldowns.getOrDefault(actor, Map.of()).getOrDefault(key.toLowerCase(), 0L);
        long remaining = expires - System.currentTimeMillis();
        return remaining > 0 ? Optional.of(remaining) : Optional.empty();
    }

    @Override
    public boolean isOnCooldown(UUID actor, String key) {
        return getRemaining(actor, key).isPresent();
    }

    @Override
    public void clear(UUID actor, String key) {
        Map<String, Long> actorCooldowns = cooldowns.get(actor);
        if (actorCooldowns != null) {
            actorCooldowns.remove(key.toLowerCase());
        }
    }
}
