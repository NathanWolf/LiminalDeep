package com.elmakers.mine.bukkit.plugins.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import com.elmakers.mine.bukkit.plugins.LiminalWorldPlugin;

public class ChunkListener implements Listener {
    private final LiminalWorldPlugin plugin;

    public ChunkListener(LiminalWorldPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk()) {
            plugin.checkNewChunk(event.getChunk());
        }
    }
}
