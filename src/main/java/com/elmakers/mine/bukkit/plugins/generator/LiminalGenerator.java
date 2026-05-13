package com.elmakers.mine.bukkit.plugins.generator;

import java.util.Locale;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.elmakers.mine.bukkit.plugins.LiminalWorld;
import com.elmakers.mine.bukkit.plugins.LiminalWorldPlugin;

public abstract class LiminalGenerator extends ChunkGenerator {
    protected final LiminalWorld world;
    protected final BiomeProvider biomeProvider;

    public LiminalGenerator(LiminalWorld world, ConfigurationSection generalConfig, ConfigurationSection config) {
        this.world = world;
        biomeProvider = createDefaultBiomeProvider(config);
    }

    protected BiomeProvider createDefaultBiomeProvider(ConfigurationSection config) {
        String biomeKey = config.getString("biome");
        try {
            if (biomeKey != null) {
                return new SingleBiomeProvider(Biome.valueOf(biomeKey.toUpperCase(Locale.ROOT)));
            }
        } catch (Exception ex) {
            world.getLogger().warning("Invalid biome specified in " + world.getName() + " config: " + biomeKey);
        }
        return null;
    }

    @Nullable
    public BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
        return biomeProvider;
    }


    public abstract Location getSpawnLocation(World world);

    public Location getEntryLocation(World world) {
        Location location = getSpawnLocation(world);
        location.setY(world.getMaxHeight() - 16);
        return location;
    }

    public void checkNewChunk(Chunk chunk) {

    }

    public LiminalWorld getWorld() {
        return world;
    }

    public LiminalWorldPlugin getPlugin() {
        return world.getPlugin();
    }
}
