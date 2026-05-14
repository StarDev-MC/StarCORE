package net.starcore.starbank.gui;

import net.starcore.api.EconomyService;
import net.starcore.starbank.config.BankConfig;
import net.starcore.starbank.manager.BankAccountManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class BankGui {
    private final EconomyService economyService;
    private final BankAccountManager bankAccountManager;
    private final BankConfig config;

    public BankGui(EconomyService economyService, BankAccountManager bankAccountManager, BankConfig config) {
        this.economyService = economyService;
        this.bankAccountManager = bankAccountManager;
        this.config = config;
    }

    public void openBankGui(Player player) {
        Inventory inventory = Bukkit.createInventory(null, config.getGuiSize(), config.getGuiTitle());

        // Balance display
        ItemStack balanceItem = createItem(Material.GOLD_INGOT, "Bank Balance", "Click to refresh");
        inventory.setItem(4, balanceItem);

        // Deposit buttons
        inventory.setItem(10, createItem(Material.GREEN_WOOL, "Deposit All", "Deposit all money from wallet"));
        inventory.setItem(11, createItem(Material.LIME_WOOL, "Deposit 50%", "Deposit half your wallet"));
        inventory.setItem(12, createItem(Material.GREEN_DYE, "Deposit Custom", "Not implemented"));

        // Withdraw buttons
        inventory.setItem(14, createItem(Material.RED_WOOL, "Withdraw 25%", "Withdraw 25% of bank balance"));
        inventory.setItem(15, createItem(Material.ORANGE_WOOL, "Withdraw 50%", "Withdraw 50% of bank balance"));
        inventory.setItem(16, createItem(Material.RED_WOOL, "Withdraw All", "Withdraw all from bank"));

        // Transaction history
        inventory.setItem(22, createItem(Material.BOOK, "Transaction History", "View recent transactions"));

        // Fill empty slots with glass
        ItemStack filler = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }

        player.openInventory(inventory);

        // Update balance
        updateBalance(player, inventory);
    }

    private void updateBalance(Player player, Inventory inventory) {
        bankAccountManager.getBalance(player.getUniqueId()).thenAccept(balance -> {
            economyService.getBalance(player.getUniqueId()).thenAccept(wallet -> {
                ItemStack balanceItem = createItem(Material.GOLD_INGOT, "Bank Balance: " + economyService.format(balance), "Wallet: " + economyService.format(wallet));
                inventory.setItem(4, balanceItem);
            });
        });
    }

    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }
}