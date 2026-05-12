package net.starcore.common.util;

import org.bukkit.command.CommandSender;

public final class PermissionUtil {
    private PermissionUtil() {
    }

    public static boolean has(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    public static boolean require(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) {
            return true;
        }
        sender.sendMessage("§cYou do not have permission to use this command.");
        return false;
    }
}
