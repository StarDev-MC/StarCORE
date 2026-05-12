package net.starcore.starboard;

import net.starcore.api.EconomyService;
import net.starcore.plugin.AbstractStarCorePlugin;
import net.starcore.starboard.listener.PlayerJoinListener;
import net.starcore.starboard.scoreboard.ScoreboardManager;

public final class StarBoardPlugin extends AbstractStarCorePlugin {
    private ScoreboardManager scoreboardManager;

    @Override
    protected void enablePlugin() {
        EconomyService economyService = getService(EconomyService.class);
        scoreboardManager = new ScoreboardManager(taskScheduler, messageProvider, placeholderResolver, economyService);
        eventBus.register(new PlayerJoinListener(scoreboardManager));
        scoreboardManager.start();
    }
}
