package net.starcore.starecon.command;

import net.starcore.api.EconomyService;
import net.starcore.plugin.command.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.starcore.plugin.AbstractStarCorePlugin;

public class BalanceCommand extends BaseCommand {
    private final AbstractStarCorePlugin plugin;
    private final EconomyService economyService;

    public BalanceCommand(AbstractStarCorePlugin plugin, EconomyService economyService) {
        super("balance");
        this.plugin = plugin;
        this.economyService = economyService;
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can view balances.");
            return true;
        }

        economyService.getBalance(player.getUniqueId()).thenAccept(balance -> {
            player.sendMessage(plugin.getConfig().getString("messages.balance", "Your balance is {balance}")
                    .replace("{balance}", economyService.format(balance)));
        });
        return true;
    }
}
