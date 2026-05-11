package com.elmakers.mine.bukkit.plugins.generator;

import org.bukkit.configuration.ConfigurationSection;

import com.elmakers.mine.bukkit.plugins.LiminalWorldPlugin;

public class DarkPoolsGenerator extends PoolsGenerator {
    public DarkPoolsGenerator(LiminalWorldPlugin plugin, ConfigurationSection generalConfig, ConfigurationSection config) {
        super(plugin, generalConfig, config);
    }

    @Override
    protected LiminalPopulator createPopulator(ConfigurationSection config) {
        return new DarkPoolsExitPopulator(plugin, config);
    }

    @Override
    public String getNextLevel() {
        return "ocean";
    }
}
