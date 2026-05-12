package net.starcore.starcmds.command.teleport;

import net.starcore.plugin.command.BaseCommand;
import net.starcore.plugin.AbstractStarCorePlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand extends BaseCommand {
    private final AbstractStarCorePlugin plugin;

    public SpawnCommand(AbstractStarCorePlugin plugin) {
        super("spawn");
        this.plugin = plugin;
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can teleport to spawn.");
            return true;
        }
        var config = plugin.getConfig();
        if (!config.contains("spawn.world")) {
            player.sendMessage("Spawn is not set yet.");
            return true;
        }
        var world = plugin.getServer().getWorld(config.getString("spawn.world"));
        if (world == null) {
            player.sendMessage("Spawn world is unavailable.");
            return true;
        }
        var location = new org.bukkit.Location(world,
                config.getDouble("spawn.x"),
                config.getDouble("spawn.y"),
                config.getDouble("spawn.z"),
                (float) config.getDouble("spawn.yaw"),
                (float) config.getDouble("spawn.pitch"));
        player.teleport(location);
        player.sendMessage("Teleported to spawn.");
        return true;
    }
}
