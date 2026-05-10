package com.elmakers.mine.bukkit.plugins;

import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
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
        getServer().getScheduler().runTaskLater(this, () -> {
            getWorld("pools");
            getWorld("ocean");
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
        final int maxY = world.getMaxHeight();
        switch (world.getName()) {
            case "world_pools":
                return new Location(world, 8.5, PoolsGenerator.FLOOR_LEVEL + 1, 8.5);
            case "world_ocean":
                return new Location(world, 0, maxY - OceanGenerator.SEA_LEVEL + 1, 0);
            default:
                return world.getSpawnLocation();
        }
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
        String worldName = "world_" + level;
        World world = getServer().getWorld(worldName);
        if (world == null) {
            final ChunkGenerator generator;
            switch (level) {
                case "pools":
                    generator = new PoolsGenerator(this);
                    break;
                case "ocean":
                    generator = new OceanGenerator(this);
                    break;
                default:
                    generator = null;
                    break;
            }
            world = Bukkit.createWorld(new WorldCreator(worldName).generator(generator));
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
