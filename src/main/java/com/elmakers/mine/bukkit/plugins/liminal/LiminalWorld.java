package com.elmakers.mine.bukkit.plugins.liminal;

import java.util.Locale;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.elmakers.mine.bukkit.plugins.liminal.generator.LiminalGenerator;

public class LiminalWorld {
    protected final String name;
    protected final LiminalWorldPlugin plugin;
    protected final LiminalGenerator generator;
    protected final int time;
    protected final boolean rain;
    protected final String title;
    protected final String nextLevel;
    protected final int titleDelay;
    private World world;

    public LiminalWorld(LiminalWorldPlugin plugin, String name, ConfigurationSection generalConfig, ConfigurationSection config) {
        this.plugin = plugin;
        this.name = name;
        time = config.getInt("time", 0);
        title = config.getString("title");
        titleDelay = config.getInt("title_delay", generalConfig.getInt("title_delay", 0));
        nextLevel = config.getString("next_level");
        rain = config.getBoolean("rain");
        generator = createGenerator(generalConfig, config);
    }

    protected LiminalGenerator createGenerator(ConfigurationSection generalConfig, ConfigurationSection config) {
        return plugin.createGenerator(this, config.getString("generator"), generalConfig, config);
    }

    public String getNextLevel() {
        return nextLevel;
    }

    public void enter(Player player) {
        plugin.getServer().getScheduler().runTaskLater(
                plugin,
                () -> player.sendTitle(
                        ChatColor.translateAlternateColorCodes('&', title),
                        null,
                        2 * 20,
                        4 * 20,
                        2 * 20
                ),
                titleDelay * 20 / 1000);
    }

    public void checkNewChunk(Chunk chunk) {
        generator.checkNewChunk(chunk);
    }

    public Location getSpawnLocation() {
        return generator.getSpawnLocation(getWorld());
    }

    public Location getEntryLocation() {
        return generator.getEntryLocation(getWorld());
    }

    public Logger getLogger() {
        return plugin.getLogger();
    }

    public LiminalWorldPlugin getPlugin() {
        return plugin;
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        if (world == null) {
            world = plugin.getServer().getWorld(name);
            if (world == null) {
                world = Bukkit.createWorld(new WorldCreator(name).generator(generator));
            }
            if (world == null) {
                plugin.getLogger().severe("Unable to create world " + name);
            } else {
                configureWorld(world);
            }
        }
        return world;
    }

    private void setGameRule(Registry<GameRule> gameRules, World world, String key, boolean value) {
        GameRule rule = gameRules.get(NamespacedKey.minecraft(key.toLowerCase(Locale.ROOT)));
        if (rule != null) {
            world.setGameRule(rule, value);
        } else {
            plugin.getLogger().warning("Invalid game rule: " + key);
        }
    }

    private void configureWorld(World world) {
        final Registry<GameRule> gameRules = plugin.getServer().getRegistry(GameRule.class);

        setGameRule(gameRules, world, "ADVANCE_WEATHER", false);
        setGameRule(gameRules, world, "ADVANCE_TIME", false);
        setGameRule(gameRules, world, "SPAWN_MOBS", false);
        setGameRule(gameRules, world, "SPAWN_MONSTERS", false);
        setGameRule(gameRules, world, "SPAWN_PHANTOMS", false);
        setGameRule(gameRules, world, "SPAWN_PATROLS", false);
        setGameRule(gameRules, world, "COMMAND_BLOCK_OUTPUT", false);
        setGameRule(gameRules, world, "COMMAND_BLOCKS_WORK", true);

        world.setStorm(rain);
        world.setTime(time);
    }
}
