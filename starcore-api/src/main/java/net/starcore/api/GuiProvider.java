package net.starcore.api;

import org.bukkit.inventory.Inventory;

public interface GuiProvider {
    Inventory createMenu(String title, int size);
}
