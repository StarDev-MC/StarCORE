package net.starcore.starbank.command;

import net.starcore.api.EconomyService;
import net.starcore.starbank.config.BankConfig;
import net.starcore.starbank.gui.BankGui;
import net.starcore.starbank.manager.BankAccountManager;
import net.starcore.plugin.command.BaseCommand;
import net.starcore.plugin.AbstractStarCorePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class BankCommand extends BaseCommand {
    private final AbstractStarCorePlugin plugin;
    private final EconomyService economyService;
    private final BankAccountManager bankAccountManager;
    private final BankGui bankGui;
    private final BankConfig config;

    public BankCommand(AbstractStarCorePlugin plugin, EconomyService economyService, BankAccountManager accountManager, BankGui bankGui, BankConfig config) {
        super("bank");
        this.plugin = plugin;
        this.economyService = economyService;
        this.bankAccountManager = accountManager;
        this.bankGui = bankGui;
        this.config = config;
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use the bank.");
            return true;
        }
        if (args.length == 0) {
            return balance(player);
        }
        switch (args[0].toLowerCase()) {
            case "deposit" -> {
                if (args.length != 2) {
                    player.sendMessage("Usage: /bank deposit <amount>");
                    return true;
                }
                return deposit(player, args[1]);
            }
            case "withdraw" -> {
                if (args.length != 2) {
                    player.sendMessage("Usage: /bank withdraw <amount>");
                    return true;
                }
                return withdraw(player, args[1]);
            }
            case "balance", "bal" -> {
                return balance(player);
            }
            case "transactions", "trans" -> {
                return transactions(player, args);
            }
            case "gui" -> {
                return gui(player);
            }
            default -> player.sendMessage("Unknown bank command. Use deposit, withdraw, balance, transactions or gui.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("balance", "deposit", "withdraw", "transactions", "gui");
        }
        return super.onTabComplete(sender, command, alias, args);
    }

    private boolean balance(Player player) {
        bankAccountManager.getBalance(player.getUniqueId()).thenAccept(balance -> {
            String msg = config.getMessage("balance").replace("%balance%", economyService.format(balance));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        });
        return true;
    }

    private boolean deposit(Player player, String amountArg) {
        long amount = parseAmount(amountArg);
        if (amount <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getMessage("invalid-amount")));
            return true;
        }
        if (amount > config.getTransactionLimit()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cAmount exceeds transaction limit."));
            return true;
        }
        bankAccountManager.deposit(player.getUniqueId(), amount, "Command deposit").thenAccept(success -> {
            if (success) {
                String msg = config.getMessage("deposit-success").replace("%amount%", economyService.format(amount));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getMessage("insufficient-funds")));
            }
        });
        return true;
    }

    private boolean withdraw(Player player, String amountArg) {
        long amount = parseAmount(amountArg);
        if (amount <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getMessage("invalid-amount")));
            return true;
        }
        if (amount > config.getTransactionLimit()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cAmount exceeds transaction limit."));
            return true;
        }
        bankAccountManager.withdraw(player.getUniqueId(), amount, "Command withdraw").thenAccept(success -> {
            if (success) {
                String msg = config.getMessage("withdraw-success").replace("%amount%", economyService.format(amount));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getMessage("insufficient-funds")));
            }
        });
        return true;
    }

    private boolean transactions(Player player, String[] args) {
        int limit = 10;
        if (args.length > 1) {
            try {
                limit = Integer.parseInt(args[1]);
                if (limit > 50) limit = 50;
            } catch (NumberFormatException ignored) {}
        }
        bankAccountManager.getTransactions(player.getUniqueId(), limit, 0).thenAccept(transactions -> {
            if (transactions.isEmpty()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getMessage("no-transactions")));
                return;
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getMessage("transaction-header")));
            for (var trans : transactions) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e" + trans.type() + " &a" + economyService.format(trans.amount()) + " &7- " + trans.reason()));
            }
        });
        return true;
    }

    private boolean gui(Player player) {
        bankGui.openBankGui(player);
        return true;
    }

    private long parseAmount(String arg) {
        try {
            return Long.parseLong(arg);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
