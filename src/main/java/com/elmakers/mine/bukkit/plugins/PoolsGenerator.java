package com.elmakers.mine.bukkit.plugins;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.EndGateway;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.CaveVines;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
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
    private static final double WALL_PROBABILITY = 0.75;
    private static final double WINDOW_PROBABILITY = 0.3;
    private static final double ISLAND_PROBABILITY = 0.75;
    private static final double POOL_PROBABILITY = 0.75;
    private static final double DOUBLE_DOOR_PROBABILITY = 0.5;
    private static final double FOOD_PROBABILITY = 0.01;
    private final LiminalWorldPlugin plugin;
    private final BiomeProvider biomeProvider;

    public PoolsGenerator(LiminalWorldPlugin plugin) {
        this.plugin = plugin;
        biomeProvider = new DesertBiomeProvider();
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return List.of(new PoolsExitPopulator(plugin));
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

    private void makeFood(int x, int z, int minY, int maxY, @NonNull ChunkData chunk) {
        BlockData foodData = plugin.getServer().createBlockData(Material.CAVE_VINES);
        CaveVines vines = (CaveVines)foodData;
        vines.setBerries(true);
        for (int y = minY; y <= maxY; y++) {
            chunk.setBlock(x, y, z, foodData);
        }
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
        final boolean hasPools = random.nextDouble() < POOL_PROBABILITY;
        int xWindowLocation = random.nextInt(4 - doorwayWidthHalf) + 1;
        if (random.nextDouble() > 0.5) xWindowLocation = 15 - xWindowLocation;
        int zWindowLocation = random.nextInt(4 - doorwayWidthHalf) + 1;
        if (random.nextDouble() > 0.5) zWindowLocation = 15 - zWindowLocation;
        final boolean hasDoubleDoor = random.nextDouble() < DOUBLE_DOOR_PROBABILITY;
        final boolean doorXSide = random.nextDouble() < 0.5;
        final boolean hasXDoor = hasDoubleDoor || doorXSide;
        final boolean hasZDoor = hasDoubleDoor || !doorXSide;
        final boolean hasFood = random.nextDouble() < FOOD_PROBABILITY;
        final int foodCorner = random.nextInt(4);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Fill in the sub-floor first
                chunk.setBlock(x, BEDROCK_LEVEL + 1, z, Material.QUARTZ_BLOCK);
                chunk.setBlock(x, BEDROCK_LEVEL, z, Material.BEDROCK);

                boolean isSunRoof = x >= 7 && z >= 7 && x <= 9 && z <= 9;
                boolean isWalkway = (x > walkwayLeft && x < walkWayRight) || (z > walkwayLeft && z < walkWayRight);
                if (x == 0 || z == 0) {
                    if ((hasXWall && z == 0) || (hasZWall && x == 0)) {
                        // Walls and doorway
                        boolean isDoorway = (x >= doorwayLeft && x <= doorwayRight) || (z >= doorwayLeft && z <= doorwayRight);
                        if (!hasXDoor && z == 0) isDoorway = false;
                        else if (!hasZDoor && x == 0) isDoorway = false;
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
                    if (!hasIsland) {
                        chunk.setBlock(x, floorLevel, z, Material.WATER);
                    } else {
                        chunk.setBlock(x, floorLevel, z, Material.QUARTZ_BLOCK);
                    }
                }  else {
                    // Water and roof
                    chunk.setBlock(x, roofLevel, z, Material.QUARTZ_BLOCK);
                    if (hasPools) {
                        chunk.setBlock(x, floorLevel, z, Material.WATER);
                    } else {
                        chunk.setBlock(x, floorLevel, z, Material.QUARTZ_BLOCK);
                    }
                }

                // Extend ceiling up
                if (!isSunRoof) {
                    for (int y = roofLevel + 1; y <= roofMaxLevel; y++) {
                        chunk.setBlock(x, y, z, Material.QUARTZ_BLOCK);
                    }
                }

                // Add food
                if (hasFood) {
                    switch (foodCorner) {
                        case 0:
                            if (x == 1 && z == 1) {
                                makeFood(x, z, floorLevel + 1, roofLevel - 1, chunk);
                            }
                            break;
                        case 1:
                            if (x == 15 && z == 1) {
                                makeFood(x, z, floorLevel + 1, roofLevel - 1, chunk);
                            }
                            break;
                        case 2:
                            if (x == 1 && z == 15) {
                                makeFood(x, z, floorLevel + 1, roofLevel - 1, chunk);
                            }
                            break;
                        case 3:
                            if (x == 15 && z == 15) {
                                makeFood(x, z, floorLevel + 1, roofLevel - 1, chunk);
                            }
                            break;
                    }
                }

                // Fill in windows after
                if (hasXWall && hasXWindow && x == xWindowLocation && z == 0) {
                    chunk.setBlock(x, floorLevel + 2, z, getWindowBlock());
                } else if (hasZWall && hasZWindow && z == zWindowLocation && x == 0) {
                    chunk.setBlock(x, floorLevel + 2, z, getWindowBlock());
                }
            }
        }
    }
}
