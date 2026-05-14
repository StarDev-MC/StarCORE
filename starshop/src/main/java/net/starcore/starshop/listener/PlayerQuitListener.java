package net.starcore.starshop.listener;

import net.starcore.starshop.manager.ShopManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final ShopManager shopManager;

    public PlayerQuitListener(ShopManager shopManager) {
        this.shopManager = shopManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        shopManager.onPlayerQuit(event.getPlayer());
    }
}
