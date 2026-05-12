package net.starcore.common.data;

import net.starcore.api.DatabaseService;
import net.starcore.api.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SqlPlayerDataRepository implements PlayerDataRepository {
    private final DatabaseService databaseService;

    public SqlPlayerDataRepository(DatabaseService databaseService) {
        this.databaseService = databaseService;
        initializeTables();
    }

    private void initializeTables() {
        databaseService.executeAsync(connection -> {
            String sql = "CREATE TABLE IF NOT EXISTS player_data (uuid TEXT PRIMARY KEY, username TEXT, last_seen INTEGER, wallet_balance INTEGER)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
            }
        });
    }

    @Override
    public CompletableFuture<Optional<PlayerData>> findByUuid(UUID uniqueId) {
        return databaseService.supplyAsync(connection -> {
            String sql = "SELECT username, last_seen, wallet_balance FROM player_data WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uniqueId.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(new PlayerData(
                                uniqueId,
                                resultSet.getString("username"),
                                resultSet.getLong("last_seen"),
                                resultSet.getLong("wallet_balance")
                        ));
                    }
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Void> save(PlayerData data) {
        return databaseService.executeAsync(connection -> {
            String sql = "INSERT INTO player_data (uuid, username, last_seen, wallet_balance) VALUES (?, ?, ?, ?)"
                    + " ON CONFLICT(uuid) DO UPDATE SET username = excluded.username, last_seen = excluded.last_seen, wallet_balance = excluded.wallet_balance";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, data.uniqueId().toString());
                statement.setString(2, data.username());
                statement.setLong(3, data.lastSeen());
                statement.setLong(4, data.walletBalance());
                statement.executeUpdate();
            }
        });
    }
}
