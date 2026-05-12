package net.starcore.starshop.command;

import net.starcore.api.EconomyService;
import net.starcore.plugin.command.BaseCommand;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.starcore.plugin.AbstractStarCorePlugin;

public class SellCommand extends BaseCommand {
    private final EconomyService economyService;

    public SellCommand(AbstractStarCorePlugin plugin, EconomyService economyService) {
        super("sell");
        this.economyService = economyService;
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can sell items.");
            return true;
        }
        if (player.getGameMode() == GameMode.CREATIVE) {
            player.sendMessage("You cannot sell items in creative mode.");
            return true;
        }
        if (player.getInventory().getItemInMainHand().isEmpty()) {
            player.sendMessage("Hold an item to sell.");
            return true;
        }
        player.sendMessage("This shop currently supports GUI browsing and sell-all support.");
        return true;
    }
}
