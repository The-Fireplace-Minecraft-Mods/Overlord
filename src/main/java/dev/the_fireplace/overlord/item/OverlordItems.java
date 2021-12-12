package dev.the_fireplace.overlord.item;

import dev.the_fireplace.overlord.entity.OverlordEntities;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static dev.the_fireplace.overlord.Overlord.MODID;

public final class OverlordItems
{
    public static final ArmorMaterial SANS_ARMOR_MATERIAL = new SansArmorMaterial();

    public static final Item OWNED_SKELETON_SPAWN_EGG = new OwnedSkeletonSpawnEggItem(OverlordEntities.OWNED_SKELETON_TYPE, 0xC1C1C1, 0xC76462, new Item.Settings().group(ItemGroup.MISC));
    public static final Item SANS_MASK = new ArmorItem(SANS_ARMOR_MATERIAL, EquipmentSlot.HEAD, new Item.Settings());
    public static final Item ORDERS_WAND = new OrdersWandItem(new Item.Settings().group(ItemGroup.TOOLS).maxCount(1));

    public static void registerItems() {
        registerItem("owned_skeleton_spawn_egg", OWNED_SKELETON_SPAWN_EGG);
        registerItem("sans_mask", SANS_MASK);
        registerItem("orders_wand", ORDERS_WAND);
    }

    private static void registerItem(String path, Item item) {
        Registry.register(Registry.ITEM, new Identifier(MODID, path), item);
    }
}
