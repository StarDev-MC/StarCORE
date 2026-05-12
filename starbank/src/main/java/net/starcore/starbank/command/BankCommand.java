package net.starcore.starbank.command;

import net.starcore.api.EconomyService;
import net.starcore.starbank.manager.BankAccountManager;
import net.starcore.plugin.command.BaseCommand;
import net.starcore.plugin.AbstractStarCorePlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BankCommand extends BaseCommand {
    private final AbstractStarCorePlugin plugin;
    private final EconomyService economyService;
    private final BankAccountManager bankAccountManager;

    public BankCommand(AbstractStarCorePlugin plugin, EconomyService economyService, BankAccountManager accountManager) {
        super("bank");
        this.plugin = plugin;
        this.economyService = economyService;
        this.bankAccountManager = accountManager;
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
            case "balance" -> {
                return balance(player);
            }
            default -> player.sendMessage("Unknown bank command. Use deposit, withdraw or balance.");
        }
        return true;
    }

    private boolean balance(Player player) {
        bankAccountManager.getBalance(player.getUniqueId()).thenAccept(balance -> {
            player.sendMessage("Bank balance: " + economyService.format(balance));
        });
        return true;
    }

    private boolean deposit(Player player, String amountArg) {
        long amount;
        try {
            amount = Long.parseLong(amountArg);
        } catch (NumberFormatException e) {
            player.sendMessage("Please enter a valid amount.");
            return true;
        }
        bankAccountManager.deposit(player.getUniqueId(), amount).thenAccept(success -> {
            if (success) {
                player.sendMessage("Deposited " + economyService.format(amount) + " into your bank.");
            } else {
                player.sendMessage("Unable to deposit funds. Check your wallet.");
            }
        });
        return true;
    }

    private boolean withdraw(Player player, String amountArg) {
        long amount;
        try {
            amount = Long.parseLong(amountArg);
        } catch (NumberFormatException e) {
            player.sendMessage("Please enter a valid amount.");
            return true;
        }
        bankAccountManager.withdraw(player.getUniqueId(), amount).thenAccept(success -> {
            if (success) {
                player.sendMessage("Withdrew " + economyService.format(amount) + " from your bank.");
            } else {
                player.sendMessage("Unable to withdraw funds. Check your bank balance.");
            }
        });
        return true;
    }
}
