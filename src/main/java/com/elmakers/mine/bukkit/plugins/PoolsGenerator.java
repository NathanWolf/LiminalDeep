package com.elmakers.mine.bukkit.plugins;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class PoolsGenerator extends ChunkGenerator {
    private static final int SEA_LEVEL = 64;
    private static final int SAND_LEVEL = 6;
    private static final int BEDROCK_LAYER = 1;
    private static final int ISLAND_LAYER = 60;
    private final LiminalWorldPlugin plugin;

    public PoolsGenerator(LiminalWorldPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = SEA_LEVEL; y > ISLAND_LAYER; y--) {

                    boolean ocean =true;
                    int wx = Math.abs(x+chunkX*16);
                    int wz = Math.abs(z+chunkZ*16);
                    wx = wx % 500;
                    wz = wz % 500;
                    if (wx>450 && wx<499 && wz>450 && wz<499)
                        ocean=false;
                    if (ocean)
                        chunk.setBlock(x, y, z, Material.WATER);
                    else
                        chunk.setBlock(x, y, z, Material.SMOOTH_SANDSTONE);
                }
                for (int y = ISLAND_LAYER; y > SAND_LEVEL; y--) {
                    chunk.setBlock(x, y, z, Material.WATER);
                }
                for (int y = SAND_LEVEL; y >= BEDROCK_LAYER; y--) {
                    chunk.setBlock(x, y, z, Material.SAND);
                }
                chunk.setBlock(x, 0, z, Material.BEDROCK);
            }
        }
        return chunk;
    }
}
