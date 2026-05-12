package net.starcore.starcmds;

import net.starcore.api.EconomyService;
import net.starcore.plugin.AbstractStarCorePlugin;
import net.starcore.starcmds.command.admin.VanishCommand;
import net.starcore.starcmds.command.chat.MsgCommand;
import net.starcore.starcmds.command.teleport.BackCommand;
import net.starcore.starcmds.command.teleport.SetSpawnCommand;
import net.starcore.starcmds.command.teleport.SpawnCommand;

public final class StarCmdsPlugin extends AbstractStarCorePlugin {
    @Override
    protected void enablePlugin() {
        EconomyService economyService = getService(EconomyService.class);

        registerCommand(new SpawnCommand(this));
        registerCommand(new SetSpawnCommand(this));
        registerCommand(new BackCommand(this));
        registerCommand(new MsgCommand(this));
        registerCommand(new VanishCommand(this));
        getServer().getPluginManager().registerEvents(new net.starcore.starcmds.listener.TeleportTracker(), this);

        if (economyService != null) {
            // Register economy-aware utility commands when StarEcon is available
        }
    }
}
