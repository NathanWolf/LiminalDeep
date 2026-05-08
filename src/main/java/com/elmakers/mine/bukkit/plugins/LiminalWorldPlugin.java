package com.elmakers.mine.bukkit.plugins;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

public class LiminalWorldPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();

        new LiminalCommandExecutor(this);
        pm.registerEvents(new PlayerListener(this), this);
    }

    @Override
    public void onDisable() {
        // Put here anything you want to happen when the server stops
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(@NonNull String worldName, String id) {
        switch (worldName) {
            case "world_pools":
                getLogger().info("Install pools world generator in world: " + worldName);
                return new PoolsGenerator(this);
            case "world_ocean":
                getLogger().info("Install ocean world generator in world: " + worldName);
                return new OceanGenerator(this);
            default:
                getLogger().info("Don't know what to do with world: " + worldName);
        }

        return null;
    }

    public World getWorld(String level) {
        String worldName = "world_" + level;
        World world = getServer().getWorld(worldName);
        if (world == null) {
            world = Bukkit.createWorld(new WorldCreator(worldName));
        }
        world.setGameRule(GameRules.ADVANCE_WEATHER, false);
        world.setGameRule(GameRules.ADVANCE_TIME, false);
        world.setGameRule(GameRules.SPAWN_MOBS, false);
        world.setGameRule(GameRules.SPAWN_MONSTERS, false);
        world.setGameRule(GameRules.SPAWN_PHANTOMS, false);
        world.setGameRule(GameRules.SPAWN_PATROLS, false);

        if (level.equals("ocean")) {
            world.setTime(18000);
        } else {
            world.setTime(6000);
        }
        return world;
    }
}
