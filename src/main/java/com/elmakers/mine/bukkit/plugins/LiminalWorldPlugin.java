package com.elmakers.mine.bukkit.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.elmakers.mine.bukkit.plugins.generator.LiminalGenerator;
import com.elmakers.mine.bukkit.plugins.generator.OceanGenerator;
import com.elmakers.mine.bukkit.plugins.generator.PoolsGenerator;
import com.elmakers.mine.bukkit.plugins.listener.ChunkListener;
import com.elmakers.mine.bukkit.plugins.listener.PlayerListener;

public class LiminalWorldPlugin extends JavaPlugin implements Listener {
    private static int CURRENT_VERSION = 1;

    private Map<String, LiminalWorld> worlds = new HashMap();
    private LiminalCommandExecutor commandExecutor;
    private PlayerListener playerListener;
    private ChunkListener chunkListener;
    private String defaultWorld;
    private ItemGenerator itemGenerator;

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
        defaultWorld = generalConfig.getString("default_world");

        ConfigurationSection worldConfigs = configuration.getConfigurationSection("worlds");
        for (String key : worldConfigs.getKeys(false)) {
            LiminalWorld world = new LiminalWorld(this, key, generalConfig, worldConfigs.getConfigurationSection(key));
            worlds.put(key, world);
        }

        ConfigurationSection itemConfigs = configuration.getConfigurationSection("items");
        itemGenerator = new ItemGenerator(this, generalConfig, itemConfigs);

        commandExecutor = new LiminalCommandExecutor(this);
        playerListener = new PlayerListener(this);
        pm.registerEvents(playerListener, this);
        chunkListener = new ChunkListener(this);
        pm.registerEvents(chunkListener, this);
        getServer().getScheduler().runTaskLater(this, () -> {
            for (String worldName : worlds.keySet()) {
                getWorld(worldName).getWorld();
            }
        }, 1L);
    }

    @Override
    public void onDisable() {
    }

    public Location getSpawnLocation(String worldName) {
        LiminalWorld liminalWorld = getWorld(worldName);
        if (liminalWorld == null) {
            return null;
        }
        return liminalWorld.getSpawnLocation();
    }

    public Location getEntryLocation(String worldName) {
        LiminalWorld world = getWorld(worldName);
        if (world == null) {
            return null;
        }
        return world.getEntryLocation();
    }

    public boolean sendToLevel(Player player, String worldName) {
        Location spawnLocation = getSpawnLocation(worldName);
        if (spawnLocation == null) {
            return false;
        }
        player.teleport(spawnLocation);
        return true;
    }

    public LiminalWorld getWorld(String worldName) {
        return worlds.get(worldName);
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
        LiminalWorld liminalWorld = getWorld(chunk.getWorld().getName());
        if (liminalWorld != null) {
            liminalWorld.checkNewChunk(chunk);
        }
    }

    public List<LiminalGenerator> getWorlds() {
        return new ArrayList(worlds.values());
    }

    public List<String> getWorldKeys() {
        return new ArrayList(worlds.keySet());
    }

    public LiminalGenerator createGenerator(LiminalWorld world, String key, ConfigurationSection generalConfig, ConfigurationSection config) {
        switch (key) {
            case "pools": return new PoolsGenerator(world, generalConfig, config);
            case "ocean": return new OceanGenerator(world, generalConfig, config);
            default:
                getLogger().severe("Unknown generator type: " + key);
                return null;
        }
    }

    public LiminalWorld getDefaultWorld() {
        return getWorld(defaultWorld);
    }

    public ItemStack createItem(String key) {
        return itemGenerator.createItem(key);
    }

    public List<String> getItemKeys() {
        return itemGenerator.getItemKeys();
    }
}
