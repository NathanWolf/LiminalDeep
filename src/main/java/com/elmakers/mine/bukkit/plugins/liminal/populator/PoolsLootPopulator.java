package com.elmakers.mine.bukkit.plugins.liminal.populator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;

import com.elmakers.mine.bukkit.plugins.liminal.LiminalWorldPlugin;
import com.elmakers.mine.bukkit.plugins.liminal.data.LootTable;
import com.elmakers.mine.bukkit.plugins.liminal.generator.LiminalGenerator;

public class PoolsLootPopulator extends LiminalPopulator {
    private Map<String, LootTable> lootTables = new HashMap<>();
    private int FLOOR_LEVEL = 62;

    public PoolsLootPopulator(LiminalGenerator generator, ConfigurationSection config) {
        super(generator);

        FLOOR_LEVEL = config.getInt("floor_level", FLOOR_LEVEL);

        ConfigurationSection lootConfig = config.getConfigurationSection("loot");
        if (lootConfig != null) {
            for (String key : lootConfig.getKeys(false)) {
                lootTables.put(key, new LootTable(lootConfig.getConfigurationSection(key)));
            }
        }
    }

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion region) {
        final LiminalWorldPlugin plugin = getPlugin();

        LootTable loot = null;
        for (LootTable lootTable : lootTables.values()) {
            if (lootTable.isPresent(random)) {
                loot = lootTable;
                break;
            }
        }

        if (loot == null) return;

        final int chunkGlobalX = chunkX << 4;
        final int chunkGlobalZ = chunkZ << 4;

        final int lootCorner = random.nextInt(4);
        final int lootSide = random.nextInt(2);

        final int lookDirection = lootSide == 0 ? 1 : -1;
        final int y = FLOOR_LEVEL + 2;
        int x;
        int z;
        int deltaX;
        int deltaZ;
        switch (lootCorner) {
            case 0:
                x = 8;
                z = 2;
                deltaX = lookDirection;
                deltaZ = 0;
                break;
            case 1:
                x = 8;
                z = 13;
                deltaX = lookDirection;
                deltaZ = 0;
                break;
            case 2:
                x = 2;
                z = 8;
                deltaX = 0;
                deltaZ = lookDirection;
                break;
            case 3:
                x = 13;
                z = 8;
                deltaX = 0;
                deltaZ = lookDirection;
                break;
            default:
                return;
        }

        BlockState blockState = region.getBlockState(chunkGlobalX + x, y, chunkGlobalZ + z);
        while (x >= 0 && z >= 0 && z <= 15 && x <= 15 && blockState.getType() == Material.AIR) {
            x += deltaX;
            z += deltaZ;
            blockState = region.getBlockState(chunkGlobalX + x, y, chunkGlobalZ + z);
        }
        int barrelX = chunkGlobalX + x;
        int barrelZ = chunkGlobalZ + z;
        BlockData barrelBlock = plugin.getServer().createBlockData(Material.BARREL);
        region.setBlockData(barrelX, y, barrelZ, barrelBlock);

        BlockState barrelState = region.getBlockState(barrelX, y, barrelZ);
        if (barrelState instanceof Container) {
            Container barrel = (Container)barrelState;
            final List<String> items = loot.getItems();
            for (String itemId : items) {
                ItemStack itemStack = plugin.createItem(itemId);
                if (itemStack == null) continue;
                barrel.getInventory().addItem(itemStack);
            }
        }
    }
}
