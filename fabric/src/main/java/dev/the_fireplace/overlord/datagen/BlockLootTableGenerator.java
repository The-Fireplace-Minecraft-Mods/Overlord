package dev.the_fireplace.overlord.datagen;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.block.OverlordBlocks;
import dev.the_fireplace.overlord.item.OverlordItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class BlockLootTableGenerator extends FabricBlockLootTableProvider
{
    private static final Set<Item> EXPLOSION_IMMUNE = Stream.of(Blocks.DRAGON_EGG, Blocks.BEACON, Blocks.CONDUIT, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.CREEPER_HEAD, Blocks.DRAGON_HEAD, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX).map(ItemLike::asItem).collect(ImmutableSet.toImmutableSet());

    public BlockLootTableGenerator(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateBlockLootTables() {
        Injector injector = OverlordConstants.getInjector();
        OverlordBlocks overlordBlocks = injector.getInstance(OverlordBlocks.class);
        OverlordItems overlordItems = injector.getInstance(OverlordItems.class);
        registerForSelfDrop(overlordBlocks.getBloodSoakedSoil());
        registerForSelfDrop(overlordBlocks.getOakGraveMarker());
        registerForSelfDrop(overlordBlocks.getBirchGraveMarker());
        registerForSelfDrop(overlordBlocks.getJungleGraveMarker());
        registerForSelfDrop(overlordBlocks.getSpruceGraveMarker());
        registerForSelfDrop(overlordBlocks.getAcaciaGraveMarker());
        registerForSelfDrop(overlordBlocks.getDarkOakGraveMarker());
        registerForSelfDrop(overlordBlocks.getWarpedGraveMarker());
        registerForSelfDrop(overlordBlocks.getCrimsonGraveMarker());
        registerMultiblock(overlordBlocks.getOakCasket(), BlockStateProperties.BED_PART, BedPart.HEAD);
        registerMultiblock(overlordBlocks.getBirchCasket(), BlockStateProperties.BED_PART, BedPart.HEAD);
        registerMultiblock(overlordBlocks.getJungleCasket(), BlockStateProperties.BED_PART, BedPart.HEAD);
        registerMultiblock(overlordBlocks.getSpruceCasket(), BlockStateProperties.BED_PART, BedPart.HEAD);
        registerMultiblock(overlordBlocks.getAcaciaCasket(), BlockStateProperties.BED_PART, BedPart.HEAD);
        registerMultiblock(overlordBlocks.getDarkOakCasket(), BlockStateProperties.BED_PART, BedPart.HEAD);
        registerMultiblock(overlordBlocks.getWarpedCasket(), BlockStateProperties.BED_PART, BedPart.HEAD);
        registerMultiblock(overlordBlocks.getCrimsonCasket(), BlockStateProperties.BED_PART, BedPart.HEAD);
        registerForSelfDrop(overlordBlocks.getStoneTombstone());
        registerForSelfDrop(overlordBlocks.getDioriteTombstone());
        registerForSelfDrop(overlordBlocks.getAndesiteTombstone());
        registerForSelfDrop(overlordBlocks.getGraniteTombstone());
        registerForSelfDrop(overlordBlocks.getBlackstoneTombstone());
        registerForSelfDrop(overlordBlocks.getDeepslateTombstone());
        add(overlordBlocks.getFleshSkeletonSkull(), overlordItems.getFleshSkeletonSkull());
        add(overlordBlocks.getMuscleSkeletonSkull(), overlordItems.getMuscleSkeletonSkull());
        add(overlordBlocks.getFleshMuscleSkeletonSkull(), overlordItems.getFleshMuscleSkeletonSkull());
        add(overlordBlocks.getFleshSkeletonWallSkull(), overlordItems.getFleshSkeletonSkull());
        add(overlordBlocks.getMuscleSkeletonWallSkull(), overlordItems.getMuscleSkeletonSkull());
        add(overlordBlocks.getFleshMuscleSkeletonWallSkull(), overlordItems.getFleshMuscleSkeletonSkull());
    }

    private static <T extends ConditionUserBuilder<T>> T addSurvivesExplosionCondition(ItemLike drop, ConditionUserBuilder<T> builder) {
        return !EXPLOSION_IMMUNE.contains(drop.asItem()) ? builder.when(ExplosionCondition.survivesExplosion()) : builder.unwrap();
    }

    private static LootTable.Builder drops(ItemLike drop) {
        return LootTable.lootTable().withPool(addSurvivesExplosionCondition(drop, LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(drop))));
    }

    private static <T extends Comparable<T> & StringRepresentable> LootTable.Builder dropsWithProperty(Block drop, Property<T> property, T comparable) {
        return LootTable.lootTable().withPool(addSurvivesExplosionCondition(drop, LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(drop).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(drop).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, comparable))))));
    }

    public void add(Block block, ItemLike loot) {
        this.add(block, drops(loot));
    }

    public void registerForSelfDrop(Block block) {
        this.add(block, block);
    }

    public <T extends Comparable<T> & StringRepresentable> void registerMultiblock(Block block, Property<T> property, T comparable) {
        this.registerWithFunction(block, (registerBlock) -> dropsWithProperty(registerBlock, property, comparable));
    }

    private void registerWithFunction(Block block, Function<Block, LootTable.Builder> function) {
        this.add(block, function.apply(block));
    }
}
