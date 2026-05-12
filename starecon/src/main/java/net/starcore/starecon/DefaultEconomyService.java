package net.starcore.starecon;

import net.starcore.api.EconomyService;
import net.starcore.api.MessageProvider;
import net.starcore.api.PlayerData;
import net.starcore.api.PlayerDataService;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DefaultEconomyService implements EconomyService {
    private final PlayerDataService playerDataService;
    private final MessageProvider messageProvider;

    public DefaultEconomyService(PlayerDataService playerDataService, MessageProvider messageProvider) {
        this.playerDataService = playerDataService;
        this.messageProvider = messageProvider;
    }

    @Override
    public CompletableFuture<Long> getBalance(UUID playerId) {
        return playerDataService.load(playerId, playerId.toString())
                .thenApply(PlayerData::walletBalance);
    }

    @Override
    public CompletableFuture<Boolean> deposit(UUID playerId, long amount) {
        if (amount <= 0) {
            return CompletableFuture.completedFuture(false);
        }
        return playerDataService.load(playerId, playerId.toString()).thenCompose(data -> {
            long next = data.walletBalance() + amount;
            PlayerData updated = new PlayerData(data.uniqueId(), data.username(), System.currentTimeMillis(), next);
            return playerDataService.save(updated).thenApply(unused -> true);
        });
    }

    @Override
    public CompletableFuture<Boolean> withdraw(UUID playerId, long amount) {
        if (amount <= 0) {
            return CompletableFuture.completedFuture(false);
        }
        return playerDataService.load(playerId, playerId.toString()).thenCompose(data -> {
            if (data.walletBalance() < amount) {
                return CompletableFuture.completedFuture(false);
            }
            PlayerData updated = new PlayerData(data.uniqueId(), data.username(), System.currentTimeMillis(), data.walletBalance() - amount);
            return playerDataService.save(updated).thenApply(unused -> true);
        });
    }

    @Override
    public CompletableFuture<Boolean> transfer(UUID from, UUID to, long amount) {
        if (from.equals(to) || amount <= 0) {
            return CompletableFuture.completedFuture(false);
        }
        return withdraw(from, amount).thenCompose(success -> {
            if (!success) {
                return CompletableFuture.completedFuture(false);
            }
            return deposit(to, amount);
        });
    }

    @Override
    public String format(long amount) {
        if (amount < 1_000) {
            return "$" + amount;
        }
        if (amount < 1_000_000) {
            return String.format("$%.1fK", amount / 1_000.0);
        }
        if (amount < 1_000_000_000) {
            return String.format("$%.1fM", amount / 1_000_000.0);
        }
        return String.format("$%.1fB", amount / 1_000_000_000.0);
    }
}
