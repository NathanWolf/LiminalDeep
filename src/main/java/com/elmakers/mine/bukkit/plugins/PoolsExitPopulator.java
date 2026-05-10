package com.elmakers.mine.bukkit.plugins;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.CommandBlock;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

public class PoolsExitPopulator extends BlockPopulator {
    private static final int EXIT_MIN_DISTANCE_SQUARED = 640 * 640;
    private static final int EXIT_MAX_DISTANCE_SQUARED = 1600 * 1600;
    private static final int BEDROCK_LEVEL = 60;
    private static final int COMMAND_BLOCK_LEVEL = BEDROCK_LEVEL - 2;
    private static final int FLOOR_LEVEL = BEDROCK_LEVEL + 2;
    private static final int EXIT_LEVEL = -32;

    private final LiminalWorldPlugin plugin;

    public PoolsExitPopulator(LiminalWorldPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion region) {
        final int distanceSquared = chunkX * 16 * chunkZ * 16;
        final double exitProbability = distanceSquared < EXIT_MIN_DISTANCE_SQUARED ? 0 :
                (double)(distanceSquared - EXIT_MIN_DISTANCE_SQUARED) / (EXIT_MAX_DISTANCE_SQUARED - EXIT_MIN_DISTANCE_SQUARED);
        final boolean isExit = random.nextDouble() < exitProbability;

        if (!isExit) return;

        BlockData commandBlock = plugin.getServer().createBlockData(Material.REPEATING_COMMAND_BLOCK);
        CommandBlock command = (CommandBlock)commandBlock;
        command.setConditional(false);

        final int chunkGlobalX = chunkX << 4;
        final int chunkGlobalZ = chunkZ << 4;
        final int commandY = COMMAND_BLOCK_LEVEL;
        final int commandX = chunkGlobalX + 4;
        final int commandZ = chunkGlobalZ + 4;
        region.setBlockData(commandX, commandY, commandZ, command);
        BlockState commandState = region.getBlockState(commandX, commandY, commandZ);
        if (commandState instanceof org.bukkit.block.CommandBlock) {
            org.bukkit.block.CommandBlock commandBlockState = (org.bukkit.block.CommandBlock)commandState;
            commandBlockState.setCommand("/particle explosion_emitter ~4 ~1 ~4");
            commandState.update(true);
        }

        BlockData observer1Data = plugin.getServer().createBlockData(Material.OBSERVER);
        BlockData observer2Data = plugin.getServer().createBlockData(Material.OBSERVER);
        if (observer1Data instanceof Directional) {
            ((Directional)observer1Data).setFacing(BlockFace.EAST);
        }
        if (observer2Data instanceof Directional) {
            ((Directional)observer2Data).setFacing(BlockFace.WEST);
        }
        region.setBlockData(commandX + 1, commandY, commandZ, observer1Data);
        region.setBlockData(commandX + 2, commandY, commandZ, observer2Data);

        BlockData airData = plugin.getServer().createBlockData(Material.AIR);
        BlockData quartzData = plugin.getServer().createBlockData(Material.QUARTZ_BLOCK);
        BlockData portalData = plugin.getServer().createBlockData(Material.END_PORTAL);
        BlockData waterData = plugin.getServer().createBlockData(Material.WATER);

        for (int relativeX = 5; relativeX <= 11; relativeX++) {
            for (int relativeZ = 5; relativeZ <= 11; relativeZ++) {
                final int x = chunkGlobalX + relativeX;
                final int z = chunkGlobalZ + relativeZ;

                // Hole in floor
                for (int y = BEDROCK_LEVEL; y <= FLOOR_LEVEL; y++) {
                    region.setBlockData(x, y, z, airData);
                }

                // Shaft downward
                if (relativeX == 5 || relativeZ == 5 || relativeX == 11 || relativeZ == 11) {
                    // Walls
                    for (int y = EXIT_LEVEL; y < FLOOR_LEVEL; y++) {
                        region.setBlockData(x, y, z, quartzData);
                    }

                    // Waterfall
                    region.setBlockData(x, FLOOR_LEVEL, z, waterData);
                }

                // Exit gateway
                region.setBlockData(x, EXIT_LEVEL, z, portalData);
            }
        }
    }

    public static void checkNewChunk(Chunk chunk) {
        Block checkObserver = chunk.getBlock(5, COMMAND_BLOCK_LEVEL, 4);
        if (checkObserver.getType() == Material.OBSERVER) {
            BlockData blockData = checkObserver.getBlockData();
            if (blockData instanceof Powerable) {
                Powerable powerable = (Powerable)blockData;
                powerable.setPowered(true);
                checkObserver.setBlockData(blockData);
            }

            // Trigger waterfall
            chunk.getBlock(6, FLOOR_LEVEL, 6).setType(Material.WATER);
            chunk.getBlock(10, FLOOR_LEVEL, 6).setType(Material.WATER);
            chunk.getBlock(10, FLOOR_LEVEL, 10).setType(Material.WATER);
            chunk.getBlock(6, FLOOR_LEVEL, 10).setType(Material.WATER);
        }
    }
}
