package net.starcore.starshop.listener;

import net.starcore.api.EconomyService;
import net.starcore.starshop.gui.CategoryMenuHolder;
import net.starcore.starshop.gui.ItemShopMenuHolder;
import net.starcore.starshop.gui.ShopGuiManager;
import net.starcore.starshop.model.Shop;
import net.starcore.starshop.model.ShopCategory;
import net.starcore.starshop.model.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class ShopClickListener implements Listener {
    private final ShopGuiManager guiManager;
    private final Shop shop;
    private final EconomyService economyService;

    public ShopClickListener(ShopGuiManager guiManager, Shop shop, EconomyService economyService) {
        this.guiManager = guiManager;
        this.shop = shop;
        this.economyService = economyService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        InventoryHolder holder = event.getClickedInventory() == null ? null : event.getClickedInventory().getHolder();
        
        if (holder instanceof CategoryMenuHolder categoryMenu) {
            event.setCancelled(true);
            handleCategoryMenuClick(player, categoryMenu, event.getSlot());
        } else if (holder instanceof ItemShopMenuHolder itemMenu) {
            event.setCancelled(true);
            handleItemShopMenuClick(player, itemMenu, event.getSlot(), event.isLeftClick(), event.isRightClick(), event.isShiftClick());
        }
    }

    private void handleCategoryMenuClick(Player player, CategoryMenuHolder menu, int slot) {
        if (slot < 0 || slot >= 36) return;
        if (slot == 0 || slot == 8 || slot % 9 == 0 || slot % 9 == 8) return;
        
        int categoryIndex = (slot - 10) / 7;
        ShopCategory category = null;
        int index = 0;
        for (ShopCategory cat : shop.getCategories().values()) {
            if (index == categoryIndex) {
                category = cat;
                break;
            }
            index++;
        }
        
        if (category != null) {
            guiManager.openItemShop(player, category);
        }
    }

    private void handleItemShopMenuClick(Player player, ItemShopMenuHolder menu, int slot, boolean leftClick, boolean rightClick, boolean shiftClick) {
        if (slot >= 28) {
            if (slot == 28) {
                guiManager.openCategoryMenu(player);
            } else if (slot == 29 && menu.getPage() > 0) {
                guiManager.openItemShopPage(player, menu.getCategory(), menu.getPage() - 1);
            } else if (slot == 34 && menu.getPage() < menu.getTotalPages() - 1) {
                guiManager.openItemShopPage(player, menu.getCategory(), menu.getPage() + 1);
            }
            return;
        }
        
        ShopItem shopItem = menu.getShopItemFromSlot(slot);
        if (shopItem == null) return;
        
        if (leftClick && shopItem.isBuyable()) {
            handleBuyTransaction(player, shopItem, shiftClick);
        } else if (rightClick && shopItem.isSellable()) {
            handleSellTransaction(player, shopItem, shiftClick);
        }
    }

    private void handleBuyTransaction(Player player, ShopItem shopItem, boolean bulk) {
        if (economyService == null) {
            player.sendMessage("§cEconomy service not available.");
            return;
        }
        
        int quantity = bulk ? 64 : 1;
        long totalCost = shopItem.getBuyPrice() * quantity;
        
        economyService.getBalance(player.getUniqueId()).thenCompose(balance -> {
            if (balance < totalCost) {
                player.sendMessage("§cInsufficient funds. Need §6$" + totalCost + "§c but you only have §6$" + balance);
                return economyService.withdraw(player.getUniqueId(), 0);
            }
            return economyService.withdraw(player.getUniqueId(), totalCost);
        }).thenAccept(success -> {
            if (success) {
                org.bukkit.Material material = org.bukkit.Material.matchMaterial(shopItem.getMaterial());
                if (material != null) {
                    org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(material, quantity);
                    player.getInventory().addItem(item);
                    player.sendMessage("§aPurchased §f" + quantity + "x " + shopItem.getMaterial() + "§a for §6$" + totalCost);
                }
            } else {
                player.sendMessage("§cTransaction failed.");
            }
        });
    }

    private void handleSellTransaction(Player player, ShopItem shopItem, boolean bulk) {
        if (economyService == null) {
            player.sendMessage("§cEconomy service not available.");
            return;
        }
        
        org.bukkit.Material material = org.bukkit.Material.matchMaterial(shopItem.getMaterial());
        if (material == null) {
            player.sendMessage("§cInvalid item material.");
            return;
        }
        
        int availableCount = 0;
        for (org.bukkit.inventory.ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                availableCount += item.getAmount();
            }
        }
        
        if (availableCount == 0) {
            player.sendMessage("§cYou don't have any " + shopItem.getMaterial() + " to sell.");
            return;
        }
        
        int quantity = bulk ? Math.min(64, availableCount) : 1;
        long totalEarnings = shopItem.getSellPrice() * quantity;
        
        int remaining = quantity;
        for (org.bukkit.inventory.ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material && remaining > 0) {
                int toRemove = Math.min(item.getAmount(), remaining);
                item.setAmount(item.getAmount() - toRemove);
                remaining -= toRemove;
            }
        }
        
        economyService.deposit(player.getUniqueId(), totalEarnings).thenAccept(success -> {
            if (success) {
                player.sendMessage("§aSold §f" + quantity + "x " + shopItem.getMaterial() + "§a for §6$" + totalEarnings);
            } else {
                player.sendMessage("§cFailed to deposit earnings.");
            }
        });
    }
}
