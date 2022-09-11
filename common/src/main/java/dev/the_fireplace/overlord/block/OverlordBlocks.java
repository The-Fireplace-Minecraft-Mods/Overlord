package dev.the_fireplace.overlord.block;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.block.internal.CasketBlock;
import dev.the_fireplace.overlord.datastructure.SingletonFactory;
import dev.the_fireplace.overlord.loader.BlockHelper;
import dev.the_fireplace.overlord.loader.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public final class OverlordBlocks
{
    private final SingletonFactory<Block> oakCasket;
    private final SingletonFactory<Block> oakGraveMarker;
    private final SingletonFactory<Block> birchCasket;
    private final SingletonFactory<Block> birchGraveMarker;
    private final SingletonFactory<Block> spruceCasket;
    private final SingletonFactory<Block> spruceGraveMarker;
    private final SingletonFactory<Block> jungleCasket;
    private final SingletonFactory<Block> jungleGraveMarker;
    private final SingletonFactory<Block> acaciaCasket;
    private final SingletonFactory<Block> acaciaGraveMarker;
    private final SingletonFactory<Block> darkOakCasket;
    private final SingletonFactory<Block> darkOakGraveMarker;

    private final SingletonFactory<Block> stoneTombstone;
    private final SingletonFactory<Block> dioriteTombstone;
    private final SingletonFactory<Block> graniteTombstone;
    private final SingletonFactory<Block> andesiteTombstone;
    private final SingletonFactory<Block> bloodSoakedSoil;
    private final SingletonFactory<Block> fleshSkeletonSkull;
    private final SingletonFactory<Block> fleshSkeletonWallSkull;
    private final SingletonFactory<Block> muscleSkeletonSkull;
    private final SingletonFactory<Block> muscleSkeletonWallSkull;
    private final SingletonFactory<Block> fleshMuscleSkeletonSkull;
    private final SingletonFactory<Block> fleshMuscleSkeletonWallSkull;

    private final List<Block> registeredBlocks = new ArrayList<>();

    private RegistryHelper<Block> blockRegistry = (id, value) -> Registry.register(Registry.BLOCK, id, value);

    private RegistryHelper<Item> itemRegistry = (id, value) -> Registry.register(Registry.ITEM, id, value);

    private RegistryType registryType;

    @Inject
    public OverlordBlocks(BlockHelper blockHelper) {
        oakCasket = new SingletonFactory<>(() -> new CasketBlock(blockHelper.copyProperties(Blocks.OAK_PLANKS)));
        oakGraveMarker = new SingletonFactory<>(() -> new GraveMarkerBlock(blockHelper.copyProperties(Blocks.OAK_PLANKS)));
        birchCasket = new SingletonFactory<>(() -> new CasketBlock(blockHelper.copyProperties(Blocks.BIRCH_PLANKS)));
        birchGraveMarker = new SingletonFactory<>(() -> new GraveMarkerBlock(blockHelper.copyProperties(Blocks.BIRCH_PLANKS)));
        spruceCasket = new SingletonFactory<>(() -> new CasketBlock(blockHelper.copyProperties(Blocks.SPRUCE_PLANKS)));
        spruceGraveMarker = new SingletonFactory<>(() -> new GraveMarkerBlock(blockHelper.copyProperties(Blocks.SPRUCE_PLANKS)));
        jungleCasket = new SingletonFactory<>(() -> new CasketBlock(blockHelper.copyProperties(Blocks.JUNGLE_PLANKS)));
        jungleGraveMarker = new SingletonFactory<>(() -> new GraveMarkerBlock(blockHelper.copyProperties(Blocks.JUNGLE_PLANKS)));
        acaciaCasket = new SingletonFactory<>(() -> new CasketBlock(blockHelper.copyProperties(Blocks.ACACIA_PLANKS)));
        acaciaGraveMarker = new SingletonFactory<>(() -> new GraveMarkerBlock(blockHelper.copyProperties(Blocks.ACACIA_PLANKS)));
        darkOakCasket = new SingletonFactory<>(() -> new CasketBlock(blockHelper.copyProperties(Blocks.DARK_OAK_PLANKS)));
        darkOakGraveMarker = new SingletonFactory<>(() -> new GraveMarkerBlock(blockHelper.copyProperties(Blocks.DARK_OAK_PLANKS)));
        stoneTombstone = new SingletonFactory<>(() -> new TombstoneBlock(blockHelper.copyProperties(Blocks.STONE)));
        dioriteTombstone = new SingletonFactory<>(() -> new TombstoneBlock(blockHelper.copyProperties(Blocks.DIORITE)));
        graniteTombstone = new SingletonFactory<>(() -> new TombstoneBlock(blockHelper.copyProperties(Blocks.GRANITE)));
        andesiteTombstone = new SingletonFactory<>(() -> new TombstoneBlock(blockHelper.copyProperties(Blocks.ANDESITE)));
        bloodSoakedSoil = new SingletonFactory<>(() -> new BloodSoakedSoil(blockHelper.createProperties(Material.DIRT)
            .strength(0.5F, 0.1F)));
        fleshSkeletonSkull = new SingletonFactory<>(() -> new ArmySkullBlock(AbstractArmySkullBlock.SkullType.SKIN_SKELETON, blockHelper.copyProperties(Blocks.SKELETON_SKULL)));
        fleshSkeletonWallSkull = new SingletonFactory<>(() -> new WallArmySkullBlock(AbstractArmySkullBlock.SkullType.SKIN_SKELETON, blockHelper.copyProperties(Blocks.SKELETON_WALL_SKULL)));
        muscleSkeletonSkull = new SingletonFactory<>(() -> new ArmySkullBlock(AbstractArmySkullBlock.SkullType.MUSCLE_SKELETON, blockHelper.copyProperties(Blocks.SKELETON_SKULL)));
        muscleSkeletonWallSkull = new SingletonFactory<>(() -> new WallArmySkullBlock(AbstractArmySkullBlock.SkullType.MUSCLE_SKELETON, blockHelper.copyProperties(Blocks.SKELETON_WALL_SKULL)));
        fleshMuscleSkeletonSkull = new SingletonFactory<>(() -> new ArmySkullBlock(AbstractArmySkullBlock.SkullType.MUSCLE_SKIN_SKELETON, blockHelper.copyProperties(Blocks.PLAYER_HEAD)));
        fleshMuscleSkeletonWallSkull = new SingletonFactory<>(() -> new WallArmySkullBlock(AbstractArmySkullBlock.SkullType.MUSCLE_SKIN_SKELETON, blockHelper.copyProperties(Blocks.PLAYER_WALL_HEAD)));
    }

    public synchronized void registerBlocks(RegistryType registryType) {
        this.registryType = registryType;
        registerBlockWithItem("oak_casket", oakCasket.get(), CreativeModeTab.TAB_DECORATIONS);
        registerBlockWithItem("oak_grave_marker", oakGraveMarker.get(), CreativeModeTab.TAB_DECORATIONS);
        registerBlockWithItem("birch_casket", birchCasket.get(), CreativeModeTab.TAB_DECORATIONS);
        registerBlockWithItem("birch_grave_marker", birchGraveMarker.get(), CreativeModeTab.TAB_DECORATIONS);
        registerBlockWithItem("jungle_casket", jungleCasket.get(), CreativeModeTab.TAB_DECORATIONS);
        registerBlockWithItem("jungle_grave_marker", jungleGraveMarker.get(), CreativeModeTab.TAB_DECORATIONS);
        registerBlockWithItem("spruce_casket", spruceCasket.get(), CreativeModeTab.TAB_DECORATIONS);
        registerBlockWithItem("spruce_grave_marker", spruceGraveMarker.get(), CreativeModeTab.TAB_DECORATIONS);
        registerBlockWithItem("acacia_casket", acaciaCasket.get(), CreativeModeTab.TAB_DECORATIONS);
        registerBlockWithItem("acacia_grave_marker", acaciaGraveMarker.get(), CreativeModeTab.TAB_DECORATIONS);
        registerBlockWithItem("dark_oak_casket", darkOakCasket.get(), CreativeModeTab.TAB_DECORATIONS);
        registerBlockWithItem("dark_oak_grave_marker", darkOakGraveMarker.get(), CreativeModeTab.TAB_DECORATIONS);
        registerBlockWithItem("stone_tombstone", stoneTombstone.get(), CreativeModeTab.TAB_DECORATIONS);
        registerBlockWithItem("diorite_tombstone", dioriteTombstone.get(), CreativeModeTab.TAB_DECORATIONS);
        registerBlockWithItem("granite_tombstone", graniteTombstone.get(), CreativeModeTab.TAB_DECORATIONS);
        registerBlockWithItem("andesite_tombstone", andesiteTombstone.get(), CreativeModeTab.TAB_DECORATIONS);
        registerBlockWithItem("blood_soaked_soil", bloodSoakedSoil.get(), CreativeModeTab.TAB_BUILDING_BLOCKS);
        registerBlock("flesh_skeleton_skull", fleshSkeletonSkull.get());
        registerBlock("flesh_skeleton_wall_skull", fleshSkeletonWallSkull.get());
        registerBlock("muscle_skeleton_skull", muscleSkeletonSkull.get());
        registerBlock("muscle_skeleton_wall_skull", muscleSkeletonWallSkull.get());
        registerBlock("flesh_muscle_skeleton_skull", fleshMuscleSkeletonSkull.get());
        registerBlock("flesh_muscle_skeleton_wall_skull", fleshMuscleSkeletonWallSkull.get());
    }

    private void registerBlock(String path, Block block) {
        if (registryType != RegistryType.ITEM) {
            registeredBlocks.add(block);
            blockRegistry.register(new ResourceLocation(OverlordConstants.MODID, path), block);
        }
    }

    private void registerBlockWithItem(String path, Block block, CreativeModeTab group) {
        registerBlock(path, block);
        if (registryType != RegistryType.BLOCK) {
            itemRegistry.register(new ResourceLocation(OverlordConstants.MODID, path), new BlockItem(block, new Item.Properties().tab(group)));
        }
    }

    public void setBlockRegistry(RegistryHelper<Block> blockRegistry) {
        this.blockRegistry = blockRegistry;
    }

    public void setItemRegistry(RegistryHelper<Item> itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public List<Block> getRegisteredBlocks() {
        return registeredBlocks;
    }

    public Block getOakCasket() {
        return oakCasket.get();
    }

    public Block getOakGraveMarker() {
        return oakGraveMarker.get();
    }

    public Block getBirchCasket() {
        return birchCasket.get();
    }

    public Block getBirchGraveMarker() {
        return birchGraveMarker.get();
    }

    public Block getSpruceCasket() {
        return spruceCasket.get();
    }

    public Block getSpruceGraveMarker() {
        return spruceGraveMarker.get();
    }

    public Block getJungleCasket() {
        return jungleCasket.get();
    }

    public Block getJungleGraveMarker() {
        return jungleGraveMarker.get();
    }

    public Block getAcaciaCasket() {
        return acaciaCasket.get();
    }

    public Block getAcaciaGraveMarker() {
        return acaciaGraveMarker.get();
    }

    public Block getDarkOakCasket() {
        return darkOakCasket.get();
    }

    public Block getDarkOakGraveMarker() {
        return darkOakGraveMarker.get();
    }

    public Block getStoneTombstone() {
        return stoneTombstone.get();
    }

    public Block getDioriteTombstone() {
        return dioriteTombstone.get();
    }

    public Block getGraniteTombstone() {
        return graniteTombstone.get();
    }

    public Block getAndesiteTombstone() {
        return andesiteTombstone.get();
    }

    public Block getBloodSoakedSoil() {
        return bloodSoakedSoil.get();
    }

    public Block getFleshSkeletonSkull() {
        return fleshSkeletonSkull.get();
    }

    public Block getFleshSkeletonWallSkull() {
        return fleshSkeletonWallSkull.get();
    }

    public Block getMuscleSkeletonSkull() {
        return muscleSkeletonSkull.get();
    }

    public Block getMuscleSkeletonWallSkull() {
        return muscleSkeletonWallSkull.get();
    }

    public Block getFleshMuscleSkeletonSkull() {
        return fleshMuscleSkeletonSkull.get();
    }

    public Block getFleshMuscleSkeletonWallSkull() {
        return fleshMuscleSkeletonWallSkull.get();
    }

    public enum RegistryType
    {
        BOTH,
        BLOCK,
        ITEM,
    }
}
