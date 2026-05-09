package com.elmakers.mine.bukkit.plugins;

import org.bukkit.Material;
import org.bukkit.block.EndGateway;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class PoolsGenerator extends ChunkGenerator {
    private static final int BEDROCK_LEVEL = 60;
    public static final int FLOOR_LEVEL = BEDROCK_LEVEL + 2;
    private static final int ROOF_MIN_HEIGHT = 4;
    private static final int ROOF_MAX_HEIGHT = 10;
    private static final int DOORWAY_MIN_HEIGHT = 2;
    private static final int DOORWAY_MAX_HEIGHT = 4;
    private static final int DOORWAY_MAX_WIDTH_HALF = 3;
    private static final int WALKWAY_MAX_WIDTH_HALF = 5;
    private static final double WALL_PROBABILITY = 0.6;
    private static final double WINDOW_PROBABILITY = 0.3;
    private static final double ISLAND_PROBABILITY = 0.75;
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

    private BlockData getWindowBlock() {
        BlockData gatewayData = plugin.getServer().createBlockData(Material.END_GATEWAY);
        if (gatewayData instanceof EndGateway) {
            EndGateway gateway = (EndGateway)gatewayData;
            gateway.setAge(-Integer.MAX_VALUE);
        }
        return gatewayData;
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunk) {
        final int floorLevel = FLOOR_LEVEL;
        final int roofLevel = floorLevel + random.nextInt(ROOF_MAX_HEIGHT - ROOF_MIN_HEIGHT) + ROOF_MIN_HEIGHT;
        final int roofMaxLevel = floorLevel + ROOF_MAX_HEIGHT;
        final int doorwayLevel = Math.min(roofLevel, floorLevel + random.nextInt(DOORWAY_MAX_HEIGHT - DOORWAY_MIN_HEIGHT) + DOORWAY_MIN_HEIGHT);
        final int doorwayWidthHalf = random.nextInt(DOORWAY_MAX_WIDTH_HALF);
        final int doorwayLeft = 7 - doorwayWidthHalf;
        final int doorwayRight = 9 + doorwayWidthHalf;
        final int walkwayWidthHalf = random.nextInt(WALKWAY_MAX_WIDTH_HALF);
        final int walkwayLeft = 8 - walkwayWidthHalf;
        final int walkWayRight = 8 + walkwayWidthHalf;
        final boolean hasXWall = random.nextDouble() < WALL_PROBABILITY;
        final boolean hasZWall = random.nextDouble() < WALL_PROBABILITY;
        final boolean hasXWindow = random.nextDouble() < WINDOW_PROBABILITY;
        final boolean hasZWindow = random.nextDouble() < WINDOW_PROBABILITY;
        final boolean hasIsland = random.nextDouble() < ISLAND_PROBABILITY;
        int xWindowLocation = random.nextInt(4 - doorwayWidthHalf) + 1;
        if (random.nextDouble() > 0.5) xWindowLocation = 15 - xWindowLocation;
        int zWindowLocation = random.nextInt(4 - doorwayWidthHalf) + 1;
        if (random.nextDouble() > 0.5) zWindowLocation = 15 - zWindowLocation;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                boolean isSunRoof = x >= 7 && z >= 7 && x <= 9 && z <= 9;
                boolean isWalkway = (x > walkwayLeft && x < walkWayRight) || (z > walkwayLeft && z < walkWayRight);
                if (x == 0 || z == 0) {
                    if ((hasXWall && z == 0) || (hasZWall && x == 0)) {
                        // Walls and doorway
                        boolean isDoorway = (x >= doorwayLeft && x <= doorwayRight) || (z >= doorwayLeft && z <= doorwayRight);
                        for (int y = floorLevel; y <= roofMaxLevel; y++) {
                            if (isDoorway && y <= doorwayLevel && y > floorLevel) continue;
                            chunk.setBlock(x, y, z, Material.QUARTZ_BLOCK);
                        }
                    } else {
                        chunk.setBlock(x, floorLevel, z, Material.QUARTZ_BLOCK);
                        for (int y = roofLevel; y <= roofMaxLevel; y++) {
                            chunk.setBlock(x, y, z, Material.QUARTZ_BLOCK);
                        }
                    }
                } else if (x == 1 || z == 1 || x == 15 || z == 15) {
                    // Border walkway
                    chunk.setBlock(x, floorLevel, z, Material.QUARTZ_BLOCK);
                    chunk.setBlock(x, roofLevel, z, Material.QUARTZ_BLOCK);
                } else if (isWalkway) {
                    // Pathways
                    if (!isSunRoof) {
                        chunk.setBlock(x, roofLevel, z, Material.QUARTZ_BLOCK);
                    }
                    chunk.setBlock(x, floorLevel, z, Material.QUARTZ_BLOCK);
                } else if (isSunRoof) {
                    // Island
                    chunk.setBlock(x, floorLevel, z, Material.QUARTZ_BLOCK);
                    if (!hasIsland) {
                        chunk.setBlock(x, floorLevel, z, Material.WATER);
                    }
                }  else {
                    // Water and roof
                    chunk.setBlock(x, roofLevel, z, Material.QUARTZ_BLOCK);
                    chunk.setBlock(x, floorLevel, z, Material.WATER);
                }

                // Extend ceiling up
                if (!isSunRoof) {
                    for (int y = roofLevel + 1; y <= roofMaxLevel; y++) {
                        chunk.setBlock(x, y, z, Material.QUARTZ_BLOCK);
                    }
                }

                // Fill in windows after
                if (hasXWall && hasXWindow && x == xWindowLocation && z == 0) {
                    BlockData gatewayData = plugin.getServer().createBlockData(Material.END_GATEWAY);
                    chunk.setBlock(x, floorLevel + 2, z, getWindowBlock());
                } else if (hasZWall && hasZWindow && z == zWindowLocation && x == 0) {
                    chunk.setBlock(x, floorLevel + 2, z, getWindowBlock());
                }

                chunk.setBlock(x, BEDROCK_LEVEL + 1, z, Material.QUARTZ_BLOCK);
                chunk.setBlock(x, BEDROCK_LEVEL, z, Material.BEDROCK);
            }
        }
    }
}
