package dev.the_fireplace.overlord.datagen;

import dev.the_fireplace.overlord.advancement.*;
import dev.the_fireplace.overlord.augment.Augments;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import dev.the_fireplace.overlord.entity.SkeletonGrowthPhase;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import javax.inject.Inject;
import java.util.function.Consumer;

public class OverlordTabAdvancementGenerator implements Consumer<Consumer<Advancement>>
{
    private static final ImpossibleTrigger.TriggerInstance CUSTOM_CONDITION = new ImpossibleTrigger.TriggerInstance();
    private final OverlordEntities overlordEntities;
    Advancement root;

    @Inject
    public OverlordTabAdvancementGenerator(OverlordEntities overlordEntities) {
        this.overlordEntities = overlordEntities;
    }

    @Override
    public void accept(Consumer<Advancement> consumer) {
        root = Advancement.Builder.advancement().display(
            Blocks.SKELETON_SKULL,
            Component.translatable("advancements.overlord.root.title"),
            Component.translatable("advancements.overlord.root.description"),
            new ResourceLocation("textures/block/bone_block_side.png"),
            FrameType.TASK,
            true,
            false,
            false
        ).addCriterion("obtained_army_member", ObtainedArmyMemberCriterion.Conditions.any()).save(consumer, "overlord:overlord/root");

        addCreationAdvancements(consumer);
        addGrowthAdvancements(consumer);
        addEquipmentAdvancements(consumer);
        addBattleAdvancements(consumer);
        addTaskAdvancements(consumer);
        //TODO squad related?
        //TODO orders related?
    }

    private void addEquipmentAdvancements(Consumer<Advancement> consumer) {
        Advancement poorCowDisguise = Advancement.Builder.advancement().parent(root).display(
            Items.LEATHER_CHESTPLATE,
            Component.translatable("advancements.overlord.poor_cow_disguise.title"),
            Component.translatable("advancements.overlord.poor_cow_disguise.description"),
            null,
            FrameType.TASK,
            true,
            true,
            false
        ).addCriterion("full_leather_armor", SkeletonInventoryChangedCriterion.Conditions.items(
            EquipmentSlotItemPredicate.simple(Items.LEATHER_HELMET, EquipmentSlot.HEAD),
            EquipmentSlotItemPredicate.simple(Items.LEATHER_CHESTPLATE, EquipmentSlot.CHEST),
            EquipmentSlotItemPredicate.simple(Items.LEATHER_LEGGINGS, EquipmentSlot.LEGS),
            EquipmentSlotItemPredicate.simple(Items.LEATHER_BOOTS, EquipmentSlot.FEET)
        )).save(consumer, "overlord:overlord/poor_cow_disguise");

        Advancement scarecrow = Advancement.Builder.advancement().parent(root).display(
            Items.CARVED_PUMPKIN,
            Component.translatable("advancements.overlord.scarecrow.title"),
            Component.translatable("advancements.overlord.scarecrow.description"),
            null,
            FrameType.TASK,
            true,
            true,
            false
        ).addCriterion("carved_pumpkin", SkeletonInventoryChangedCriterion.Conditions.items(
            EquipmentSlotItemPredicate.simple(Items.CARVED_PUMPKIN, EquipmentSlot.HEAD)
        )).save(consumer, "overlord:overlord/scarecrow");

        Advancement falseImposter = Advancement.Builder.advancement().parent(root).display(
            Items.SKELETON_SKULL,
            Component.translatable("advancements.overlord.false_imposter.title"),
            Component.translatable("advancements.overlord.false_imposter.description"),
            null,
            FrameType.TASK,
            true,
            true,
            false
        ).addCriterion("skeleton_skull", SkeletonInventoryChangedCriterion.Conditions.items(
            EquipmentSlotItemPredicate.simple(Items.SKELETON_SKULL, EquipmentSlot.HEAD)
        )).save(consumer, "overlord:overlord/false_imposter");
    }

    private void addTaskAdvancements(Consumer<Advancement> consumer) {
        Advancement selfSustaining = Advancement.Builder.advancement().parent(root).display(
                Items.BUCKET,
                Component.translatable("advancements.overlord.self_sustaining.title"),
                Component.translatable("advancements.overlord.self_sustaining.description"),
                null,
                FrameType.TASK,
                true,
                true,
                false
            ).addCriterion("skeleton_milked_cow", ArmyMemberMilkedCowCriterion.Conditions.of(overlordEntities.getOwnedSkeletonType()))
            .save(consumer, "overlord:overlord/self_sustaining");
    }

    private void addGrowthAdvancements(Consumer<Advancement> consumer) {
        Advancement goodForTheBones = Advancement.Builder.advancement().parent(root).display(
            Items.MILK_BUCKET,
            Component.translatable("advancements.overlord.good_for_the_bones.title"),
            Component.translatable("advancements.overlord.good_for_the_bones.description"),
            null,
            FrameType.TASK,
            true,
            true,
            false
        ).addCriterion("one_bucket_drank", SkeletonDrankMilkCriterion.Conditions.any()).save(consumer, "overlord:overlord/good_for_the_bones");

        Advancement skeletonVeteran = Advancement.Builder.advancement().parent(goodForTheBones).display(
                Items.MILK_BUCKET,
                Component.translatable("advancements.overlord.skeleton_veteran.title"),
                Component.translatable("advancements.overlord.skeleton_veteran.description"),
                null,
                FrameType.TASK,
                true,
                true,
                false
            ).addCriterion("thousand_buckets_drank", SkeletonDrankMilkCriterion.Conditions.of(1000))
            .save(consumer, "overlord:overlord/skeleton_veteran");

        Advancement skeletonMaster = Advancement.Builder.advancement().parent(skeletonVeteran).display(
                Items.MILK_BUCKET,
                Component.translatable("advancements.overlord.skeleton_master.title"),
                Component.translatable("advancements.overlord.skeleton_master.description"),
                null,
                FrameType.CHALLENGE,
                true,
                true,
                false
            ).addCriterion("nine_thousand_buckets_drank", SkeletonDrankMilkCriterion.Conditions.of(9000))
            .save(consumer, "overlord:overlord/skeleton_master");
    }

    private void addCreationAdvancements(Consumer<Advancement> consumer) {
        Advancement bodybuilder = Advancement.Builder.advancement().parent(root).display(
            Items.BEEF,
            Component.translatable("advancements.overlord.bodybuilder.title"),
            Component.translatable("advancements.overlord.bodybuilder.description"),
            null,
            FrameType.TASK,
            true,
            true,
            false
        ).addCriterion("has_muscle_skeleton", SkeletonGrowthPhaseCriterion.Conditions.of(
            SkeletonGrowthPhase.BABY,
            null,
            true,
            null,
            null)
        ).save(consumer, "overlord:overlord/bodybuilder");

        Advancement fleshedOut = Advancement.Builder.advancement().parent(root).display(
            Items.LEATHER,
            Component.translatable("advancements.overlord.fleshed_out.title"),
            Component.translatable("advancements.overlord.fleshed_out.description"),
            null,
            FrameType.TASK,
            true,
            true,
            false
        ).addCriterion("has_flesh_skeleton", SkeletonGrowthPhaseCriterion.Conditions.of(
            SkeletonGrowthPhase.BABY,
            true,
            null,
            null,
            null)
        ).save(consumer, "overlord:overlord/fleshed_out");

        Advancement inhuman = Advancement.Builder.advancement().parent(fleshedOut).display(
            Items.ZOMBIE_HEAD,
            Component.translatable("advancements.overlord.inhuman.title"),
            Component.translatable("advancements.overlord.inhuman.description"),
            null,
            FrameType.TASK,
            true,
            true,
            false
        ).addCriterion("adult_flesh_no_muscle_skeleton", SkeletonGrowthPhaseCriterion.Conditions.of(
            SkeletonGrowthPhase.ADULT,
            true,
            false,
            true,
            null
        )).save(consumer, "overlord:overlord/inhuman");

        Advancement skinwalker = Advancement.Builder.advancement().parent(fleshedOut).display(
            Items.PLAYER_HEAD,
            Component.translatable("advancements.overlord.skinwalker.title"),
            Component.translatable("advancements.overlord.skinwalker.description"),
            null,
            FrameType.TASK,
            true,
            true,
            false
        ).addCriterion("adult_flesh_muscle_skeleton", SkeletonGrowthPhaseCriterion.Conditions.of(
            SkeletonGrowthPhase.ADULT,
            true,
            true,
            true,
            null
        )).save(consumer, "overlord:overlord/skinwalker");

        Advancement augmented = Advancement.Builder.advancement().parent(root).display(
            Items.MAGENTA_STAINED_GLASS,
            Component.translatable("advancements.overlord.augmented.title"),
            Component.translatable("advancements.overlord.augmented.description"),
            null,
            FrameType.TASK,
            true,
            true,
            false
        ).addCriterion("augmented_skeleton", SkeletonGrowthPhaseCriterion.Conditions.of(
            SkeletonGrowthPhase.BABY,
            null,
            null,
            null,
            SkeletonGrowthPhaseCriterion.ANY_AUGMENT
        )).save(consumer, "overlord:overlord/augmented");

        Advancement trueImposter = Advancement.Builder.advancement().parent(augmented).display(
            Items.SKELETON_SKULL,
            Component.translatable("advancements.overlord.true_imposter.title"),
            Component.translatable("advancements.overlord.true_imposter.description"),
            null,
            FrameType.TASK,
            true,
            true,
            true
        ).addCriterion("imposter_skeleton", SkeletonGrowthPhaseCriterion.Conditions.of(
            SkeletonGrowthPhase.BABY,
            null,
            null,
            null,
            Augments.IMPOSTER
        )).save(consumer, "overlord:overlord/true_imposter");

        Advancement suspicious = Advancement.Builder.advancement().parent(trueImposter).display(
            Items.SKELETON_SKULL,
            Component.translatable("advancements.overlord.suspicious.title"),
            Component.translatable("advancements.overlord.suspicious.description"),
            null,
            FrameType.CHALLENGE,
            true,
            true,
            true
        ).addCriterion("custom", CUSTOM_CONDITION).save(consumer, "overlord:overlord/sus");
    }

    private void addBattleAdvancements(Consumer<Advancement> consumer) {
        Advancement firstBlood = Advancement.Builder.advancement().parent(root).display(
            Items.IRON_AXE,
            Component.translatable("advancements.overlord.first_blood.title"),
            Component.translatable("advancements.overlord.first_blood.description"),
            null,
            FrameType.TASK,
            true,
            true,
            false
        ).addCriterion("custom", CUSTOM_CONDITION).save(consumer, "overlord:overlord/first_blood");

        EntityPredicate.Builder isSkeletonType = EntityPredicate.Builder.entity().of(overlordEntities.getOwnedSkeletonType());
        KilledTrigger.TriggerInstance onKilledSkeleton = KilledTrigger.TriggerInstance.playerKilledEntity(isSkeletonType);
        Advancement skeletonKiller = Advancement.Builder.advancement().parent(root).display(
                Items.IRON_SWORD,
                Component.translatable("advancements.overlord.skeleton_killer.title"),
                Component.translatable("advancements.overlord.skeleton_killer.description"),
                null,
                FrameType.TASK,
                true,
                true,
                false
            ).addCriterion("killed_army_skeleton", onKilledSkeleton)
            .save(consumer, "overlord:overlord/skeleton_killer");

        Advancement mirrorRoutine = Advancement.Builder.advancement().parent(firstBlood).display(
            Items.BOW,
            Component.translatable("advancements.overlord.mirror_routine.title"),
            Component.translatable("advancements.overlord.mirror_routine.description"),
            null,
            FrameType.CHALLENGE,
            true,
            true,
            false
        ).addCriterion("custom", CUSTOM_CONDITION).save(consumer, "overlord:overlord/mirror_routine");
    }
}
