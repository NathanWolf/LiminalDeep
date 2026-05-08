package com.elmakers.mine.bukkit.plugins;

import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

public class LiminalWorldPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Put here anything you want to happen when the server starts
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
}
