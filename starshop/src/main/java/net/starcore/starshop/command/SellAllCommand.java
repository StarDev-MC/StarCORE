package net.starcore.starshop.command;

import net.starcore.api.EconomyService;
import net.starcore.plugin.command.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.starcore.plugin.AbstractStarCorePlugin;

public class SellAllCommand extends BaseCommand {
    private final EconomyService economyService;

    public SellAllCommand(AbstractStarCorePlugin plugin, EconomyService economyService) {
        super("sellall");
        this.economyService = economyService;
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can sell all items.");
            return true;
        }
        player.sendMessage("Sell-all is available in the StarShop framework. Future builds will match items to category pricing.");
        return true;
    }
}
