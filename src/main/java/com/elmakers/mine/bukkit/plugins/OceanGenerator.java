package com.elmakers.mine.bukkit.plugins;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

public class OceanGenerator extends ChunkGenerator {
    private static final int SEA_LEVEL = 32;
    private static final int SAND_LEVEL = 6;
    private static final int BEDROCK_LAYER = 1;
    private final LiminalWorldPlugin plugin;

    public OceanGenerator(LiminalWorldPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunk) {
        final int minY = worldInfo.getMinHeight();
        final int maxY = worldInfo.getMaxHeight();
        final int seaLevel = maxY - SEA_LEVEL;
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
}
