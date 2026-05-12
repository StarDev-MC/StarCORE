package net.starcore.starecon.command;

import net.starcore.api.MessageProvider;
import net.starcore.api.PlayerData;
import net.starcore.api.PlayerDataService;
import net.starcore.plugin.command.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.starcore.plugin.AbstractStarCorePlugin;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BalanceTopCommand extends BaseCommand {
    private final AbstractStarCorePlugin plugin;
    private final PlayerDataService playerDataService;
    private final MessageProvider messageProvider;

    public BalanceTopCommand(AbstractStarCorePlugin plugin, PlayerDataService playerDataService, MessageProvider messageProvider) {
        super("balancetop");
        this.plugin = plugin;
        this.playerDataService = playerDataService;
        this.messageProvider = messageProvider;
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players may use this command.");
            return true;
        }
        List<PlayerData> top = playerDataService.getCachedList().stream()
                .sorted(Comparator.comparingLong(PlayerData::walletBalance).reversed())
                .limit(10)
                .collect(Collectors.toList());
        sender.sendMessage(messageProvider.colorize("§6Top Balances"));
        for (int i = 0; i < top.size(); i++) {
            PlayerData row = top.get(i);
            sender.sendMessage(String.format("§e%d. §f%s §7- §a%s", i + 1, row.username(), messageProvider.colorize("$" + row.walletBalance())));
        }
        return true;
    }
}
