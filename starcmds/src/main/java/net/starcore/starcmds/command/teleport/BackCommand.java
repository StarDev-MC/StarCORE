package net.starcore.starcmds.command.teleport;

import net.starcore.plugin.command.BaseCommand;
import net.starcore.plugin.AbstractStarCorePlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.starcore.starcmds.listener.TeleportTracker;

public class BackCommand extends BaseCommand {
    private final AbstractStarCorePlugin plugin;

    public BackCommand(AbstractStarCorePlugin plugin) {
        super("back");
        this.plugin = plugin;
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use /back.");
            return true;
        }
        var previous = TeleportTracker.getLastLocation(player.getUniqueId());
        if (previous == null) {
            player.sendMessage("No location to return to.");
            return true;
        }
        player.teleport(previous);
        player.sendMessage("Teleported back.");
        return true;
    }
}
