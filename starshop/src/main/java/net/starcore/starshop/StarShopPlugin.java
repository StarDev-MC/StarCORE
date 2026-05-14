package net.starcore.starshop;

import net.starcore.api.EconomyService;
import net.starcore.plugin.AbstractStarCorePlugin;
import net.starcore.starshop.command.SellAllCommand;
import net.starcore.starshop.command.SellCommand;
import net.starcore.starshop.command.ShopCommand;
import net.starcore.starshop.listener.ShopClickListener;
import net.starcore.starshop.listener.PlayerQuitListener;
import net.starcore.starshop.manager.ShopManager;
import org.bukkit.event.Listener;

public final class StarShopPlugin extends AbstractStarCorePlugin {
    private ShopManager shopManager;

    @Override
    protected void enablePlugin() {
        EconomyService economyService = getService(EconomyService.class);
        if (economyService == null) {
            getLogger().warning("EconomyService not available. Shop disabled.");
            return;
        }
        shopManager = new ShopManager(economyService);
        registerCommand(new ShopCommand(this, shopManager));
        registerCommand(new SellCommand(this, economyService));
        registerCommand(new SellAllCommand(this, economyService));
        eventBus.register(new ShopClickListener(shopManager.getGuiManager(), shopManager.getShop(), economyService));
        eventBus.register(new PlayerQuitListener(shopManager));
    }
}
