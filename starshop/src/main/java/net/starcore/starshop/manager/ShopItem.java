package net.starcore.starshop.manager;

import org.bukkit.inventory.ItemStack;

public record ShopItem(String name, ItemStack stack, long price) {
}
