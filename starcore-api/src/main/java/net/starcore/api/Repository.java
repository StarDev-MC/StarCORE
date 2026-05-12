package net.starcore.api;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Repository<T> {
    CompletableFuture<Optional<T>> find(Object key);
    CompletableFuture<Void> save(T item);
    CompletableFuture<Void> delete(Object key);
}
