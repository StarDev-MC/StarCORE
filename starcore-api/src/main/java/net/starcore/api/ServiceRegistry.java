package net.starcore.api;

import java.util.Optional;

public interface ServiceRegistry {
    <T> void register(Class<T> serviceType, T implementation);

    <T> Optional<T> get(Class<T> serviceType);

    void unregister(Class<?> serviceType);
}
