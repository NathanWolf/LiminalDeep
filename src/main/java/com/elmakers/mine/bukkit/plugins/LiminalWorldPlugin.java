package com.elmakers.mine.bukkit.plugins;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

public class LiminalWorldPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        new LiminalCommandExecutor(this);
    }

    @Override
    public void onDisable() {
        // Put here anything you want to happen when the server stops
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(@NonNull String worldName, String id) {
        getLogger().info("Install pools world generator in world: " + worldName);
        return new PoolsGenerator(this);
    }

    public World getWorld(String level) {
        String worldName = "world_" + level;
        World world = getServer().getWorld(worldName);
        if (world == null) {
            world = Bukkit.createWorld(new WorldCreator(level));
        }
        world.setGameRule(GameRule.ADVANCE_WEATHER, false);
        world.setGameRule(GameRule.ADVANCE_TIME, false);
        world.setGameRule(GameRule.SPAWN_MOBS, false);
        world.setGameRule(GameRule.SPAWN_MONSTERS, false);
        world.setGameRule(GameRule.SPAWN_PHANTOMS, false);
        world.setGameRule(GameRule.SPAWN_PATROLS, false);
        return world;
    }
}
