package com.elmakers.mine.bukkit.plugins.generator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import com.elmakers.mine.bukkit.plugins.LiminalWorldPlugin;

public abstract class LiminalGenerator extends ChunkGenerator {
    protected final String worldName;
    protected final LiminalWorldPlugin plugin;

    public LiminalGenerator(LiminalWorldPlugin plugin, ConfigurationSection generalConfig, ConfigurationSection config) {
        this.worldName = config.getString("world");
        this.plugin = plugin;
    }

    public String getWorldName() {
        return worldName;
    }

    public abstract Location getSpawnLocation(World world);

    public Location toNextLevel(Player player) {
        return null;
    }
}
