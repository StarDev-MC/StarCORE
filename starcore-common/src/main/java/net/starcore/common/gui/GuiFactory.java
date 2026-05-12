package net.starcore.common.gui;

import net.starcore.api.GuiProvider;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class GuiFactory implements GuiProvider {
    @Override
    public Inventory createMenu(String title, int size) {
        return Bukkit.createInventory(null, size, title);
    }
}
