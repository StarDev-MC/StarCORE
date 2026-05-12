package net.starcore.starcmds.command.admin;

import net.starcore.plugin.command.BaseCommand;
import net.starcore.plugin.AbstractStarCorePlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand extends BaseCommand {
    private final AbstractStarCorePlugin plugin;
    private final java.util.Set<java.util.UUID> vanished = new java.util.concurrent.ConcurrentHashMap<>()
            .newKeySet();

    public VanishCommand(AbstractStarCorePlugin plugin) {
        super("vanish");
        this.plugin = plugin;
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players may toggle vanish.");
            return true;
        }
        if (!player.hasPermission("starcmds.vanish")) {
            player.sendMessage("You do not have permission to vanish.");
            return true;
        }
        if (vanished.contains(player.getUniqueId())) {
            vanished.remove(player.getUniqueId());
            plugin.getServer().getOnlinePlayers().forEach(other -> other.showPlayer(plugin, player));
            player.sendMessage("You are no longer vanished.");
        } else {
            vanished.add(player.getUniqueId());
            plugin.getServer().getOnlinePlayers().forEach(other -> other.hidePlayer(plugin, player));
            player.sendMessage("You are now vanished.");
        }
        return true;
    }
}
