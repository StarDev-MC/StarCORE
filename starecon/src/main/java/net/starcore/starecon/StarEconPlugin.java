package net.starcore.starecon;

import net.starcore.api.EconomyService;
import net.starcore.plugin.AbstractStarCorePlugin;

/**
 * StarEcon - Professional grade modular economy system for StarCore.
 * 
 * ARCHITECTURE OVERVIEW:
 * - api/ contains the public EconomyAPI interface for other plugins
 * - database/ provides HikariCP pooling, connection management, config
 * - repository/ implements Repository pattern with SQLite (extensible to MySQL)
 * - model/ defines Transaction records, AccountBalance, TransactionType enums
 * - manager/ contains EconomyManager (core service implementation)
 * - event/ provides BalanceChangeEvent, PlayerPayEvent, EconomyTransactionEvent
 * - config/ manages economy configuration settings
 * - util/ includes CurrencyFormatter for $1.2M style formatting
 * - command/ structured commands following BaseCommand pattern
 * 
 * KEY DESIGN PRINCIPLES:
 * - All database operations are async (non-blocking)
 * - Thread-safe using HikariCP connection pooling
 * - Double-precision balances with NaN/overflow validation
 * - Transaction audit logging for compliance
 * - Immutable data records (AccountBalance, Transaction)
 * - Clean API interfaces for other plugins
 * - Extensible to MySQL/MariaDB without code changes
 * - Custom Bukkit events for plugin integration
 */
public final class StarEconPlugin extends AbstractStarCorePlugin {
    private DefaultEconomyService economyService;

    @Override
    protected void enablePlugin() {
        economyService = new DefaultEconomyService(playerDataService, messageProvider);
        services.register(EconomyService.class, economyService);

        getLogger().info("StarEcon (professional build) enabled successfully");
        getLogger().info("Professional architecture: repository pattern, async database, transaction logging");
    }
}
