package net.starcore.api;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface CooldownService {
    void setCooldown(UUID actor, String key, Duration duration);

    Optional<Long> getRemaining(UUID actor, String key);

    boolean isOnCooldown(UUID actor, String key);

    void clear(UUID actor, String key);
}
