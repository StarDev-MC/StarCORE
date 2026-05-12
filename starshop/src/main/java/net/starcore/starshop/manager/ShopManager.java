package net.starcore.starshop.manager;

import net.starcore.api.EconomyService;
import net.starcore.common.config.ConfigManager;
import net.starcore.common.gui.GuiFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ShopManager {
    private final GuiFactory guiFactory;
    private final EconomyService economyService;
    private final ConfigManager configManager;
    private final Map<String, ShopItem> shopItems = new HashMap<>();

    public ShopManager(org.bukkit.plugin.java.JavaPlugin plugin, EconomyService economyService) {
        this.guiFactory = new GuiFactory();
        this.economyService = economyService;
        this.configManager = new ConfigManager(plugin);
        loadItems();
    }

    private void loadItems() {
        ConfigurationSection itemsSection = configManager.get("shop.yml").getConfigurationSection("items");
        if (itemsSection == null) {
            return;
        }
        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
            if (itemSection == null) {
                continue;
            }
            ItemStack stack = ItemStack.deserialize(itemSection.getConfigurationSection("item").getValues(false));
            long price = itemSection.getLong("price", 0);
            shopItems.put(key, new ShopItem(key, stack, price));
        }
    }

    public void openShop(Player player) {
        Inventory inventory = guiFactory.createMenu("StarShop", 27);
        int slot = 0;
        for (ShopItem item : shopItems.values()) {
            ItemStack stack = item.stack().clone();
            ItemMeta meta = stack.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§a" + item.name());
                meta.setLore(java.util.List.of("§7Price: " + economyService.format(item.price())));
                stack.setItemMeta(meta);
            }
            inventory.setItem(slot++, stack);
        }
        player.openInventory(inventory);
    }

    public long getSellValue(ItemStack stack) {
        return shopItems.values().stream()
                .filter(item -> item.stack().isSimilar(stack))
                .mapToLong(ShopItem::price)
                .findFirst()
                .orElse(0L);
    }

    public boolean isShopItem(ItemStack stack) {
        return shopItems.values().stream().anyMatch(item -> item.stack().isSimilar(stack));
    }
}
