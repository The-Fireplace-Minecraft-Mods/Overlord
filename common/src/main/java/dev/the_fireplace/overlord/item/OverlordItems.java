package dev.the_fireplace.overlord.item;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.block.OverlordBlocks;
import dev.the_fireplace.overlord.datastructure.SingletonFactory;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import dev.the_fireplace.overlord.loader.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class OverlordItems
{
    private final SingletonFactory<Item> ownedSkeletonSpawnEgg;
    private final SingletonFactory<Item> ordersWand;
    private final SingletonFactory<Item> fleshSkeletonSkull;
    private final SingletonFactory<Item> fleshMuscleSkeletonSkull;
    private final SingletonFactory<Item> muscleSkeletonSkull;

    private RegistryHelper<Item> itemRegistry = (id, value) -> Registry.register(Registry.ITEM, id, value);

    @Inject
    public OverlordItems(OverlordEntities overlordEntities, OverlordBlocks overlordBlocks) {
        ownedSkeletonSpawnEgg = new SingletonFactory<>(() -> new OwnedSkeletonSpawnEggItem(overlordEntities.getOwnedSkeletonType(), 0xC1C1C1, 0xC76462, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
        ordersWand = new SingletonFactory<>(() -> new OrdersWandItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1)));
        fleshSkeletonSkull = new SingletonFactory<>(() -> new StandingAndWallBlockItem(
            overlordBlocks.getFleshSkeletonSkull(),
            overlordBlocks.getFleshSkeletonWallSkull(),
            new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)
        ));
        fleshMuscleSkeletonSkull = new SingletonFactory<>(() -> new StandingAndWallBlockItem(
            overlordBlocks.getFleshMuscleSkeletonSkull(),
            overlordBlocks.getFleshMuscleSkeletonWallSkull(),
            new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)
        ));
        muscleSkeletonSkull = new SingletonFactory<>(() -> new StandingAndWallBlockItem(
            overlordBlocks.getMuscleSkeletonSkull(),
            overlordBlocks.getMuscleSkeletonWallSkull(),
            new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)
        ));
    }

    public void registerItems(boolean isForge) {
        if (!isForge) {
            registerItem("owned_skeleton_spawn_egg", ownedSkeletonSpawnEgg.get());
            registerItem("flesh_skeleton_skull", fleshSkeletonSkull.get());
            registerItem("flesh_muscle_skeleton_skull", fleshMuscleSkeletonSkull.get());
            registerItem("muscle_skeleton_skull", muscleSkeletonSkull.get());
        }
        registerItem("orders_wand", ordersWand.get());
    }

    private void registerItem(String path, Item item) {
        itemRegistry.register(new ResourceLocation(OverlordConstants.MODID, path), item);
    }

    public void setItemRegistry(RegistryHelper<Item> itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public Item getOwnedSkeletonSpawnEgg() {
        return ownedSkeletonSpawnEgg.get();
    }

    public Item getOrdersWand() {
        return ordersWand.get();
    }

    public Item getFleshSkeletonSkull() {
        return fleshSkeletonSkull.get();
    }

    public Item getFleshMuscleSkeletonSkull() {
        return fleshMuscleSkeletonSkull.get();
    }

    public Item getMuscleSkeletonSkull() {
        return muscleSkeletonSkull.get();
    }
}
