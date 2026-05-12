package net.starcore.starcmds.command.teleport;

import net.starcore.plugin.command.BaseCommand;
import net.starcore.plugin.AbstractStarCorePlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends BaseCommand {
    private final AbstractStarCorePlugin plugin;

    public SetSpawnCommand(AbstractStarCorePlugin plugin) {
        super("setspawn");
        this.plugin = plugin;
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can set spawn.");
            return true;
        }
        if (!player.hasPermission("starcmds.spawn.set")) {
            player.sendMessage("You do not have permission to set spawn.");
            return true;
        }
        var config = plugin.getConfig();
        var location = player.getLocation();
        config.set("spawn.world", location.getWorld().getName());
        config.set("spawn.x", location.getX());
        config.set("spawn.y", location.getY());
        config.set("spawn.z", location.getZ());
        config.set("spawn.yaw", location.getYaw());
        config.set("spawn.pitch", location.getPitch());
        plugin.saveConfig();
        player.sendMessage("Spawn location updated.");
        return true;
    }
}
