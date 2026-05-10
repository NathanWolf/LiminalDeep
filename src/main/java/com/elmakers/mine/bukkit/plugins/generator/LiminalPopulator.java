package com.elmakers.mine.bukkit.plugins.generator;

import org.bukkit.Chunk;
import org.bukkit.generator.BlockPopulator;

import com.elmakers.mine.bukkit.plugins.LiminalWorldPlugin;

public class LiminalPopulator extends BlockPopulator {
    protected final LiminalWorldPlugin plugin;

    public LiminalPopulator(LiminalWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public void checkNewChunk(Chunk chunk) {
    }
}
