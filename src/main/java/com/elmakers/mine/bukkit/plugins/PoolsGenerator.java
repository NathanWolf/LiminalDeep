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
    public static final int FLOOR_LEVEL = BEDROCK_LEVEL + 2;
    private static final int ROOF_MIN_HEIGHT = 3;
    private static final int ROOF_MAX_HEIGHT = 10;
    private static final double WALL_PROBABILITY = 0.91;
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
        final int floorLevel = FLOOR_LEVEL;
        final int roofLevel = floorLevel + random.nextInt(ROOF_MAX_HEIGHT - ROOF_MIN_HEIGHT) + ROOF_MIN_HEIGHT;
        final int roofMaxLevel = roofLevel + ROOF_MAX_HEIGHT;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (x == 0 || z == 0) {
                    // boolean hasWall = random.nextDouble() < WALL_PROBABILITY;
                    boolean hasWall = true;
                    if (hasWall) {
                        // Walls and doorway
                        boolean isDoorway = x == 7 || z == 7 || x == 8 || z == 8 || x == 9 || z == 9;
                        for (int y = floorLevel; y <= roofMaxLevel; y++) {
                            if (isDoorway && y < floorLevel + 3 && y > floorLevel) continue;
                            chunk.setBlock(x, y, z, Material.QUARTZ_BLOCK);
                        }
                    } else {
                        chunk.setBlock(x, floorLevel, z, Material.QUARTZ_BLOCK);
                        for (int y = roofLevel; y <= roofMaxLevel; y++) {
                            chunk.setBlock(x, y, z, Material.QUARTZ_BLOCK);
                        }
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

                // Sunroof walls
                if (x >= 6 && z >= 6 && x <= 10 && z <= 10) {
                    if (x == 6 || z == 6 || x == 10 || z == 10) {
                        for (int y = roofLevel + 1; y <= roofMaxLevel; y++) {
                            chunk.setBlock(x, y, z, Material.QUARTZ_BLOCK);
                        }
                    }
                }

                chunk.setBlock(x, BEDROCK_LEVEL + 1, z, Material.QUARTZ_BLOCK);
                chunk.setBlock(x, BEDROCK_LEVEL, z, Material.BEDROCK);
            }
        }
    }
}
