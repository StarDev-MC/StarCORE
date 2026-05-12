package net.starcore.common;

import net.starcore.api.ServiceRegistry;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServiceRegistry implements ServiceRegistry {
    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    @Override
    public <T> void register(Class<T> serviceType, T implementation) {
        services.put(serviceType, implementation);
    }

    @Override
    public <T> Optional<T> get(Class<T> serviceType) {
        return Optional.ofNullable(serviceType.cast(services.get(serviceType)));
    }

    @Override
    public void unregister(Class<?> serviceType) {
        services.remove(serviceType);
    }
}
