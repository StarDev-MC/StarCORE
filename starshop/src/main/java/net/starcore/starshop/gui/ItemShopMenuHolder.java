package net.starcore.starshop.gui;

import net.starcore.starshop.model.Shop;
import net.starcore.starshop.model.ShopCategory;
import net.starcore.starshop.model.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemShopMenuHolder implements InventoryHolder {
    private static final int ITEMS_PER_PAGE = 28;
    private static final int NAVIGATION_SLOTS = 9;

    private final Shop shop;
    private final ShopCategory category;
    private final int page;
    private final Inventory inventory;

    public ItemShopMenuHolder(Shop shop, ShopCategory category, int page) {
        this.shop = shop;
        this.category = category;
        this.page = Math.max(0, page);
        this.inventory = createItemMenu();
    }

    private Inventory createItemMenu() {
        int totalPages = category.getTotalPages();
        String title = GuiUtils.colorize("&6" + category.getDisplayName() + " &8| Page &e" + (page + 1));
        Inventory inv = Bukkit.createInventory(this, 36, title);
        
        ItemStack filler = GuiUtils.createFillerItem();
        for (int i = 28; i < 36; i++) {
            inv.setItem(i, new ItemStack(filler));
        }
        
        List<ShopItem> items = category.getPage(page);
        int slot = 0;
        for (ShopItem item : items) {
            if (slot >= ITEMS_PER_PAGE) break;
            inv.setItem(slot, createShopItemDisplay(item));
            slot++;
        }
        
        inv.setItem(28, GuiUtils.createBackItem());
        if (page > 0) {
            inv.setItem(29, GuiUtils.createPrevPageItem(page));
        }
        if (page < totalPages - 1) {
            inv.setItem(34, GuiUtils.createNextPageItem(page, totalPages));
        }
        
        return inv;
    }

    private ItemStack createShopItemDisplay(ShopItem shopItem) {
        org.bukkit.Material material;
        try {
            material = org.bukkit.Material.valueOf(shopItem.getMaterial());
        } catch (IllegalArgumentException ex) {
            material = org.bukkit.Material.BARRIER;
        }
        
        ItemStack display = new ItemStack(material);
        ItemMeta meta = display.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(GuiUtils.colorize("&f" + formatMaterialName(shopItem.getMaterial())));
            List<String> lore = new ArrayList<>();
            if (shopItem.isBuyable()) {
                lore.add(GuiUtils.colorize("&aLeft Click: Buy for &6$" + shopItem.getBuyPrice()));
            }
            if (shopItem.isSellable()) {
                lore.add(GuiUtils.colorize("&cRight Click: Sell for &6$" + shopItem.getSellPrice()));
            }
            if (shopItem.isBuyable() || shopItem.isSellable()) {
                lore.add(GuiUtils.colorize("&fShift Click: Bulk buy/sell"));
            }
            meta.setLore(lore);
            display.setItemMeta(meta);
        }
        display.setAmount(1);
        return display;
    }

    private String formatMaterialName(String material) {
        return material.replace('_', ' ').toLowerCase();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Shop getShop() {
        return shop;
    }

    public ShopCategory getCategory() {
        return category;
    }

    public int getPage() {
        return page;
    }

    public ShopItem getShopItemFromSlot(int slot) {
        if (slot >= ITEMS_PER_PAGE) return null;
        List<ShopItem> pageItems = category.getPage(page);
        if (slot < pageItems.size()) {
            return pageItems.get(slot);
        }
        return null;
    }

    public int getTotalPages() {
        return category.getTotalPages();
    }
}
