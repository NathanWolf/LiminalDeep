package com.elmakers.mine.bukkit.plugins.generator;

import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.elmakers.mine.bukkit.plugins.LiminalWorldPlugin;

public abstract class LiminalGenerator extends ChunkGenerator {
    protected final String worldName;
    protected final LiminalWorldPlugin plugin;
    protected final int time;
    protected final boolean rain;
    protected final BiomeProvider biomeProvider;
    protected final String title;
    protected final String nextLevel;
    protected final int titleDelay;

    public LiminalGenerator(LiminalWorldPlugin plugin, ConfigurationSection generalConfig, ConfigurationSection config) {
        this.plugin = plugin;
        worldName = config.getString("world");
        time = config.getInt("time", 0);
        biomeProvider = createDefaultBiomeProvider(config);
        title = config.getString("title");
        titleDelay = config.getInt("title_delay", generalConfig.getInt("title_delay", 0));
        nextLevel = config.getString("next_level");
        rain = config.getBoolean("rain");
    }

    protected BiomeProvider createDefaultBiomeProvider(ConfigurationSection config) {
        String biomeKey = config.getString("biome");
        try {
            if (biomeKey != null) {
                return new SingleBiomeProvider(Biome.valueOf(biomeKey.toUpperCase(Locale.ROOT)));
            }
        } catch (Exception ex) {
            plugin.getLogger().warning("Invalid biome specified in " + worldName + " config: " + biomeKey);
        }
        return null;
    }

    @Nullable
    public BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
        return biomeProvider;
    }

    public String getWorldName() {
        return worldName;
    }

    public abstract Location getSpawnLocation(World world);

    public Location getEntryLocation(World world) {
        Location location = getSpawnLocation(world);
        location.setY(world.getMaxHeight() - 16);
        return location;
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

    }

    private void setGameRule(Registry<GameRule> gameRules, World world, String key, boolean value) {
        GameRule rule = gameRules.get(NamespacedKey.minecraft(key.toLowerCase(Locale.ROOT)));
        if (rule != null) {
            world.setGameRule(rule, value);
        } else {
            plugin.getLogger().warning("Invalid game rule: " + key);
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

        world.setStorm(rain);
        world.setTime(time);
    }
}
