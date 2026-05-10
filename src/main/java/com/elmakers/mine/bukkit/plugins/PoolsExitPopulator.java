package com.elmakers.mine.bukkit.plugins;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.CommandBlock;
import org.bukkit.block.data.type.Observer;
import org.bukkit.craftbukkit.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.block.CraftCommandBlock;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;

public class PoolsExitPopulator extends BlockPopulator {
    private static final int EXIT_MIN_DISTANCE_SQUARED = 10 * 10;
    private static final int EXIT_MAX_DISTANCE_SQUARED = 20 * 20;
    private static final int BEDROCK_LEVEL = 60;
    private static Field commandBlockAccessor;

    private final LiminalWorldPlugin plugin;

    public PoolsExitPopulator(LiminalWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public static void initialize(LiminalWorldPlugin plugin) {
        try {
            commandBlockAccessor = CraftBlockEntityState.class.getDeclaredField("blockEntity");
            commandBlockAccessor.setAccessible(true);
        } catch (NoSuchFieldException ignore) {
            plugin.getLogger().warning("Unable to access CraftBlockEntityState.blockEntity");
        }
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

        // TOOD "always on"
        int centerX = chunkX << 4;
        int centerZ = chunkZ << 4;
        final int commandY = BEDROCK_LEVEL + 4;
        final int commandX = centerX - 4;
        final int commandZ = centerZ - 4;
        region.setBlockData(commandX, commandY, commandZ, command);
        BlockState commandState = region.getBlockState(commandX, commandY, commandZ);
        if (commandState instanceof org.bukkit.block.CommandBlock) {
            org.bukkit.block.CommandBlock commandBlockState = (org.bukkit.block.CommandBlock)commandState;
            commandBlockState.setCommand("/particle explosion_emitter ~4 ~-1 ~4");
            commandState.update(true);
        }

        // I wish there was an API for this!
        if (commandState instanceof CraftCommandBlock && commandBlockAccessor != null) {
            try {
                CraftCommandBlock craftBlock = (CraftCommandBlock) commandState;
                // And this is also not accessible.. cry
                Object tileEntity = commandBlockAccessor.get(craftBlock);
                if (tileEntity instanceof CommandBlockEntity) {
                    CommandBlockEntity blockEntity = (CommandBlockEntity) tileEntity;
                    blockEntity.setAutomatic(true);
                }
            } catch (Exception ignore) {

            }
        }

        BlockData observer1Data = plugin.getServer().createBlockData(Material.OBSERVER);
        BlockData observer2Data = plugin.getServer().createBlockData(Material.OBSERVER);
        if (observer1Data instanceof Directional) {
            ((Directional)observer1Data).setFacing(BlockFace.WEST);
        }
        if (observer2Data instanceof Directional) {
            ((Directional)observer2Data).setFacing(BlockFace.EAST);
        }
        region.setBlockData(commandX - 1, commandY, commandZ, observer1Data);
        region.setBlockData(commandX - 2, commandY, commandZ, observer2Data);
        



        /*
        if (isExit && x >= 4 && x <= 12 && z >= 4 && z <= 12) {
            if (x == 4 && z == 4) {
            }
            if (x == 4 || x == 12 || z == 4 || z == 12) {
                BlockData water = plugin.getServer().createBlockData(Material.WATER);
                Levelled levelled = (Levelled)water;
                levelled.setLevel(levelled.getMaximumLevel() - 4);
                chunk.setBlock(x, floorLevel, z, water);
                chunk.setBlock(x, BEDROCK_LEVEL + 1, z, Material.QUARTZ_BLOCK);
                chunk.setBlock(x, BEDROCK_LEVEL, z, Material.BEDROCK);
            } else if (x == 5 || x == 11 || z == 5 || z == 11) {
                BlockData water = plugin.getServer().createBlockData(Material.WATER);
                Levelled levelled = (Levelled)water;
                levelled.setLevel(4);
                chunk.setBlock(x, floorLevel, z, water);
                chunk.setBlock(x, floorLevel - 1, z, Material.WATER);
                for (int y = floorLevel - 2; y > 10; y--) {
                    chunk.setBlock(x, y, z, Material.QUARTZ_BLOCK);
                }
            } else {
                BlockData water = plugin.getServer().createBlockData(Material.WATER);
                Levelled levelled = (Levelled)water;
                levelled.setLevel(2);
                chunk.setBlock(x, floorLevel - 1, z, water);
                if (x == 6 || x == 10 || z == 6 || z == 10) {
                    for (int y = floorLevel - 2; y > 10; y--) {
                        chunk.setBlock(x, y, z, Material.WATER);
                    }
                }
                chunk.setBlock(x, 16, z, Material.END_PORTAL);
            }
            continue;
        }
        */
    }
}
