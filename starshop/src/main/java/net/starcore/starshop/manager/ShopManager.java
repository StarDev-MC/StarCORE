package net.starcore.starshop.manager;

import net.starcore.api.EconomyService;
import net.starcore.starshop.defaults.DefaultShopGenerator;
import net.starcore.starshop.gui.ShopGuiManager;
import net.starcore.starshop.model.Shop;
import org.bukkit.entity.Player;

public class ShopManager {
    private final Shop shop;
    private final ShopGuiManager guiManager;
    private final EconomyService economyService;

    public ShopManager(EconomyService economyService) {
        this.economyService = economyService;
        this.shop = DefaultShopGenerator.generateDefaultShop();
        this.guiManager = new ShopGuiManager(shop);
    }

    public void openShop(Player player) {
        guiManager.openCategoryMenu(player);
    }

    public Shop getShop() {
        return shop;
    }

    public ShopGuiManager getGuiManager() {
        return guiManager;
    }

    public EconomyService getEconomyService() {
        return economyService;
    }

    public void onPlayerQuit(Player player) {
        guiManager.onPlayerQuit(player);
    }
}
