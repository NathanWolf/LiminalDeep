package com.elmakers.mine.bukkit.plugins.generator;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.EndGateway;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.CaveVines;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Random;

import com.elmakers.mine.bukkit.plugins.LiminalWorldPlugin;

public class PoolsGenerator extends LiminalGenerator {
    private int BEDROCK_LEVEL = 60;
    private int FLOOR_LEVEL = 62;
    private int ROOF_MIN_HEIGHT = 4;
    private int ROOF_MAX_HEIGHT = 10;
    private int DOORWAY_MIN_HEIGHT = 2;
    private int DOORWAY_MAX_HEIGHT = 4;
    private int DOORWAY_MAX_WIDTH_HALF = 3;
    private int WALKWAY_MAX_WIDTH_HALF = 5;
    private int HALLWAY_MAX_WIDTH_HALF = 0;
    private int HALLWAY_MIN_WIDTH_HALF = 0;
    private double WALL_PROBABILITY = 0.75;
    private double WINDOW_PROBABILITY = 0.3;
    private double ISLAND_PROBABILITY = 0.75;
    private double POOL_PROBABILITY = 0.75;
    private double DOUBLE_DOOR_PROBABILITY = 0.5;
    private double FOOD_PROBABILITY = 0.01;
    private double LIGHT_PROBABILITY = 1;
    private final LiminalPopulator exitPopulator;

    private Material[] FLOOR_BLOCKS = {
        Material.BLUE_CONCRETE,
        Material.LIGHT_BLUE_CONCRETE
    };

    private Material[] WALL_BLOCKS = {
        Material.POLISHED_DIORITE,
        Material.DIORITE
    };

    private Material[] CEILING_BLOCKS = {
            Material.POLISHED_DIORITE,
            Material.DIORITE
    };

    private Material[] LIGHT_BLOCKS = {
            Material.SEA_LANTERN
    };

    public PoolsGenerator(LiminalWorldPlugin plugin, ConfigurationSection generalConfig, ConfigurationSection config) {
        super(plugin, generalConfig, config);
        exitPopulator = createPopulator(config);

        BEDROCK_LEVEL = config.getInt("bedrock_level", BEDROCK_LEVEL);
        FLOOR_LEVEL = config.getInt("floor_level", FLOOR_LEVEL);
        ROOF_MIN_HEIGHT = config.getInt("roof_min_height", ROOF_MIN_HEIGHT);
        ROOF_MAX_HEIGHT = config.getInt("roof_max_height", ROOF_MAX_HEIGHT);
        DOORWAY_MIN_HEIGHT = config.getInt("doorway_min_height", DOORWAY_MIN_HEIGHT);
        DOORWAY_MAX_HEIGHT = config.getInt("doorway_max_height", DOORWAY_MAX_HEIGHT);
        DOORWAY_MAX_WIDTH_HALF = config.getInt("doorway_max_width_half", DOORWAY_MAX_WIDTH_HALF);
        WALKWAY_MAX_WIDTH_HALF = config.getInt("walkway_max_width_half", WALKWAY_MAX_WIDTH_HALF);
        WALL_PROBABILITY = config.getDouble("wall_probability", WALL_PROBABILITY);
        WINDOW_PROBABILITY = config.getDouble("window_probability", WINDOW_PROBABILITY);
        ISLAND_PROBABILITY = config.getDouble("island_probability", ISLAND_PROBABILITY);
        POOL_PROBABILITY = config.getDouble("pool_probability", POOL_PROBABILITY);
        DOUBLE_DOOR_PROBABILITY = config.getDouble("double_door_probability", DOUBLE_DOOR_PROBABILITY);
        FOOD_PROBABILITY = config.getDouble("food_probability", FOOD_PROBABILITY);
        LIGHT_PROBABILITY = config.getDouble("light_probability", LIGHT_PROBABILITY);
        HALLWAY_MAX_WIDTH_HALF = config.getInt("hallway_max_width_half", HALLWAY_MAX_WIDTH_HALF);
        HALLWAY_MIN_WIDTH_HALF = config.getInt("hallway_min_width_half", HALLWAY_MIN_WIDTH_HALF);

        FLOOR_BLOCKS = plugin.getMaterials(config, "floor_blocks", FLOOR_BLOCKS);
        WALL_BLOCKS = plugin.getMaterials(config, "wall_blocks", WALL_BLOCKS);
        CEILING_BLOCKS = plugin.getMaterials(config, "ceiling_blocks", CEILING_BLOCKS);
        LIGHT_BLOCKS = plugin.getMaterials(config, "light_blocks", LIGHT_BLOCKS);
    }

    protected LiminalPopulator createPopulator(ConfigurationSection config) {
        return new PoolsExitPopulator(plugin, config);
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return List.of(exitPopulator);
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
        final boolean isStartingChunk = chunkX == 0 && chunkZ == 0;
        final int floorLevel = FLOOR_LEVEL;
        final int roofLevel = floorLevel + random.nextInt(ROOF_MAX_HEIGHT - ROOF_MIN_HEIGHT) + ROOF_MIN_HEIGHT;
        final int roofMaxLevel = floorLevel + ROOF_MAX_HEIGHT;
        final int doorwayLevel = Math.min(roofLevel, floorLevel + random.nextInt(DOORWAY_MAX_HEIGHT - DOORWAY_MIN_HEIGHT) + DOORWAY_MIN_HEIGHT);
        final int doorwayWidthHalf = random.nextInt(DOORWAY_MAX_WIDTH_HALF);
        final int doorwayLeft = 7 - doorwayWidthHalf;
        final int doorwayRight = 9 + doorwayWidthHalf;
        final int walkwayWidthHalf = isStartingChunk ? 0 : random.nextInt(WALKWAY_MAX_WIDTH_HALF);
        final int walkwayLeft = 8 - walkwayWidthHalf;
        final int walkWayRight = 8 + walkwayWidthHalf;
        final boolean hasXWall = random.nextDouble() < WALL_PROBABILITY;
        final boolean hasZWall = random.nextDouble() < WALL_PROBABILITY;
        final boolean hasXWindow = random.nextDouble() < WINDOW_PROBABILITY;
        final boolean hasZWindow = random.nextDouble() < WINDOW_PROBABILITY;
        final boolean hasIsland = !isStartingChunk && random.nextDouble() < ISLAND_PROBABILITY;
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
        final Material floorBlock = FLOOR_BLOCKS[random.nextInt(FLOOR_BLOCKS.length)];
        final Material wallBlock = WALL_BLOCKS[random.nextInt(WALL_BLOCKS.length)];
        final Material ceilingBlock = CEILING_BLOCKS[random.nextInt(CEILING_BLOCKS.length)];
        final Material lightBlock = LIGHT_BLOCKS[random.nextInt(LIGHT_BLOCKS.length)];
        final int lightsFirst = walkwayLeft / 2 + 1;
        final int lightsSecond = 16 - lightsFirst;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                final boolean hasLight = random.nextDouble() < LIGHT_PROBABILITY;
                final Material lightMaterial = hasLight ? lightBlock : floorBlock;

                // Fill in the sub-floor first
                chunk.setBlock(x, BEDROCK_LEVEL, z, Material.BEDROCK);
                for (int y = BEDROCK_LEVEL + 1; y < FLOOR_LEVEL; y++) {
                    chunk.setBlock(x, y, z, floorBlock);
                }

                final boolean isSunRoof = x >= 7 && z >= 7 && x <= 9 && z <= 9;
                final boolean isWalkway = (x > walkwayLeft && x < walkWayRight) || (z > walkwayLeft && z < walkWayRight);
                if (x == 0 || z == 0) {
                    chunk.setBlock(x, floorLevel, z, floorBlock);
                    if ((hasXWall && z == 0) || (hasZWall && x == 0)) {
                        // Walls and doorway
                        boolean isDoorway = (x >= doorwayLeft && x <= doorwayRight) || (z >= doorwayLeft && z <= doorwayRight);
                        if (!hasXDoor && z == 0) isDoorway = false;
                        else if (!hasZDoor && x == 0) isDoorway = false;
                        for (int y = floorLevel + 1; y <= roofMaxLevel; y++) {
                            if (isDoorway && y <= doorwayLevel) continue;
                            chunk.setBlock(x, y, z, wallBlock);
                        }
                    } else {
                        for (int y = roofLevel; y <= roofMaxLevel; y++) {
                            chunk.setBlock(x, y, z, wallBlock);
                        }
                    }
                } else if (x == 1 || z == 1 || x == 15 || z == 15) {
                    // Border walkway
                    chunk.setBlock(x, floorLevel, z, floorBlock);
                    chunk.setBlock(x, roofLevel, z, ceilingBlock);
                } else if (isWalkway) {
                    // Pathways
                    if (!isSunRoof) {
                        chunk.setBlock(x, roofLevel, z, ceilingBlock);
                    }
                    chunk.setBlock(x, floorLevel, z, floorBlock);
                } else if (isSunRoof) {
                    // Island
                    if (!hasIsland) {
                        chunk.setBlock(x, floorLevel, z, Material.WATER);
                        if (x == 8 && z == 8) {
                            chunk.setBlock(x, floorLevel - 1, z, lightMaterial);
                        }
                    } else {
                        chunk.setBlock(x, floorLevel, z, floorBlock);
                    }
                } else {
                    // Water and roof
                    chunk.setBlock(x, roofLevel, z, ceilingBlock);
                    if (hasPools) {
                        chunk.setBlock(x, floorLevel, z, Material.WATER);
                        if ((x == lightsFirst || x == lightsSecond) && (z == lightsFirst || z == lightsSecond)) {
                            chunk.setBlock(x, floorLevel - 1, z, lightMaterial);
                        }
                    } else {
                        chunk.setBlock(x, floorLevel, z, floorBlock);
                    }
                }

                // Extend ceiling up
                if (!isSunRoof) {
                    final int ceilingHeight = isStartingChunk ? worldInfo.getMaxHeight() : roofMaxLevel;
                    for (int y = roofLevel + 1; y <= ceilingHeight; y++) {
                        chunk.setBlock(x, y, z, ceilingBlock);
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

                // Fill in hallways after
                boolean isCenterWalkway = isWalkway || x == 8 || z == 8;
                if (!isCenterWalkway && (HALLWAY_MAX_WIDTH_HALF > 0 || HALLWAY_MIN_WIDTH_HALF > 0)) {
                    int hallwayWidthHalf = random.nextInt(HALLWAY_MAX_WIDTH_HALF - HALLWAY_MIN_WIDTH_HALF) + HALLWAY_MIN_WIDTH_HALF;
                    int hallwayLeft = 8 - hallwayWidthHalf;
                    int hallwayRight = 8 + hallwayWidthHalf;
                    if (isStartingChunk) {
                        hallwayLeft = Math.min(hallwayLeft, 6);
                        hallwayRight = Math.max(hallwayRight, 10);
                    }
                    if (x < hallwayLeft || x > hallwayRight || z < hallwayLeft || z > hallwayRight) {
                        for (int y = floorLevel; y <= roofLevel; y++) {
                            chunk.setBlock(x, y, z, wallBlock);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Location getSpawnLocation(World world) {
        return new Location(world, 8.5, FLOOR_LEVEL + 1, 8.5);
    }

    @Override
    public void checkNewChunk(Chunk chunk) {
        exitPopulator.checkNewChunk(chunk);
    }
}
