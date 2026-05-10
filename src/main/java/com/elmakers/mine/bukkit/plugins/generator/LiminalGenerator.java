package com.elmakers.mine.bukkit.plugins.generator;

import java.util.Locale;

import org.bukkit.Chunk;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import com.elmakers.mine.bukkit.plugins.LiminalWorldPlugin;

public abstract class LiminalGenerator extends ChunkGenerator {
    protected final String worldName;
    protected final LiminalWorldPlugin plugin;
    protected final int time;

    public LiminalGenerator(LiminalWorldPlugin plugin, ConfigurationSection generalConfig, ConfigurationSection config) {
        this.worldName = config.getString("world");
        time = config.getInt("time", 0);
        this.plugin = plugin;
    }

    public String getWorldName() {
        return worldName;
    }

    public abstract Location getSpawnLocation(World world);

    public Location toNextLevel(Player player) {
        return null;
    }

    public void checkNewChunk(Chunk chunk) {

    }

    private void setGameRule(Registry<GameRule> gameRules, World world, String key, boolean value) {
        GameRule rule = gameRules.get(NamespacedKey.minecraft(key.toLowerCase(Locale.ROOT)));
        if (rule != null) {
            world.setGameRule(rule, value);
        }
    }

    public void configureWorld(World world) {
        final Registry<GameRule> gameRules = plugin.getServer().getRegistry(GameRule.class);

        setGameRule(gameRules, world, "ADVANCE_WEATHER", false);
        setGameRule(gameRules, world, "ADVANCE_TIME", false);
        setGameRule(gameRules, world, "SPAWN_MOBS", false);
        setGameRule(gameRules, world, "SPAWN_MONSTERS", false);
        setGameRule(gameRules, world, "SPAWN_PHANTOMS", false);
        setGameRule(gameRules, world, "SPAWN_PATROLS", false);
        setGameRule(gameRules, world, "COMMAND_BLOCK_OUTPUT", false);
        setGameRule(gameRules, world, "COMMAND_BLOCKS_WORK", true);

        world.setTime(time);
    }
}
