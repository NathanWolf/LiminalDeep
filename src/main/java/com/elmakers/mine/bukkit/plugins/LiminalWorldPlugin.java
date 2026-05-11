package com.elmakers.mine.bukkit.plugins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.elmakers.mine.bukkit.plugins.generator.LiminalCommandExecutor;
import com.elmakers.mine.bukkit.plugins.generator.LiminalGenerator;
import com.elmakers.mine.bukkit.plugins.generator.OceanGenerator;
import com.elmakers.mine.bukkit.plugins.generator.PoolsGenerator;
import com.elmakers.mine.bukkit.plugins.listener.ChunkListener;
import com.elmakers.mine.bukkit.plugins.listener.PlayerListener;

public class LiminalWorldPlugin extends JavaPlugin implements Listener {
    private static int CURRENT_VERSION = 1;

    private Map<String, LiminalGenerator> generators = new HashMap();
    private Map<String, LiminalGenerator> worldGenerators = new HashMap();
    private LiminalCommandExecutor commandExecutor;
    private PlayerListener playerListener;
    private ChunkListener chunkListener;

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        saveDefaultConfig();
        ConfigurationSection configuration = getConfig();
        ConfigurationSection generalConfig = configuration.getConfigurationSection("general");
        if (generalConfig == null || generalConfig.getInt("version", 0) < CURRENT_VERSION) {
            getLogger().severe("Plugin configuration is outdated. Disabling plugin. Please regenerate the config, make a copy first if you want to add any edits you've made.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        ConfigurationSection poolsConfig = configuration.getConfigurationSection("pools");
        ConfigurationSection darkPoolsConfig = configuration.getConfigurationSection("dark_pools");
        ConfigurationSection oceanConfig = configuration.getConfigurationSection("ocean");

        generators.put("pools", new PoolsGenerator(this, generalConfig, poolsConfig));
        generators.put("dark_pools", new PoolsGenerator(this, generalConfig, darkPoolsConfig));
        generators.put("ocean", new OceanGenerator(this, generalConfig, oceanConfig));

        for (Map.Entry<String, LiminalGenerator> entry : generators.entrySet()) {
            worldGenerators.put(entry.getValue().getWorldName(), entry.getValue());
        }

        commandExecutor = new LiminalCommandExecutor(this);
        playerListener = new PlayerListener(this);
        pm.registerEvents(playerListener, this);
        chunkListener = new ChunkListener(this);
        pm.registerEvents(chunkListener, this);
        getServer().getScheduler().runTaskLater(this, () -> {
            for (String worldName : generators.keySet()) {
                getWorld(worldName);
            }
        }, 1L);
    }

    @Override
    public void onDisable() {
        // Put here anything you want to happen when the server stops
    }

    public Location getSpawnLocation(String level) {
        World poolsWorld = getWorld(level);
        if (poolsWorld == null) {
            return null;
        }
        return getSpawnLocation(poolsWorld);
    }

    public Location getSpawnLocation(World world) {
        final LiminalGenerator generator = worldGenerators.get(world.getName());
        if (generator == null) {
            return world.getSpawnLocation();
        }
        return generator.getSpawnLocation(world);
    }

    public Location getEntryLocation(String level) {
        World poolsWorld = getWorld(level);
        if (poolsWorld == null) {
            return null;
        }
        return getEntryLocation(poolsWorld);
    }

    public Location getEntryLocation(World world) {
        final LiminalGenerator generator = worldGenerators.get(world.getName());
        if (generator == null) {
            return world.getSpawnLocation();
        }
        return generator.getEntryLocation(world);
    }

    public boolean sendToLevel(Player player, String level) {
        Location spawnLocation = getSpawnLocation(level);
        if (spawnLocation == null) {
            return false;
        }
        player.teleport(spawnLocation);
        return true;
    }

    public World getWorld(String level) {
        final LiminalGenerator generator = generators.get(level);
        if (generator == null) {
            return null;
        }
        String worldName = generator.getWorldName();
        World world = getServer().getWorld(worldName);
        if (world == null) {
            world = Bukkit.createWorld(new WorldCreator(worldName).generator(generator));
        }
        if (world == null) {
            return null;
        }
        generator.configureWorld(world);
        return world;
    }

    public LiminalGenerator getGeneratorByWorld(String worldName) {
        return worldGenerators.get(worldName);
    }

    public Material[] getMaterials(ConfigurationSection config, String key, Material[] defaults) {
        List<String> materialNames = config.getStringList(key);
        if (materialNames == null || materialNames.isEmpty()) {
            return defaults;
        }
        Material[] materials = new Material[materialNames.size()];
        for (int i = 0; i < materialNames.size(); i++) {
            String materialName = materialNames.get(i);
            materials[i] = Material.matchMaterial(materialName);
        }
        return materials;
    }

    public void checkNewChunk(Chunk chunk) {
        LiminalGenerator generator = worldGenerators.get(chunk.getWorld().getName());
        if (generator != null) {
            generator.checkNewChunk(chunk);
        }
    }
}
