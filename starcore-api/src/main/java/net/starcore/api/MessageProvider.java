package net.starcore.api;

import org.bukkit.command.CommandSender;

public interface MessageProvider {
    String translate(String key);

    String translate(String key, CommandSender receiver);

    String colorize(String input);
}
