package com.elmakers.mine.bukkit.plugins;

import org.bukkit.Material;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class PoolsGenerator extends ChunkGenerator {
    private static final int BEDROCK_LEVEL = 60;
    private static final int ROOF_HEIGHT = 5;
    private final LiminalWorldPlugin plugin;
    private final BiomeProvider biomeProvider;

    public PoolsGenerator(LiminalWorldPlugin plugin) {
        this.plugin = plugin;
        biomeProvider = new DesertBiomeProvider();
    }

    @Nullable
    public BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
        return biomeProvider;
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunk) {
        final int floorLevel = BEDROCK_LEVEL + 2;
        final int roofLevel = floorLevel + ROOF_HEIGHT;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (x == 0 || z == 0) {
                    // Walls and doorway
                    boolean isDoorway = x == 7 || z == 7 || x == 8 || z == 8 || x == 9 || z == 9;
                    for (int y = floorLevel; y <= roofLevel; y++) {
                        if (isDoorway && y < floorLevel + 3 && y > floorLevel) continue;
                        chunk.setBlock(x, y, z, Material.QUARTZ_BLOCK);
                    }
                } else if (x == 1 || z == 1 || x == 15 || z == 15) {
                    // Walkway
                    chunk.setBlock(x, floorLevel, z, Material.QUARTZ_BLOCK);
                    chunk.setBlock(x, roofLevel, z, Material.QUARTZ_BLOCK);
                } else if (x >= 7 && z >= 7 && x <= 9 && z <= 9) {
                    // Island
                    chunk.setBlock(x, floorLevel, z, Material.QUARTZ_BLOCK);
                } else if (x == 8 || z == 8) {
                    // Pathways
                    chunk.setBlock(x, roofLevel, z, Material.QUARTZ_BLOCK);
                    chunk.setBlock(x, floorLevel, z, Material.QUARTZ_BLOCK);
                } else {
                    // Water and roof
                    chunk.setBlock(x, roofLevel, z, Material.QUARTZ_BLOCK);
                    chunk.setBlock(x, floorLevel, z, Material.WATER);
                }
                chunk.setBlock(x, BEDROCK_LEVEL + 1, z, Material.QUARTZ_BLOCK);
                chunk.setBlock(x, BEDROCK_LEVEL, z, Material.BEDROCK);
            }
        }
    }
}
