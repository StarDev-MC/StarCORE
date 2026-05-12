package net.starcore.api;

import java.util.UUID;

public record PlayerData(UUID uniqueId, String username, long lastSeen, long walletBalance) {
}
