package the_fireplace.overlord.fabric.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import the_fireplace.overlord.fabric.item.OwnedSkeletonSpawnEggItem;

import static the_fireplace.overlord.OverlordHelper.MODID;

public class OverlordItems {

    public static final Item OWNED_SKELETON_SPAWN_EGG = new OwnedSkeletonSpawnEggItem(OverlordEntities.OWNED_SKELETON_TYPE, 0xC1C1C1, 0xC76462, new Item.Settings().group(ItemGroup.MISC));
    public static final Item SCORCHED_TORCH = new WallStandingBlockItem(OverlordBlocks.SCORCHED_TORCH, OverlordBlocks.WALL_SCORCHED_TORCH, (new Item.Settings()).group(ItemGroup.DECORATIONS));
    public static final Item TORCH_OF_THE_DEAD = new WallStandingBlockItem(OverlordBlocks.TORCH_OF_THE_DEAD, OverlordBlocks.WALL_TORCH_OF_THE_DEAD, (new Item.Settings()).group(ItemGroup.DECORATIONS));

    public static void registerItems() {
        registerItem("owned_skeleton_spawn_egg", OWNED_SKELETON_SPAWN_EGG);
        registerItem("scorched_torch", SCORCHED_TORCH);
        registerItem("torch_of_the_dead", TORCH_OF_THE_DEAD);
    }

    private static void registerItem(String path, Item item) {
        Registry.register(Registry.ITEM, new Identifier(MODID, path), item);
    }
}
