package com.elmakers.mine.bukkit.plugins.tasks;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import com.elmakers.mine.bukkit.plugins.LiminalWorld;

public class StalkerTask implements Consumer<BukkitTask> {
    private final LiminalWorld world;
    private final Map<UUID, WeakReference<Entity>> stalkers = new HashMap<>();

    public StalkerTask(LiminalWorld world) {
        this.world = world;
    }

    protected Entity spawnStalker(Player stalked) {
        Location spawnLocation = stalked.getLocation();
        spawnLocation.setY(0);
        while (spawnLocation.getBlock().getType() != Material.WATER) {
            spawnLocation.add(0, 1, 0);
        }
        spawnLocation.add(0, 4, 0);
        Drowned drowned = (Drowned)world.getWorld().spawnEntity(spawnLocation, EntityType.DROWNED);
        ItemStack stalkerItem = new ItemStack(Material.TORCH);
        ItemMeta itemMeta = stalkerItem.getItemMeta();
        itemMeta.setCustomModelData(8888);
        stalkerItem.setItemMeta(itemMeta);
        drowned.getAttribute(Attribute.SCALE).setBaseValue(10);
        drowned.getEquipment().setHelmet(stalkerItem);
        drowned.addPotionEffect(new PotionEffect(
            PotionEffectType.INVISIBILITY,
            PotionEffect.INFINITE_DURATION,
            1,
            true,
            false,
            false
        ));
        drowned.setTarget(stalked);
        return drowned;

    }

    @Override
    public void accept(BukkitTask bukkitTask) {
        List<Player> players = world.getWorld().getPlayers();
        Set<UUID> worldPlayers = new HashSet<>();
        for (Player player : players) {
            worldPlayers.add(player.getUniqueId());
            WeakReference<Entity> stalkerReference = stalkers.get(player.getUniqueId());
            Entity stalker = stalkerReference == null ? null : stalkerReference.get();
            if (stalker == null || !stalker.isValid()) {
                Entity newStalker = spawnStalker(player);
                stalkers.put(player.getUniqueId(), new WeakReference<>(newStalker));
            }
        }
        Set<UUID> stalkerPlayers = new HashSet<>(stalkers.keySet());
        for (UUID playerId : stalkerPlayers) {
            if (!worldPlayers.contains(playerId)) {
                WeakReference<Entity> stalkerReference = stalkers.get(playerId);
                Entity stalker = stalkerReference.get();
                if (stalker != null && stalker.isValid()) {
                    stalker.remove();
                }
                stalkers.remove(playerId);
            }
        }
    }
}
