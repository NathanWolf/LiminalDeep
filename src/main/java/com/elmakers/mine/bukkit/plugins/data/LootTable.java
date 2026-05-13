package com.elmakers.mine.bukkit.plugins.data;

import java.util.List;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;

public class LootTable {
    private final List<String> items;
    private double probability;

    public LootTable(ConfigurationSection config) {
        items = config.getStringList("items");
        probability = config.getDouble("probability", 1.0);
    }

    public List<String> getItems() {
        return items;
    }

    public boolean isPresent(final Random random) {
        return random.nextDouble() < probability;
    }
}
