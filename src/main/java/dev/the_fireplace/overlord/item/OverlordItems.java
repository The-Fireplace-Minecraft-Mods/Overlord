package dev.the_fireplace.overlord.item;

import dev.the_fireplace.overlord.block.OverlordBlocks;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static dev.the_fireplace.overlord.Overlord.MODID;

public final class OverlordItems
{
    public static final Item OWNED_SKELETON_SPAWN_EGG = new OwnedSkeletonSpawnEggItem(OverlordEntities.OWNED_SKELETON_TYPE, 0xC1C1C1, 0xC76462, new Item.Settings().group(ItemGroup.MISC));
    public static final Item ORDERS_WAND = new OrdersWandItem(new Item.Settings().group(ItemGroup.TOOLS).maxCount(1));
    public static final Item FLESH_SKELETON_SKULL = new WallStandingBlockItem(
        OverlordBlocks.FLESH_SKELETON_SKULL,
        OverlordBlocks.FLESH_SKELETON_WALL_SKULL,
        new Item.Settings().group(ItemGroup.DECORATIONS)
    );
    public static final Item FLESH_MUSCLE_SKELETON_SKULL = new WallStandingBlockItem(
        OverlordBlocks.FLESH_MUSCLE_SKELETON_SKULL,
        OverlordBlocks.FLESH_MUSCLE_SKELETON_WALL_SKULL,
        new Item.Settings().group(ItemGroup.DECORATIONS)
    );
    public static final Item MUSCLE_SKELETON_SKULL = new WallStandingBlockItem(
        OverlordBlocks.MUSCLE_SKELETON_SKULL,
        OverlordBlocks.MUSCLE_SKELETON_WALL_SKULL,
        new Item.Settings().group(ItemGroup.DECORATIONS)
    );

    public static void registerItems() {
        registerItem("owned_skeleton_spawn_egg", OWNED_SKELETON_SPAWN_EGG);
        registerItem("orders_wand", ORDERS_WAND);
        registerItem("flesh_skeleton_skull", FLESH_SKELETON_SKULL);
        registerItem("flesh_muscle_skeleton_skull", FLESH_MUSCLE_SKELETON_SKULL);
        registerItem("muscle_skeleton_skull", MUSCLE_SKELETON_SKULL);
    }

    private static void registerItem(String path, Item item) {
        Registry.register(Registry.ITEM, new Identifier(MODID, path), item);
    }
}
