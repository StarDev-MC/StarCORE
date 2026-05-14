package net.starcore.starbank;

import net.starcore.api.BankService;
import net.starcore.api.EconomyService;
import net.starcore.api.PlayerDataService;
import net.starcore.plugin.AbstractStarCorePlugin;
import net.starcore.starbank.command.BankCommand;
import net.starcore.starbank.config.BankConfig;
import net.starcore.starbank.gui.BankGui;
import net.starcore.starbank.listener.BankGuiListener;
import net.starcore.starbank.manager.BankAccountManager;
import org.bukkit.Bukkit;

public final class StarBankPlugin extends AbstractStarCorePlugin {
    private BankAccountManager bankAccountManager;
    private BankGui bankGui;
    private BankConfig bankConfig;

    @Override
    protected void enablePlugin() {
        EconomyService economyService = getService(EconomyService.class);
        if (economyService == null) {
            getLogger().warning("StarEcon service was not available. Bank operations will be disabled.");
            return;
        }

        bankConfig = new BankConfig(this);
        bankAccountManager = new BankAccountManager(databaseService, economyService, bankConfig);
        services.register(BankService.class, bankAccountManager);

        bankGui = new BankGui(economyService, bankAccountManager, bankConfig);

        registerCommand(new BankCommand(this, economyService, bankAccountManager, bankGui, bankConfig));
        Bukkit.getPluginManager().registerEvents(new BankGuiListener(economyService, bankAccountManager, bankConfig), this);
    }

    public BankConfig getBankConfig() {
        return bankConfig;
    }
}
