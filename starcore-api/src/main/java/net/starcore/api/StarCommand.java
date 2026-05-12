package net.starcore.api;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public interface StarCommand extends CommandExecutor, TabCompleter {
    String getName();
}
