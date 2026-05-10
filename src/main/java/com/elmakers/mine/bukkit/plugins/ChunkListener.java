package com.elmakers.mine.bukkit.plugins;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkListener implements Listener {
    private final LiminalWorldPlugin plugin;

    public ChunkListener(LiminalWorldPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk()) {
            PoolsExitPopulator.checkNewChunk(event.getChunk());
        }
    }
}
