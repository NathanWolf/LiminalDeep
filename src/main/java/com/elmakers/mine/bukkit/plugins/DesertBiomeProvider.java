package com.elmakers.mine.bukkit.plugins;

import java.util.List;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

public class DesertBiomeProvider extends BiomeProvider {
    @Override
    public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
        return Biome.DESERT;
    }

    @Override
    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
        return List.of(Biome.DESERT);
    }
}
