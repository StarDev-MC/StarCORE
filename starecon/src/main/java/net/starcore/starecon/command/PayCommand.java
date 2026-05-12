package net.starcore.starecon.command;

import net.starcore.api.EconomyService;
import net.starcore.plugin.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.starcore.plugin.AbstractStarCorePlugin;

import java.util.UUID;

public class PayCommand extends BaseCommand {
    private final AbstractStarCorePlugin plugin;
    private final EconomyService economyService;

    public PayCommand(AbstractStarCorePlugin plugin, EconomyService economyService) {
        super("pay");
        this.plugin = plugin;
        this.economyService = economyService;
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player payer)) {
            sender.sendMessage("Only players can use /pay.");
            return true;
        }
        if (args.length != 2) {
            payer.sendMessage("Usage: /pay <player> <amount>");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            payer.sendMessage("That player is not online.");
            return true;
        }
        long amount;
        try {
            amount = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            payer.sendMessage("Please provide a valid amount.");
            return true;
        }
        if (amount <= 0) {
            payer.sendMessage("Amount must be positive.");
            return true;
        }

        economyService.transfer(payer.getUniqueId(), target.getUniqueId(), amount)
                .thenAccept(success -> {
                    if (success) {
                        payer.sendMessage("You paid " + target.getName() + " " + economyService.format(amount) + ".");
                        target.sendMessage(payer.getName() + " paid you " + economyService.format(amount) + ".");
                    } else {
                        payer.sendMessage("Transaction failed. Check your balance.");
                    }
                });
        return true;
    }
}
