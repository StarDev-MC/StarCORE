package net.starcore.api;

import org.bukkit.command.CommandSender;

public interface PlaceholderResolver {
    String resolve(String source, CommandSender sender);
}
