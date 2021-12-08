package dev.the_fireplace.overlord.block;

import dev.the_fireplace.overlord.block.internal.CasketBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

import static dev.the_fireplace.overlord.Overlord.MODID;

public final class OverlordBlocks
{
    public static final Block OAK_CASKET = new CasketBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS));
    public static final Block OAK_GRAVE_MARKER = new GraveMarkerBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS));
    public static final Block BIRCH_CASKET = new CasketBlock(FabricBlockSettings.copyOf(Blocks.BIRCH_PLANKS));
    public static final Block BIRCH_GRAVE_MARKER = new GraveMarkerBlock(FabricBlockSettings.copyOf(Blocks.BIRCH_PLANKS));
    public static final Block SPRUCE_CASKET = new CasketBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS));
    public static final Block SPRUCE_GRAVE_MARKER = new GraveMarkerBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS));
    public static final Block JUNGLE_CASKET = new CasketBlock(FabricBlockSettings.copyOf(Blocks.JUNGLE_PLANKS));
    public static final Block JUNGLE_GRAVE_MARKER = new GraveMarkerBlock(FabricBlockSettings.copyOf(Blocks.JUNGLE_PLANKS));
    public static final Block ACACIA_CASKET = new CasketBlock(FabricBlockSettings.copyOf(Blocks.ACACIA_PLANKS));
    public static final Block ACACIA_GRAVE_MARKER = new GraveMarkerBlock(FabricBlockSettings.copyOf(Blocks.ACACIA_PLANKS));
    public static final Block DARK_OAK_CASKET = new CasketBlock(FabricBlockSettings.copyOf(Blocks.DARK_OAK_PLANKS));
    public static final Block DARK_OAK_GRAVE_MARKER = new GraveMarkerBlock(FabricBlockSettings.copyOf(Blocks.DARK_OAK_PLANKS));
    public static final Block CRIMSON_CASKET = new CasketBlock(FabricBlockSettings.copyOf(Blocks.CRIMSON_PLANKS));
    public static final Block CRIMSON_GRAVE_MARKER = new GraveMarkerBlock(FabricBlockSettings.copyOf(Blocks.CRIMSON_PLANKS));
    public static final Block WARPED_CASKET = new CasketBlock(FabricBlockSettings.copyOf(Blocks.WARPED_PLANKS));
    public static final Block WARPED_GRAVE_MARKER = new GraveMarkerBlock(FabricBlockSettings.copyOf(Blocks.WARPED_PLANKS));
    public static final Block STONE_TOMBSTONE = new TombstoneBlock(FabricBlockSettings.copyOf(Blocks.STONE));
    public static final Block DIORITE_TOMBSTONE = new TombstoneBlock(FabricBlockSettings.copyOf(Blocks.DIORITE));
    public static final Block GRANITE_TOMBSTONE = new TombstoneBlock(FabricBlockSettings.copyOf(Blocks.GRANITE));
    public static final Block ANDESITE_TOMBSTONE = new TombstoneBlock(FabricBlockSettings.copyOf(Blocks.ANDESITE));
    public static final Block BLACKSTONE_TOMBSTONE = new TombstoneBlock(FabricBlockSettings.copyOf(Blocks.BLACKSTONE));
    public static final Block BLOOD_SOAKED_SOIL = new BloodSoakedSoil(FabricBlockSettings.of(Material.SOIL)
        .strength(0.5F, 0.1F)
        .sounds(BlockSoundGroup.WET_GRASS)
        .materialColor(MapColor.RED)
        .breakByTool(FabricToolTags.SHOVELS));

    private static final List<Block> registeredBlocks = new ArrayList<>();

    public static void registerBlocks() {
        registerBlockWithItem("oak_casket", OAK_CASKET, ItemGroup.DECORATIONS);
        registerBlockWithItem("oak_grave_marker", OAK_GRAVE_MARKER, ItemGroup.DECORATIONS);
        registerBlockWithItem("birch_casket", BIRCH_CASKET, ItemGroup.DECORATIONS);
        registerBlockWithItem("birch_grave_marker", BIRCH_GRAVE_MARKER, ItemGroup.DECORATIONS);
        registerBlockWithItem("jungle_casket", JUNGLE_CASKET, ItemGroup.DECORATIONS);
        registerBlockWithItem("jungle_grave_marker", JUNGLE_GRAVE_MARKER, ItemGroup.DECORATIONS);
        registerBlockWithItem("spruce_casket", SPRUCE_CASKET, ItemGroup.DECORATIONS);
        registerBlockWithItem("spruce_grave_marker", SPRUCE_GRAVE_MARKER, ItemGroup.DECORATIONS);
        registerBlockWithItem("acacia_casket", ACACIA_CASKET, ItemGroup.DECORATIONS);
        registerBlockWithItem("acacia_grave_marker", ACACIA_GRAVE_MARKER, ItemGroup.DECORATIONS);
        registerBlockWithItem("dark_oak_casket", DARK_OAK_CASKET, ItemGroup.DECORATIONS);
        registerBlockWithItem("dark_oak_grave_marker", DARK_OAK_GRAVE_MARKER, ItemGroup.DECORATIONS);
        registerBlockWithItem("warped_casket", WARPED_CASKET, ItemGroup.DECORATIONS);
        registerBlockWithItem("warped_grave_marker", WARPED_GRAVE_MARKER, ItemGroup.DECORATIONS);
        registerBlockWithItem("crimson_casket", CRIMSON_CASKET, ItemGroup.DECORATIONS);
        registerBlockWithItem("crimson_grave_marker", CRIMSON_GRAVE_MARKER, ItemGroup.DECORATIONS);
        registerBlockWithItem("stone_tombstone", STONE_TOMBSTONE, ItemGroup.DECORATIONS);
        registerBlockWithItem("diorite_tombstone", DIORITE_TOMBSTONE, ItemGroup.DECORATIONS);
        registerBlockWithItem("granite_tombstone", GRANITE_TOMBSTONE, ItemGroup.DECORATIONS);
        registerBlockWithItem("andesite_tombstone", ANDESITE_TOMBSTONE, ItemGroup.DECORATIONS);
        registerBlockWithItem("blackstone_tombstone", BLACKSTONE_TOMBSTONE, ItemGroup.DECORATIONS);
        registerBlockWithItem("blood_soaked_soil", BLOOD_SOAKED_SOIL, ItemGroup.BUILDING_BLOCKS);
    }

    private static void registerBlock(String path, Block block) {
        registeredBlocks.add(block);
        Registry.register(Registry.BLOCK, new Identifier(MODID, path), block);
    }

    private static void registerBlockWithItem(String path, Block block, ItemGroup group) {
        registerBlock(path, block);
        Registry.register(Registry.ITEM, new Identifier(MODID, path), new BlockItem(block, new Item.Settings().group(group)));
    }

    public static List<Block> getRegisteredBlocks() {
        return registeredBlocks;
    }
}