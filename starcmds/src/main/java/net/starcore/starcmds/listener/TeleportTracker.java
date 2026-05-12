package net.starcore.starcmds.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportTracker implements Listener {
    private static final Map<UUID, org.bukkit.Location> lastLocations = new ConcurrentHashMap<>();

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        lastLocations.put(event.getPlayer().getUniqueId(), event.getFrom());
    }

    public static org.bukkit.Location getLastLocation(UUID playerId) {
        return lastLocations.get(playerId);
    }
}
