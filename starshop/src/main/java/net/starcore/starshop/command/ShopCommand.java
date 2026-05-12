package net.starcore.starshop.command;

import net.starcore.plugin.command.BaseCommand;
import net.starcore.plugin.AbstractStarCorePlugin;
import net.starcore.starshop.manager.ShopManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand extends BaseCommand {
    private final ShopManager shopManager;
    private final AbstractStarCorePlugin plugin;

    public ShopCommand(AbstractStarCorePlugin plugin, ShopManager shopManager) {
        super("shop");
        this.plugin = plugin;
        this.shopManager = shopManager;
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can open the shop.");
            return true;
        }
        shopManager.openShop(player);
        return true;
    }
}
