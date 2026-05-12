package net.starcore.starcmds.command.chat;

import net.starcore.plugin.command.BaseCommand;
import net.starcore.plugin.AbstractStarCorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MsgCommand extends BaseCommand {
    private final AbstractStarCorePlugin plugin;

    public MsgCommand(AbstractStarCorePlugin plugin) {
        super("msg");
        this.plugin = plugin;
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can message other players.");
            return true;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /msg <player> <message>");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage("Player not found.");
            return true;
        }
        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        target.sendMessage("[From " + player.getName() + "] " + message);
        player.sendMessage("[To " + target.getName() + "] " + message);
        return true;
    }
}
