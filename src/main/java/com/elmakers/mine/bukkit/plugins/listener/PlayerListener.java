package com.elmakers.mine.bukkit.plugins.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import com.elmakers.mine.bukkit.plugins.generator.LiminalGenerator;
import com.elmakers.mine.bukkit.plugins.LiminalWorldPlugin;

public class PlayerListener implements Listener {
    private final LiminalWorldPlugin plugin;

    public PlayerListener(LiminalWorldPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        World startingWorld = plugin.getServer().getWorlds().get(0);
        if (world.equals(startingWorld)) {
            if (!plugin.sendToLevel(player, "pools")) {
                plugin.getLogger().warning("Unable to send " + player.getName() + " to starting world");
            } else {
                plugin.getLogger().info("Player " + player.getName() + " sent to starting world");
            }
        }
        player.setGameMode(GameMode.ADVENTURE);
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        LiminalGenerator generator = plugin.getGeneratorByWorld(world.getName());
        if (generator != null) {
            String nextLevel = generator.getNextLevel();
            if (nextLevel != null) {
                Location entryLocation = plugin.getEntryLocation(nextLevel);
                event.setTo(entryLocation);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Location spawnLocation = plugin.getSpawnLocation("pools");
        if (spawnLocation != null) {
            event.getEntity().setRespawnLocation(spawnLocation, true);
        }
    }
}
