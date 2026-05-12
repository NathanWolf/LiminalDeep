package com.elmakers.mine.bukkit.plugins.generator;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import com.elmakers.mine.bukkit.plugins.LiminalWorldPlugin;

public class OceanGenerator extends LiminalGenerator {
    private int SKY_HEIGHT = 32;
    private int SAND_LEVEL = 6;
    private int BEDROCK_LAYER = 1;

    public OceanGenerator(LiminalWorldPlugin plugin, ConfigurationSection generalConfig, ConfigurationSection config) {
        super(plugin, generalConfig, config);

        SKY_HEIGHT = config.getInt("sky_height", SKY_HEIGHT);
        SAND_LEVEL = config.getInt("sand_level", SAND_LEVEL);
        BEDROCK_LAYER = config.getInt("bedrock_level", BEDROCK_LAYER);
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunk) {
        final int minY = worldInfo.getMinHeight();
        final int maxY = worldInfo.getMaxHeight();
        final int seaLevel = maxY - SKY_HEIGHT;
        final int sandLevel = minY + SAND_LEVEL;
        final int bedrockLevel = minY + BEDROCK_LAYER;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = seaLevel; y > sandLevel; y--) {
                    chunk.setBlock(x, y, z, Material.WATER);
                }
                for (int y = sandLevel; y >= bedrockLevel; y--) {
                    chunk.setBlock(x, y, z, Material.SAND);
                }
                chunk.setBlock(x, minY, z, Material.BEDROCK);
            }
        }
    }

    @Override
    public Location getSpawnLocation(World world) {
        final int maxY = world.getMaxHeight();
        return new Location(world, 0, maxY - SKY_HEIGHT + 1, 0);
    }

    @Override
    public Location getEntryLocation(World world) {
        Location location = getSpawnLocation(world);
        location.setY(world.getMaxHeight());
        return location;
    }
}
