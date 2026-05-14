package com.elmakers.mine.bukkit.plugins.liminal.data;

import java.util.List;
import java.util.Random;

import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;

import com.elmakers.mine.bukkit.plugins.liminal.generator.LiminalGenerator;

public class LootTable {
    private final LiminalGenerator generator;
    private final String blockData;
    private final List<String> items;
    private double probability;

    public LootTable(LiminalGenerator generator, ConfigurationSection config) {
        this.generator = generator;
        blockData = config.getString("block");
        items = config.getStringList("items");
        probability = config.getDouble("probability", 1.0);
    }

    public List<String> getItems() {
        return items;
    }

    public boolean isPresent(final Random random) {
        return blockData != null && !blockData.isEmpty() && random.nextDouble() < probability;
    }

    public BlockData getBlockData() {
        return generator.getPlugin().getServer().createBlockData(blockData);
    }
}
