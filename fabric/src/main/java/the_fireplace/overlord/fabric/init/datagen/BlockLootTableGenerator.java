package the_fireplace.overlord.fabric.init.datagen;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.condition.*;
import net.minecraft.loot.entry.DynamicEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.*;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.state.property.Property;
import net.minecraft.util.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;
import the_fireplace.overlord.fabric.init.OverlordBlocks;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class BlockLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
    private static final LootCondition.Builder NEEDS_SILK_TOUCH;
    private static final LootCondition.Builder DOESNT_NEED_SILK_TOUCH;
    private static final LootCondition.Builder NEEDS_SHEARS;
    private static final LootCondition.Builder NEEDS_SILK_TOUCH_SHEARS;
    private static final LootCondition.Builder DOESNT_NEED_SILK_TOUCH_SHEARS;
    private static final Set<Item> ALWAYS_DROPPED_FROM_EXPLOSION;
    private static final float[] SAPLING_DROP_CHANCES_FROM_LEAVES;
    private static final float[] JUNGLE_SAPLING_DROP_CHANCES_FROM_LEAVES;
    private final Map<Identifier, LootTable.Builder> lootTables = Maps.newHashMap();

    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> identifierBuilderBiConsumer) {
        registerForSelfDrop(OverlordBlocks.BLOOD_SOAKED_SOIL);



        Set<Identifier> set = Sets.newHashSet();

        for (Block block : Registry.BLOCK) {
            Identifier identifier = block.getDropTableId();
            if (identifier != LootTables.EMPTY && set.add(identifier)) {
                LootTable.Builder builder5 = this.lootTables.remove(identifier);
                if (builder5 == null) {
                    throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", identifier, Registry.BLOCK.getId(block)));
                }

                identifierBuilderBiConsumer.accept(identifier, builder5);
            }
        }

        if (!this.lootTables.isEmpty()) {
            throw new IllegalStateException("Created block loot tables for non-blocks: " + this.lootTables.keySet());
        }
    }

    private static <T> T addExplosionDecayLootFunction(ItemConvertible itemConvertible, LootFunctionConsumingBuilder<T> lootFunctionConsumingBuilder) {
        return !ALWAYS_DROPPED_FROM_EXPLOSION.contains(itemConvertible.asItem()) ? lootFunctionConsumingBuilder.withFunction(ExplosionDecayLootFunction.builder()) : lootFunctionConsumingBuilder.getThis();
    }

    private static <T> T addSurvivesExplosionLootCondition(ItemConvertible itemConvertible, LootConditionConsumingBuilder<T> lootConditionConsumingBuilder) {
        return !ALWAYS_DROPPED_FROM_EXPLOSION.contains(itemConvertible.asItem()) ? lootConditionConsumingBuilder.withCondition(SurvivesExplosionLootCondition.builder()) : lootConditionConsumingBuilder.getThis();
    }

    private static LootTable.Builder create(ItemConvertible itemConvertible) {
        return LootTable.builder().withPool(addSurvivesExplosionLootCondition(itemConvertible, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible))));
    }

    private static LootTable.Builder create(Block block, LootCondition.Builder conditionBuilder, LootEntry.Builder<?> child) {
        return LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(block).withCondition(conditionBuilder).withChild(child)));
    }

    private static LootTable.Builder createForNeedingSilkTouch(Block block, LootEntry.Builder<?> child) {
        return create(block, NEEDS_SILK_TOUCH, child);
    }

    private static LootTable.Builder createForNeedingShears(Block block, LootEntry.Builder<?> child) {
        return create(block, NEEDS_SHEARS, child);
    }

    private static LootTable.Builder createForNeedingSilkTouchShears(Block block, LootEntry.Builder<?> child) {
        return create(block, NEEDS_SILK_TOUCH_SHEARS, child);
    }

    private static LootTable.Builder createForBlockWithItemDrops(Block block, ItemConvertible lootWithoutSilkTouch) {
        return createForNeedingSilkTouch(block, addSurvivesExplosionLootCondition(block, ItemEntry.builder(lootWithoutSilkTouch)));
    }

    private static LootTable.Builder create(ItemConvertible itemConvertible, LootTableRange count) {
        return LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(addExplosionDecayLootFunction(itemConvertible, ItemEntry.builder(itemConvertible).withFunction(SetCountLootFunction.builder(count)))));
    }

    private static LootTable.Builder createForBlockWithItemDrops(Block block, ItemConvertible lootWithoutSilkTouch, LootTableRange count) {
        return createForNeedingSilkTouch(block, addExplosionDecayLootFunction(block, ItemEntry.builder(lootWithoutSilkTouch).withFunction(SetCountLootFunction.builder(count))));
    }

    private static LootTable.Builder createForNeedingSilkTouch(ItemConvertible itemConvertible) {
        return LootTable.builder().withPool(LootPool.builder().withCondition(NEEDS_SILK_TOUCH).withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible)));
    }

    private static LootTable.Builder createForPottedPlant(ItemConvertible itemConvertible) {
        return LootTable.builder().withPool(addSurvivesExplosionLootCondition(Blocks.FLOWER_POT, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Blocks.FLOWER_POT)))).withPool((LootPool.Builder)addSurvivesExplosionLootCondition(itemConvertible, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible))));
    }

    private static LootTable.Builder createForSlabs(Block block) {
        return LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(addExplosionDecayLootFunction(block, ItemEntry.builder(block).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(2)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, (Comparable) SlabType.DOUBLE)))))));
    }

    private static <T extends Comparable<T> & StringIdentifiable> LootTable.Builder createForMultiblock(Block block, Property<T> property, T comparable) {
        return LootTable.builder().withPool(addSurvivesExplosionLootCondition(block, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(block).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(property, comparable))))));
    }

    private static LootTable.Builder createForNameableContainer(Block block) {
        return LootTable.builder().withPool(addSurvivesExplosionLootCondition(block, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(block).withFunction(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY)))));
    }

    private static LootTable.Builder createForShulkerBox(Block block) {
        return LootTable.builder().withPool(addSurvivesExplosionLootCondition(block, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(block).withFunction(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY)).withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Lock", "BlockEntityTag.Lock").withOperation("LootTable", "BlockEntityTag.LootTable").withOperation("LootTableSeed", "BlockEntityTag.LootTableSeed")).withFunction(SetContentsLootFunction.builder().withEntry(DynamicEntry.builder(ShulkerBoxBlock.CONTENTS))))));
    }

    private static LootTable.Builder createForBanner(Block block) {
        return LootTable.builder().withPool(addSurvivesExplosionLootCondition(block, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(block).withFunction(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY)).withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Patterns", "BlockEntityTag.Patterns")))));
    }

    private static LootTable.Builder createForBeeNest(Block block) {
        return LootTable.builder().withPool(LootPool.builder().withCondition(NEEDS_SILK_TOUCH).withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(block).withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Bees", "BlockEntityTag.Bees")).withFunction(CopyStateFunction.getBuilder(block).method_21898(BeehiveBlock.HONEY_LEVEL))));
    }

    private static LootTable.Builder createForBeehive(Block block) {
        return LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(block).withCondition(NEEDS_SILK_TOUCH).withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Bees", "BlockEntityTag.Bees")).withFunction(CopyStateFunction.getBuilder(block).method_21898(BeehiveBlock.HONEY_LEVEL)).withChild(ItemEntry.builder(block))));
    }

    private static LootTable.Builder createForOreWithSingleItemDrop(Block block, Item item) {
        return createForNeedingSilkTouch(block, addExplosionDecayLootFunction(block, ItemEntry.builder(item).withFunction(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))));
    }

    private static LootTable.Builder createForLargeMushroomBlock(Block block, ItemConvertible loot) {
        return createForNeedingSilkTouch(block, addExplosionDecayLootFunction(block, ItemEntry.builder(loot).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(-6.0F, 2.0F))).withFunction(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMin(0)))));
    }

    private static LootTable.Builder createForTallGrass(Block block) {
        return createForNeedingShears(block, addExplosionDecayLootFunction(block, ItemEntry.builder(Items.WHEAT_SEEDS).withCondition(RandomChanceLootCondition.builder(0.125F)).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2))));
    }

    private static LootTable.Builder createForCropStem(Block block, Item seeds) {
        return LootTable.builder().withPool(addExplosionDecayLootFunction(block, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(seeds).withFunction(SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.06666667F)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 0)))).withFunction(SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.13333334F)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 1)))).withFunction(SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.2F)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 2)))).withFunction(SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.26666668F)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 3)))).withFunction(SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.33333334F)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 4)))).withFunction(SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.4F)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 5)))).withFunction(SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.46666667F)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 6)))).withFunction(SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.53333336F)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 7)))))));
    }

    private static LootTable.Builder createForAttachedCropStem(Block block, Item seeds) {
        return LootTable.builder().withPool(addExplosionDecayLootFunction(block, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(seeds).withFunction(SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.53333336F))))));
    }

    private static LootTable.Builder createForBlockNeedingShears(ItemConvertible itemConvertible) {
        return LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withCondition(NEEDS_SHEARS).withEntry(ItemEntry.builder(itemConvertible)));
    }

    private static LootTable.Builder createForLeaves(Block leafBlock, Block sapling, float... saplingDropChances) {
        return createForNeedingSilkTouchShears(leafBlock, addSurvivesExplosionLootCondition(leafBlock, ItemEntry.builder(sapling)).withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, saplingDropChances))).withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withCondition(DOESNT_NEED_SILK_TOUCH_SHEARS).withEntry(((LeafEntry.Builder)addExplosionDecayLootFunction(leafBlock, ItemEntry.builder(Items.STICK).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))).withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))));
    }

    private static LootTable.Builder createForOakLeaves(Block block, Block block2, float... fs) {
        return createForLeaves(block, block2, fs).withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withCondition(DOESNT_NEED_SILK_TOUCH_SHEARS).withEntry(addSurvivesExplosionLootCondition(block, ItemEntry.builder(Items.APPLE)).withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
    }

    private static LootTable.Builder createForCrops(Block block, Item food, Item seeds, LootCondition.Builder condition) {
        return addExplosionDecayLootFunction(block, LootTable.builder().withPool(LootPool.builder().withEntry(ItemEntry.builder(food).withCondition(condition).withChild(ItemEntry.builder(seeds)))).withPool(LootPool.builder().withCondition(condition).withEntry(ItemEntry.builder(seeds).withFunction(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))));
    }

    public static LootTable.Builder createEmpty() {
        return LootTable.builder();
    }

    public void registerForPottedPlant(Block block) {
        this.registerWithFunction(block, (blockx) -> createForPottedPlant(((FlowerPotBlock)blockx).getContent()));
    }

    public void registerForNeedingSilkTouch(Block block, Block droppedBlock) {
        this.register(block, createForNeedingSilkTouch(droppedBlock));
    }

    public void register(Block block, ItemConvertible loot) {
        this.register(block, create(loot));
    }

    public void registerForNeedingSilkTouch(Block block) {
        this.registerForNeedingSilkTouch(block, block);
    }

    public void registerForSelfDrop(Block block) {
        this.register(block, block);
    }

    private void registerWithFunction(Block block, Function<Block, LootTable.Builder> function) {
        this.register(block, function.apply(block));
    }

    private void register(Block block, LootTable.Builder builder) {
        this.lootTables.put(block.getDropTableId(), builder);
    }

    static {
        NEEDS_SILK_TOUCH = MatchToolLootCondition.builder(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1))));
        DOESNT_NEED_SILK_TOUCH = NEEDS_SILK_TOUCH.invert();
        NEEDS_SHEARS = MatchToolLootCondition.builder(ItemPredicate.Builder.create().item(Items.SHEARS));
        NEEDS_SILK_TOUCH_SHEARS = NEEDS_SHEARS.withCondition(NEEDS_SILK_TOUCH);
        DOESNT_NEED_SILK_TOUCH_SHEARS = NEEDS_SILK_TOUCH_SHEARS.invert();
        ALWAYS_DROPPED_FROM_EXPLOSION = Stream.of(Blocks.DRAGON_EGG, Blocks.BEACON, Blocks.CONDUIT, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.CREEPER_HEAD, Blocks.DRAGON_HEAD, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX).map(ItemConvertible::asItem).collect(ImmutableSet.toImmutableSet());
        SAPLING_DROP_CHANCES_FROM_LEAVES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
        JUNGLE_SAPLING_DROP_CHANCES_FROM_LEAVES = new float[]{0.025F, 0.027777778F, 0.03125F, 0.041666668F, 0.1F};
    }
}
