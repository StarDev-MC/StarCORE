package net.starcore.starbank;

import net.starcore.api.BankService;
import net.starcore.api.EconomyService;
import net.starcore.api.PlayerDataService;
import net.starcore.plugin.AbstractStarCorePlugin;
import net.starcore.starbank.command.BankCommand;
import net.starcore.starbank.manager.BankAccountManager;

public final class StarBankPlugin extends AbstractStarCorePlugin {
    private BankAccountManager bankAccountManager;

    @Override
    protected void enablePlugin() {
        EconomyService economyService = getService(EconomyService.class);
        if (economyService == null) {
            getLogger().warning("StarEcon service was not available. Bank operations will be disabled.");
            return;
        }
        bankAccountManager = new BankAccountManager(databaseService, economyService);
        services.register(BankService.class, bankAccountManager);
        registerCommand(new BankCommand(this, economyService, bankAccountManager));
    }
}
