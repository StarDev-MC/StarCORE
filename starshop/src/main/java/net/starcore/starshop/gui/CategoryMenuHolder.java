package net.starcore.starshop.gui;

import net.starcore.starshop.model.Shop;
import net.starcore.starshop.model.ShopCategory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class CategoryMenuHolder implements InventoryHolder {
    private final Shop shop;
    private final Inventory inventory;

    public CategoryMenuHolder(Shop shop) {
        this.shop = shop;
        this.inventory = createCategoryMenu();
    }

    private Inventory createCategoryMenu() {
        int categoryCount = shop.getTotalCategories();
        int rows = (categoryCount + 8) / 9;
        int size = Math.max(27, rows * 9);
        Inventory inv = Bukkit.createInventory(this, size, GuiUtils.colorize("&6StarMC Shop &8| &eCategories"));
        
        ItemStack filler = GuiUtils.createFillerItem();
        GuiUtils.fillBorder(inv, filler);
        
        int slot = 10;
        for (ShopCategory category : shop.getCategories().values()) {
            if (slot % 9 == 0 || slot % 9 == 8) {
                slot++;
            }
            ItemStack item = GuiUtils.createItem(
                getMaterialFor(category.getIcon()),
                category.getDisplayName()
            );
            inv.setItem(slot, item);
            slot++;
        }
        
        return inv;
    }

    private org.bukkit.Material getMaterialFor(String name) {
        try {
            return org.bukkit.Material.valueOf(name);
        } catch (IllegalArgumentException ex) {
            return org.bukkit.Material.CHEST;
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Shop getShop() {
        return shop;
    }

    public ShopCategory getCategoryFromSlot(int slot) {
        int categoryIndex = 0;
        int currentSlot = 0;
        for (ShopCategory category : shop.getCategories().values()) {
            if (slot == currentSlot) {
                return category;
            }
            currentSlot++;
        }
        return null;
    }
}
