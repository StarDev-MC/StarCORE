package net.starcore.starshop;

import net.starcore.api.EconomyService;
import net.starcore.plugin.AbstractStarCorePlugin;
import net.starcore.starshop.command.SellAllCommand;
import net.starcore.starshop.command.SellCommand;
import net.starcore.starshop.command.ShopCommand;
import net.starcore.starshop.manager.ShopManager;

public final class StarShopPlugin extends AbstractStarCorePlugin {
    private ShopManager shopManager;

    @Override
    protected void enablePlugin() {
        EconomyService economyService = getService(EconomyService.class);
        shopManager = new ShopManager(this, economyService);
        registerCommand(new ShopCommand(this, shopManager));
        registerCommand(new SellCommand(this, economyService));
        registerCommand(new SellAllCommand(this, economyService));
    }
}
