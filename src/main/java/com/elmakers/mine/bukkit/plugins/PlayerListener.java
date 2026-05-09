package com.elmakers.mine.bukkit.plugins;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final LiminalWorldPlugin plugin;

    public PlayerListener(LiminalWorldPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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
    }
}
