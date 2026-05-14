package net.starcore.starbank.listener;

import net.starcore.api.EconomyService;
import net.starcore.starbank.config.BankConfig;
import net.starcore.starbank.manager.BankAccountManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BankGuiListener implements Listener {
    private final EconomyService economyService;
    private final BankAccountManager bankAccountManager;
    private final BankConfig config;

    public BankGuiListener(EconomyService economyService, BankAccountManager bankAccountManager, BankConfig config) {
        this.economyService = economyService;
        this.bankAccountManager = bankAccountManager;
        this.config = config;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(config.getGuiTitle())) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack item = event.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;

        String name = item.getItemMeta().getDisplayName();

        switch (name) {
            case "Deposit All" -> depositAll(player);
            case "Deposit 50%" -> depositHalf(player);
            case "Withdraw 25%" -> withdrawPercent(player, 25);
            case "Withdraw 50%" -> withdrawPercent(player, 50);
            case "Withdraw All" -> withdrawAll(player);
            case "Transaction History" -> showTransactions(player);
        }
    }

    private void depositAll(Player player) {
        economyService.getBalance(player.getUniqueId()).thenAccept(wallet -> {
            if (wallet <= 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getMessage("insufficient-funds")));
                return;
            }
            bankAccountManager.deposit(player.getUniqueId(), wallet, "GUI deposit all").thenAccept(success -> {
                if (success) {
                    String msg = config.getMessage("deposit-success").replace("%amount%", economyService.format(wallet));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getMessage("insufficient-funds")));
                }
            });
        });
    }

    private void depositHalf(Player player) {
        economyService.getBalance(player.getUniqueId()).thenAccept(wallet -> {
            long amount = wallet / 2;
            if (amount <= 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getMessage("insufficient-funds")));
                return;
            }
            bankAccountManager.deposit(player.getUniqueId(), amount, "GUI deposit 50%").thenAccept(success -> {
                if (success) {
                    String msg = config.getMessage("deposit-success").replace("%amount%", economyService.format(amount));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getMessage("insufficient-funds")));
                }
            });
        });
    }

    private void withdrawPercent(Player player, int percent) {
        bankAccountManager.getBalance(player.getUniqueId()).thenAccept(balance -> {
            long amount = balance * percent / 100;
            if (amount <= 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getMessage("insufficient-funds")));
                return;
            }
            bankAccountManager.withdraw(player.getUniqueId(), amount, "GUI withdraw " + percent + "%").thenAccept(success -> {
                if (success) {
                    String msg = config.getMessage("withdraw-success").replace("%amount%", economyService.format(amount));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getMessage("insufficient-funds")));
                }
            });
        });
    }

    private void withdrawAll(Player player) {
        bankAccountManager.getBalance(player.getUniqueId()).thenAccept(balance -> {
            if (balance <= 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getMessage("insufficient-funds")));
                return;
            }
            bankAccountManager.withdraw(player.getUniqueId(), balance, "GUI withdraw all").thenAccept(success -> {
                if (success) {
                    String msg = config.getMessage("withdraw-success").replace("%amount%", economyService.format(balance));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getMessage("insufficient-funds")));
                }
            });
        });
    }

    private void showTransactions(Player player) {
        bankAccountManager.getTransactions(player.getUniqueId(), 10, 0).thenAccept(transactions -> {
            if (transactions.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getMessage("no-transactions")));
                return;
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getMessage("transaction-header")));
            for (var trans : transactions) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e" + trans.type() + " &a" + economyService.format(trans.amount()) + " &7- " + trans.reason()));
            }
        });
    }
}