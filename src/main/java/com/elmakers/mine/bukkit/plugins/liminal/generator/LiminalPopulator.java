package com.elmakers.mine.bukkit.plugins.liminal.generator;

import org.bukkit.Chunk;
import org.bukkit.generator.BlockPopulator;

import com.elmakers.mine.bukkit.plugins.liminal.LiminalWorld;
import com.elmakers.mine.bukkit.plugins.liminal.LiminalWorldPlugin;

public class LiminalPopulator extends BlockPopulator {
    protected final LiminalGenerator generator;

    public LiminalPopulator(LiminalGenerator generator) {
        this.generator = generator;
    }

    public void checkNewChunk(Chunk chunk) {
    }

    public LiminalGenerator getGenerator() {
        return generator;
    }

    public LiminalWorld getWorld() {
        return generator.getWorld();
    }

    public LiminalWorldPlugin getPlugin() {
        return generator.getPlugin();
    }
}
