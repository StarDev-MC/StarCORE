package net.starcore.starshop.gui;

import net.starcore.starshop.model.Shop;
import net.starcore.starshop.model.ShopCategory;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShopGuiManager {
    private final Shop shop;
    private final Map<String, Integer> playerPages = new ConcurrentHashMap<>();
    private final Map<String, ShopCategory> playerCategories = new ConcurrentHashMap<>();

    public ShopGuiManager(Shop shop) {
        this.shop = shop;
    }

    public void openCategoryMenu(Player player) {
        CategoryMenuHolder holder = new CategoryMenuHolder(shop);
        player.openInventory(holder.getInventory());
    }

    public void openItemShop(Player player, ShopCategory category) {
        int page = playerPages.getOrDefault(player.getUniqueId().toString(), 0);
        playerCategories.put(player.getUniqueId().toString(), category);
        ItemShopMenuHolder holder = new ItemShopMenuHolder(shop, category, page);
        player.openInventory(holder.getInventory());
    }

    public void openItemShopPage(Player player, ShopCategory category, int page) {
        playerPages.put(player.getUniqueId().toString(), page);
        playerCategories.put(player.getUniqueId().toString(), category);
        ItemShopMenuHolder holder = new ItemShopMenuHolder(shop, category, page);
        player.openInventory(holder.getInventory());
    }

    public void onPlayerQuit(Player player) {
        String uuid = player.getUniqueId().toString();
        playerPages.remove(uuid);
        playerCategories.remove(uuid);
    }

    public ShopCategory getPlayerCategory(Player player) {
        return playerCategories.get(player.getUniqueId().toString());
    }

    public int getPlayerPage(Player player) {
        return playerPages.getOrDefault(player.getUniqueId().toString(), 0);
    }
}
