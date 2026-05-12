package net.starcore.api;

import java.sql.Connection;
import java.util.concurrent.CompletableFuture;

public interface DatabaseService extends AutoCloseable {
    CompletableFuture<Connection> getConnection();

    CompletableFuture<Void> executeAsync(SqlConsumer<Connection> consumer);

    <T> CompletableFuture<T> supplyAsync(SqlFunction<Connection, T> function);

    CompletableFuture<Void> executeUpdateAsync(String sql, Object... params);

    @FunctionalInterface
    interface SqlConsumer<T> {
        void accept(T t) throws Exception;
    }

    @FunctionalInterface
    interface SqlFunction<T, R> {
        R apply(T t) throws Exception;
    }
}
