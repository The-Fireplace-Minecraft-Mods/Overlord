package dev.the_fireplace.overlord.item;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.block.OverlordBlocks;
import dev.the_fireplace.overlord.datastructure.SingletonFactory;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import dev.the_fireplace.overlord.loader.CreativeTabHelper;
import dev.the_fireplace.overlord.loader.RegistryHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class OverlordItems
{
    private final ArmorMaterial sansArmorMaterial;
    private final SingletonFactory<Item> ownedSkeletonSpawnEgg;
    private final SingletonFactory<Item> sansMask;
    private final SingletonFactory<Item> ordersWand;
    private final SingletonFactory<Item> fleshSkeletonSkull;
    private final SingletonFactory<Item> fleshMuscleSkeletonSkull;
    private final SingletonFactory<Item> muscleSkeletonSkull;

    private RegistryHelper<Item> itemRegistry = (id, value) -> Registry.register(BuiltInRegistries.ITEM, id, value);

    @Inject
    public OverlordItems(OverlordEntities overlordEntities, OverlordBlocks overlordBlocks) {
        ownedSkeletonSpawnEgg = new SingletonFactory<>(() -> new OwnedSkeletonSpawnEggItem(overlordEntities.getOwnedSkeletonType(), 0xC1C1C1, 0xC76462, new Item.Properties()));
        sansArmorMaterial = new SansArmorMaterial();
        sansMask = new SingletonFactory<>(() -> new ArmorItem(sansArmorMaterial, ArmorItem.Type.HELMET, new Item.Properties()));
        ordersWand = new SingletonFactory<>(() -> new OrdersWandItem(new Item.Properties().stacksTo(1)));
        fleshSkeletonSkull = new SingletonFactory<>(() -> new StandingAndWallBlockItem(
            overlordBlocks.getFleshSkeletonSkull(),
            overlordBlocks.getFleshSkeletonWallSkull(),
            new Item.Properties(),
            Direction.DOWN
        ));
        fleshMuscleSkeletonSkull = new SingletonFactory<>(() -> new StandingAndWallBlockItem(
            overlordBlocks.getFleshMuscleSkeletonSkull(),
            overlordBlocks.getFleshMuscleSkeletonWallSkull(),
            new Item.Properties(),
            Direction.DOWN
        ));
        muscleSkeletonSkull = new SingletonFactory<>(() -> new StandingAndWallBlockItem(
            overlordBlocks.getMuscleSkeletonSkull(),
            overlordBlocks.getMuscleSkeletonWallSkull(),
            new Item.Properties(),
            Direction.DOWN
        ));
    }

    public void registerItems(boolean isForge) {
        CreativeTabHelper creativeTabHelper = OverlordConstants.getInjector().getInstance(CreativeTabHelper.class);
        if (!isForge) {
            Item[] skulls = new Item[]{
                registerItem("flesh_skeleton_skull", fleshSkeletonSkull.get()),
                registerItem("flesh_muscle_skeleton_skull", fleshMuscleSkeletonSkull.get()),
                registerItem("muscle_skeleton_skull", muscleSkeletonSkull.get())
            };
            CreativeModeTabs.allTabs().stream().filter(tab -> tab.contains(new ItemStack(Items.WITHER_SKELETON_SKULL))).findFirst().ifPresent(creativeTabGroup ->
                creativeTabHelper.registerItemAfter(creativeTabGroup, Items.WITHER_SKELETON_SKULL, skulls)
            );
            Item[] spawnEggs = new Item[]{
                registerItem("owned_skeleton_spawn_egg", ownedSkeletonSpawnEgg.get())
            };
            CreativeModeTabs.allTabs().stream().filter(tab -> tab.contains(new ItemStack(Items.WITHER_SKELETON_SPAWN_EGG))).findFirst().ifPresent(creativeTabGroup ->
                creativeTabHelper.registerItemAfter(creativeTabGroup, Items.WITHER_SKELETON_SPAWN_EGG, spawnEggs)
            );
        }
        registerItem("sans_mask", sansMask.get());
        Item[] tools = new Item[]{
            registerItem("orders_wand", ordersWand.get())
        };
        CreativeModeTabs.allTabs().stream().filter(tab -> tab.contains(new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK))).findFirst().ifPresent(creativeTabGroup ->
            creativeTabHelper.registerItemAfter(creativeTabGroup, Items.WARPED_FUNGUS_ON_A_STICK, tools)
        );
    }

    private Item registerItem(String path, Item item) {
        itemRegistry.register(new ResourceLocation(OverlordConstants.MODID, path), item);

        return item;
    }

    public void setItemRegistry(RegistryHelper<Item> itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public ArmorMaterial getSansArmorMaterial() {
        return sansArmorMaterial;
    }

    public Item getOwnedSkeletonSpawnEgg() {
        return ownedSkeletonSpawnEgg.get();
    }

    public Item getSansMask() {
        return sansMask.get();
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
