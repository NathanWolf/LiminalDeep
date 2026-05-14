package com.elmakers.mine.bukkit.plugins.liminal.loot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;

import com.elmakers.mine.bukkit.plugins.liminal.generator.LiminalGenerator;

public class LootTable {
    private final LiminalGenerator generator;
    private double probability;
    private final List<LootDrop> drops = new ArrayList<>();
    private final double totalWeight;

    public LootTable(LiminalGenerator generator, ConfigurationSection config) {
        this.generator = generator;
        probability = config.getDouble("probability", 1.0);
        double totalWeight = 0;
        final ConfigurationSection dropsConfig = config.getConfigurationSection("drops");
        for (String key : dropsConfig.getKeys(false)) {
            LootDrop drop = new LootDrop(generator, dropsConfig.getConfigurationSection(key));
            drops.add(drop);
            totalWeight += drop.getWeight();
        }
        this.totalWeight = totalWeight;
    }

    public LootDrop getDrop(final Random random) {
        double weight = random.nextDouble() * totalWeight;
        for (LootDrop drop : drops) {
            weight -= drop.getWeight();
            if (weight <= 0) {
                return drop;
            }
        }
        // Should never happen
        return drops.get(drops.size() - 1);
    }

    public boolean isPresent(final Random random) {
        return this.totalWeight > 0 && random.nextDouble() < probability;
    }
}
