package com.elmakers.mine.bukkit.plugins;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemGenerator {
    private final LiminalWorldPlugin plugin;

    public ItemGenerator(LiminalWorldPlugin plugin, ConfigurationSection generalConfig) {
        this.plugin = plugin;
    }

    private ItemStack createDivingHelmet() {
        ItemStack itemStack = new ItemStack(Material.CHAINMAIL_HELMET);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("Diving Helmet");
        NamespacedKey modifierKey = new NamespacedKey(plugin, "oxygen");
        itemMeta.addAttributeModifier(
            Attribute.OXYGEN_BONUS,
            new AttributeModifier(
                modifierKey,
                1000000,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.HEAD
            )
        );
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public List<String> getItemKeys() {
        return List.of("diving_helmet");
    }

    public ItemStack createItem(String id) {
        switch (id) {
            case "diving_helmet":
                return createDivingHelmet();
            default:
                plugin.getLogger().warning("Unknown item type: " + id);
                return null;
        }
    }
}
