package net.starcore.starshop.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GuiUtils {
    private static final Material FILLER = Material.GRAY_STAINED_GLASS_PANE;
    private static final Material NEXT_PAGE = Material.GREEN_STAINED_GLASS_PANE;
    private static final Material PREV_PAGE = Material.RED_STAINED_GLASS_PANE;
    private static final Material BACK = Material.YELLOW_STAINED_GLASS_PANE;

    public static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(colorize(name));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = createItem(material, name);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(colorize(line));
            }
            meta.setLore(coloredLore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createFillerItem() {
        return createItem(FILLER, " ");
    }

    public static ItemStack createNextPageItem(int page, int maxPages) {
        List<String> lore = new ArrayList<>();
        lore.add("&7Page " + (page + 1) + " of " + maxPages);
        lore.add("&7Click to go to next page");
        return createItem(NEXT_PAGE, "&6Next Page", lore);
    }

    public static ItemStack createPrevPageItem(int page) {
        List<String> lore = new ArrayList<>();
        lore.add("&7Page " + page + " of many");
        lore.add("&7Click to go to previous page");
        return createItem(PREV_PAGE, "&cPrevious Page", lore);
    }

    public static ItemStack createBackItem() {
        List<String> lore = new ArrayList<>();
        lore.add("&7Return to category menu");
        return createItem(BACK, "&eBack", lore);
    }

    public static String colorize(String text) {
        if (text == null) {
            return "";
        }
        return text.replace('&', '§');
    }

    public static void fillRow(Inventory inv, int startSlot, ItemStack item) {
        for (int i = 0; i < 9; i++) {
            inv.setItem(startSlot + i, new ItemStack(item));
        }
    }

    public static void fillBorder(Inventory inv, ItemStack item) {
        int size = inv.getSize();
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, new ItemStack(item));
            inv.setItem(size - 9 + i, new ItemStack(item));
        }
        for (int row = 1; row < (size / 9) - 1; row++) {
            inv.setItem(row * 9, new ItemStack(item));
            inv.setItem(row * 9 + 8, new ItemStack(item));
        }
    }
}
