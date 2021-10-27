package dev.the_fireplace.overlord.init;

import com.google.common.collect.ImmutableList;
import dev.the_fireplace.overlord.block.BloodSoakedSoil;
import dev.the_fireplace.overlord.block.GraveMarkerBlock;
import dev.the_fireplace.overlord.block.internal.CasketBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static dev.the_fireplace.overlord.Overlord.MODID;

public class OverlordBlocks
{
    public static final Block OAK_CASKET = new CasketBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS));
    public static final Block OAK_GRAVE_MARKER = new GraveMarkerBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS));
    public static final Block BLOOD_SOAKED_SOIL = new BloodSoakedSoil(FabricBlockSettings.of(Material.SOIL)
        .strength(0.5F, 0.1F)
        .sounds(BlockSoundGroup.WET_GRASS)
        .materialColor(MaterialColor.RED)
        .breakByTool(FabricToolTags.SHOVELS));

    public static final ImmutableList<Block> BLOCKS = ImmutableList.of(OAK_CASKET, BLOOD_SOAKED_SOIL, OAK_GRAVE_MARKER);

    public static void registerBlocks() {
        registerBlockWithItem("oak_casket", OAK_CASKET, ItemGroup.DECORATIONS);
        registerBlockWithItem("oak_grave_marker", OAK_GRAVE_MARKER, ItemGroup.DECORATIONS);
        registerBlockWithItem("blood_soaked_soil", BLOOD_SOAKED_SOIL, ItemGroup.BUILDING_BLOCKS);
    }

    private static void registerBlock(String path, Block block) {
        Registry.register(Registry.BLOCK, new Identifier(MODID, path), block);
    }

    private static void registerBlockWithItem(String path, Block block, ItemGroup group) {
        registerBlock(path, block);
        Registry.register(Registry.ITEM, new Identifier(MODID, path), new BlockItem(block, new Item.Settings().group(group)));
    }
}
