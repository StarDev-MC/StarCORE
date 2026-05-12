package net.starcore.starecon;

import net.starcore.api.EconomyService;
import net.starcore.api.MessageProvider;
import net.starcore.api.PlayerDataService;
import net.starcore.plugin.AbstractStarCorePlugin;
import net.starcore.starecon.command.BalanceCommand;
import net.starcore.starecon.command.BalanceTopCommand;
import net.starcore.starecon.command.PayCommand;

public final class StarEconPlugin extends AbstractStarCorePlugin {
    private EconomyService economyService;

    @Override
    protected void enablePlugin() {
        economyService = new DefaultEconomyService(playerDataService, messageProvider);
        services.register(EconomyService.class, economyService);

        registerCommand(new BalanceCommand(this, economyService));
        registerCommand(new PayCommand(this, economyService));
        registerCommand(new BalanceTopCommand(this, playerDataService, messageProvider));
    }
}
