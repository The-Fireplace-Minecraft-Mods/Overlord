package dev.the_fireplace.overlord.datagen;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.block.OverlordBlocks;
import dev.the_fireplace.overlord.item.OverlordItems;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BedPart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class BlockLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>>
{
    private static final Set<Item> EXPLOSION_IMMUNE = Stream.of(Blocks.DRAGON_EGG, Blocks.BEACON, Blocks.CONDUIT, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.CREEPER_HEAD, Blocks.DRAGON_HEAD, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX).map(ItemConvertible::asItem).collect(ImmutableSet.toImmutableSet());
    ;
    private final Map<Identifier, LootTable.Builder> lootTables = Maps.newHashMap();

    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> identifierBuilderBiConsumer) {
        registerForSelfDrop(OverlordBlocks.BLOOD_SOAKED_SOIL);
        registerForSelfDrop(OverlordBlocks.OAK_GRAVE_MARKER);
        registerForSelfDrop(OverlordBlocks.BIRCH_GRAVE_MARKER);
        registerForSelfDrop(OverlordBlocks.JUNGLE_GRAVE_MARKER);
        registerForSelfDrop(OverlordBlocks.SPRUCE_GRAVE_MARKER);
        registerForSelfDrop(OverlordBlocks.ACACIA_GRAVE_MARKER);
        registerForSelfDrop(OverlordBlocks.DARK_OAK_GRAVE_MARKER);
        registerForSelfDrop(OverlordBlocks.WARPED_GRAVE_MARKER);
        registerForSelfDrop(OverlordBlocks.CRIMSON_GRAVE_MARKER);
        registerMultiblock(OverlordBlocks.OAK_CASKET, Properties.BED_PART, BedPart.HEAD);
        registerMultiblock(OverlordBlocks.BIRCH_CASKET, Properties.BED_PART, BedPart.HEAD);
        registerMultiblock(OverlordBlocks.JUNGLE_CASKET, Properties.BED_PART, BedPart.HEAD);
        registerMultiblock(OverlordBlocks.SPRUCE_CASKET, Properties.BED_PART, BedPart.HEAD);
        registerMultiblock(OverlordBlocks.ACACIA_CASKET, Properties.BED_PART, BedPart.HEAD);
        registerMultiblock(OverlordBlocks.DARK_OAK_CASKET, Properties.BED_PART, BedPart.HEAD);
        registerMultiblock(OverlordBlocks.WARPED_CASKET, Properties.BED_PART, BedPart.HEAD);
        registerMultiblock(OverlordBlocks.CRIMSON_CASKET, Properties.BED_PART, BedPart.HEAD);
        registerForSelfDrop(OverlordBlocks.STONE_TOMBSTONE);
        registerForSelfDrop(OverlordBlocks.DIORITE_TOMBSTONE);
        registerForSelfDrop(OverlordBlocks.ANDESITE_TOMBSTONE);
        registerForSelfDrop(OverlordBlocks.GRANITE_TOMBSTONE);
        registerForSelfDrop(OverlordBlocks.BLACKSTONE_TOMBSTONE);
        registerForSelfDrop(OverlordBlocks.DEEPSLATE_TOMBSTONE);
        register(OverlordBlocks.FLESH_SKELETON_SKULL, OverlordItems.FLESH_SKELETON_SKULL);
        register(OverlordBlocks.MUSCLE_SKELETON_SKULL, OverlordItems.MUSCLE_SKELETON_SKULL);
        register(OverlordBlocks.FLESH_MUSCLE_SKELETON_SKULL, OverlordItems.FLESH_MUSCLE_SKELETON_SKULL);
        register(OverlordBlocks.FLESH_SKELETON_WALL_SKULL, OverlordItems.FLESH_SKELETON_SKULL);
        register(OverlordBlocks.MUSCLE_SKELETON_WALL_SKULL, OverlordItems.MUSCLE_SKELETON_SKULL);
        register(OverlordBlocks.FLESH_MUSCLE_SKELETON_WALL_SKULL, OverlordItems.FLESH_MUSCLE_SKELETON_SKULL);


        Set<Identifier> set = Sets.newHashSet();

        for (Block block : OverlordBlocks.getRegisteredBlocks()) {
            Identifier identifier = block.getLootTableId();
            if (identifier != LootTables.EMPTY && set.add(identifier)) {
                LootTable.Builder builder5 = this.lootTables.remove(identifier);
                if (builder5 == null) {
                    Overlord.getLogger().error(String.format("Missing loottable '%s' for '%s'", identifier, Registry.BLOCK.getId(block)));
                    continue;
                }

                identifierBuilderBiConsumer.accept(identifier, builder5);
            }
        }

        if (!this.lootTables.isEmpty()) {
            throw new IllegalStateException("Created block loot tables for non-blocks: " + this.lootTables.keySet());
        }
    }

    private static <T> T addSurvivesExplosionCondition(ItemConvertible drop, LootConditionConsumingBuilder<T> builder) {
        return !EXPLOSION_IMMUNE.contains(drop.asItem()) ? builder.conditionally(SurvivesExplosionLootCondition.builder()) : builder.getThis();
    }

    private static LootTable.Builder drops(ItemConvertible drop) {
        return LootTable.builder().pool(addSurvivesExplosionCondition(drop, LootPool.builder().rolls(ConstantLootNumberProvider.create(1)).with(ItemEntry.builder(drop))));
    }

    private static <T extends Comparable<T> & StringIdentifiable> LootTable.Builder dropsWithProperty(Block drop, Property<T> property, T comparable) {
        return LootTable.builder().pool(addSurvivesExplosionCondition(drop, LootPool.builder().rolls(ConstantLootNumberProvider.create(1)).with(ItemEntry.builder(drop).conditionally(BlockStatePropertyLootCondition.builder(drop).properties(StatePredicate.Builder.create().exactMatch(property, comparable))))));
    }

    public void register(Block block, ItemConvertible loot) {
        this.register(block, drops(loot));
    }

    public void registerForSelfDrop(Block block) {
        this.register(block, block);
    }

    public <T extends Comparable<T> & StringIdentifiable> void registerMultiblock(Block block, Property<T> property, T comparable) {
        this.registerWithFunction(block, (registerBlock) -> dropsWithProperty(registerBlock, property, comparable));
    }

    private void registerWithFunction(Block block, Function<Block, LootTable.Builder> function) {
        this.register(block, function.apply(block));
    }

    private void register(Block block, LootTable.Builder builder) {
        this.lootTables.put(block.getLootTableId(), builder);
    }
}
