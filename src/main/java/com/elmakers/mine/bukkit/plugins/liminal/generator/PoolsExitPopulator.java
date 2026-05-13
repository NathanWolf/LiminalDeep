package com.elmakers.mine.bukkit.plugins.liminal.generator;

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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import com.elmakers.mine.bukkit.plugins.liminal.LiminalWorldPlugin;

public class PoolsExitPopulator extends LiminalPopulator {
    private int EXIT_MIN_DISTANCE_SQUARED = 640 * 640;
    private int EXIT_MAX_DISTANCE_SQUARED = 3200 * 3200;
    private double EXIT_MAX_PROBABILITY = 0.3;
    private int BEDROCK_LEVEL = 60;
    private int FLOOR_LEVEL = 62;
    private final int COMMAND_BLOCK_LEVEL;
    private int EXIT_LEVEL = -32;
    private boolean COMMAND_BLOCKS_ENABLED = true;

    public PoolsExitPopulator(LiminalGenerator generator, ConfigurationSection config) {
        super(generator);

        BEDROCK_LEVEL = config.getInt("bedrock_level", BEDROCK_LEVEL);
        FLOOR_LEVEL = config.getInt("floor_level", FLOOR_LEVEL);

        ConfigurationSection exitConfig = config.getConfigurationSection("exit");
        int minDistance = exitConfig.getInt("min_distance", 640);
        int maxDistance = exitConfig.getInt("max_distance", 3200);
        EXIT_MIN_DISTANCE_SQUARED = minDistance * minDistance;
        EXIT_MAX_DISTANCE_SQUARED = maxDistance * maxDistance;
        COMMAND_BLOCKS_ENABLED = exitConfig.getBoolean("command_blocks", COMMAND_BLOCKS_ENABLED);
        EXIT_MAX_PROBABILITY = exitConfig.getDouble("max_probability", EXIT_MAX_PROBABILITY);

        EXIT_LEVEL = exitConfig.getInt("exit_level", EXIT_LEVEL);

        COMMAND_BLOCK_LEVEL = BEDROCK_LEVEL - 2;
    }

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion region) {
        final LiminalWorldPlugin plugin = getPlugin();
        final long distanceSquared = (long)(chunkX * 16) * (chunkX * 16) + (chunkZ * 16) * (chunkZ * 16);
        final double exitProbability = distanceSquared < EXIT_MIN_DISTANCE_SQUARED ? 0 :
                EXIT_MAX_PROBABILITY * Math.min(1.0, (distanceSquared - EXIT_MIN_DISTANCE_SQUARED) / (EXIT_MAX_DISTANCE_SQUARED - EXIT_MIN_DISTANCE_SQUARED));
        final boolean isExit = random.nextDouble() < exitProbability;

        if (!isExit) return;

        final int chunkGlobalX = chunkX << 4;
        final int chunkGlobalZ = chunkZ << 4;

        if (COMMAND_BLOCKS_ENABLED) {
            final int commandY = COMMAND_BLOCK_LEVEL;
            final int commandX = chunkGlobalX + 4;
            final int commandZ = chunkGlobalZ + 4;

            BlockData commandBlock = plugin.getServer().createBlockData(Material.REPEATING_COMMAND_BLOCK);
            CommandBlock command = (CommandBlock)commandBlock;
            command.setConditional(false);

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

            // Sounds
            region.setBlockData(commandX + 3, commandY, commandZ, commandBlock);
            commandState = region.getBlockState(commandX + 3, commandY, commandZ);
            if (commandState instanceof org.bukkit.block.CommandBlock) {
                org.bukkit.block.CommandBlock commandBlockState = (org.bukkit.block.CommandBlock)commandState;
                commandBlockState.setCommand("/playsound minecraft:entity.boat.paddle_water ambient @p ~4 ~1 ~4 10");
                commandState.update(true);
            }

        }
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

    public void checkNewChunk(Chunk chunk) {
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
