package dev.the_fireplace.overlord.datagen;

import dev.the_fireplace.overlord.advancement.*;
import dev.the_fireplace.overlord.augment.Augments;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import dev.the_fireplace.overlord.entity.SkeletonGrowthPhase;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class OverlordTabAdvancementGenerator implements Consumer<Consumer<Advancement>>
{
    private static final ImpossibleCriterion.Conditions CUSTOM_CONDITION = new ImpossibleCriterion.Conditions();
    Advancement root;

    @Override
    public void accept(Consumer<Advancement> consumer) {
        root = Advancement.Builder.create().display(
            Blocks.SKELETON_SKULL,
            new TranslatableText("advancements.overlord.root.title"),
            new TranslatableText("advancements.overlord.root.description"),
            new Identifier("textures/block/bone_block_side.png"),
            AdvancementFrame.TASK,
            true,
            false,
            false
        ).criterion("obtained_army_member", ObtainedArmyMemberCriterion.Conditions.any()).build(consumer, "overlord:overlord/root");

        addCreationAdvancements(consumer);
        addGrowthAdvancements(consumer);
        addEquipmentAdvancements(consumer);
        addBattleAdvancements(consumer);
        addTaskAdvancements(consumer);
        //TODO squad related?
        //TODO orders related?
    }

    private void addEquipmentAdvancements(Consumer<Advancement> consumer) {
        Advancement poorCowDisguise = Advancement.Builder.create().parent(root).display(
            Items.LEATHER_CHESTPLATE,
            new TranslatableText("advancements.overlord.poor_cow_disguise.title"),
            new TranslatableText("advancements.overlord.poor_cow_disguise.description"),
            null,
            AdvancementFrame.TASK,
            true,
            true,
            false
        ).criterion("full_leather_armor", SkeletonInventoryChangedCriterion.Conditions.items(
            EquipmentSlotItemPredicate.simple(Items.LEATHER_HELMET, EquipmentSlot.HEAD),
            EquipmentSlotItemPredicate.simple(Items.LEATHER_CHESTPLATE, EquipmentSlot.CHEST),
            EquipmentSlotItemPredicate.simple(Items.LEATHER_LEGGINGS, EquipmentSlot.LEGS),
            EquipmentSlotItemPredicate.simple(Items.LEATHER_BOOTS, EquipmentSlot.FEET)
        )).build(consumer, "overlord:overlord/poor_cow_disguise");

        Advancement scarecrow = Advancement.Builder.create().parent(root).display(
            Items.CARVED_PUMPKIN,
            new TranslatableText("advancements.overlord.scarecrow.title"),
            new TranslatableText("advancements.overlord.scarecrow.description"),
            null,
            AdvancementFrame.TASK,
            true,
            true,
            false
        ).criterion("carved_pumpkin", SkeletonInventoryChangedCriterion.Conditions.items(
            EquipmentSlotItemPredicate.simple(Items.CARVED_PUMPKIN, EquipmentSlot.HEAD)
        )).build(consumer, "overlord:overlord/scarecrow");

        Advancement falseImposter = Advancement.Builder.create().parent(root).display(
            Items.SKELETON_SKULL,
            new TranslatableText("advancements.overlord.false_imposter.title"),
            new TranslatableText("advancements.overlord.false_imposter.description"),
            null,
            AdvancementFrame.TASK,
            true,
            true,
            false
        ).criterion("skeleton_skull", SkeletonInventoryChangedCriterion.Conditions.items(
            EquipmentSlotItemPredicate.simple(Items.SKELETON_SKULL, EquipmentSlot.HEAD)
        )).build(consumer, "overlord:overlord/false_imposter");
    }

    private void addTaskAdvancements(Consumer<Advancement> consumer) {
        Advancement selfSustaining = Advancement.Builder.create().parent(root).display(
                Items.BUCKET,
                new TranslatableText("advancements.overlord.self_sustaining.title"),
                new TranslatableText("advancements.overlord.self_sustaining.description"),
                null,
                AdvancementFrame.TASK,
                true,
                true,
                false
            ).criterion("skeleton_milked_cow", ArmyMemberMilkedCowCriterion.Conditions.of(OverlordEntities.OWNED_SKELETON_TYPE))
            .build(consumer, "overlord:overlord/self_sustaining");
    }

    private void addGrowthAdvancements(Consumer<Advancement> consumer) {
        Advancement goodForTheBones = Advancement.Builder.create().parent(root).display(
            Items.MILK_BUCKET,
            new TranslatableText("advancements.overlord.good_for_the_bones.title"),
            new TranslatableText("advancements.overlord.good_for_the_bones.description"),
            null,
            AdvancementFrame.TASK,
            true,
            true,
            false
        ).criterion("one_bucket_drank", SkeletonDrankMilkCriterion.Conditions.any()).build(consumer, "overlord:overlord/good_for_the_bones");

        Advancement skeletonVeteran = Advancement.Builder.create().parent(goodForTheBones).display(
                Items.MILK_BUCKET,
                new TranslatableText("advancements.overlord.skeleton_veteran.title"),
                new TranslatableText("advancements.overlord.skeleton_veteran.description"),
                null,
                AdvancementFrame.TASK,
                true,
                true,
                false
            ).criterion("thousand_buckets_drank", SkeletonDrankMilkCriterion.Conditions.of(1000))
            .build(consumer, "overlord:overlord/skeleton_veteran");

        Advancement skeletonMaster = Advancement.Builder.create().parent(skeletonVeteran).display(
                Items.MILK_BUCKET,
                new TranslatableText("advancements.overlord.skeleton_master.title"),
                new TranslatableText("advancements.overlord.skeleton_master.description"),
                null,
                AdvancementFrame.CHALLENGE,
                true,
                true,
                false
            ).criterion("nine_thousand_buckets_drank", SkeletonDrankMilkCriterion.Conditions.of(9000))
            .build(consumer, "overlord:overlord/skeleton_master");
    }

    private void addCreationAdvancements(Consumer<Advancement> consumer) {
        Advancement bodybuilder = Advancement.Builder.create().parent(root).display(
            Items.BEEF,
            new TranslatableText("advancements.overlord.bodybuilder.title"),
            new TranslatableText("advancements.overlord.bodybuilder.description"),
            null,
            AdvancementFrame.TASK,
            true,
            true,
            false
        ).criterion("has_muscle_skeleton", SkeletonGrowthPhaseCriterion.Conditions.of(
            SkeletonGrowthPhase.BABY,
            null,
            true,
            null,
            null)
        ).build(consumer, "overlord:overlord/bodybuilder");

        Advancement fleshedOut = Advancement.Builder.create().parent(root).display(
            Items.LEATHER,
            new TranslatableText("advancements.overlord.fleshed_out.title"),
            new TranslatableText("advancements.overlord.fleshed_out.description"),
            null,
            AdvancementFrame.TASK,
            true,
            true,
            false
        ).criterion("has_flesh_skeleton", SkeletonGrowthPhaseCriterion.Conditions.of(
            SkeletonGrowthPhase.BABY,
            true,
            null,
            null,
            null)
        ).build(consumer, "overlord:overlord/fleshed_out");

        Advancement inhuman = Advancement.Builder.create().parent(fleshedOut).display(
            Items.ZOMBIE_HEAD,
            new TranslatableText("advancements.overlord.inhuman.title"),
            new TranslatableText("advancements.overlord.inhuman.description"),
            null,
            AdvancementFrame.TASK,
            true,
            true,
            false
        ).criterion("adult_flesh_no_muscle_skeleton", SkeletonGrowthPhaseCriterion.Conditions.of(
            SkeletonGrowthPhase.ADULT,
            true,
            false,
            true,
            null
        )).build(consumer, "overlord:overlord/inhuman");

        Advancement skinwalker = Advancement.Builder.create().parent(fleshedOut).display(
            Items.PLAYER_HEAD,
            new TranslatableText("advancements.overlord.skinwalker.title"),
            new TranslatableText("advancements.overlord.skinwalker.description"),
            null,
            AdvancementFrame.TASK,
            true,
            true,
            false
        ).criterion("adult_flesh_muscle_skeleton", SkeletonGrowthPhaseCriterion.Conditions.of(
            SkeletonGrowthPhase.ADULT,
            true,
            true,
            true,
            null
        )).build(consumer, "overlord:overlord/skinwalker");

        Advancement augmented = Advancement.Builder.create().parent(root).display(
            Items.MAGENTA_STAINED_GLASS,
            new TranslatableText("advancements.overlord.augmented.title"),
            new TranslatableText("advancements.overlord.augmented.description"),
            null,
            AdvancementFrame.TASK,
            true,
            true,
            false
        ).criterion("augmented_skeleton", SkeletonGrowthPhaseCriterion.Conditions.of(
            SkeletonGrowthPhase.BABY,
            null,
            null,
            null,
            SkeletonGrowthPhaseCriterion.ANY_AUGMENT
        )).build(consumer, "overlord:overlord/augmented");

        Advancement trueImposter = Advancement.Builder.create().parent(augmented).display(
            Items.SKELETON_SKULL,
            new TranslatableText("advancements.overlord.true_imposter.title"),
            new TranslatableText("advancements.overlord.true_imposter.description"),
            null,
            AdvancementFrame.TASK,
            true,
            true,
            true
        ).criterion("imposter_skeleton", SkeletonGrowthPhaseCriterion.Conditions.of(
            SkeletonGrowthPhase.BABY,
            null,
            null,
            null,
            Augments.IMPOSTER
        )).build(consumer, "overlord:overlord/true_imposter");

        Advancement suspicious = Advancement.Builder.create().parent(trueImposter).display(
            Items.SKELETON_SKULL,
            new TranslatableText("advancements.overlord.suspicious.title"),
            new TranslatableText("advancements.overlord.suspicious.description"),
            null,
            AdvancementFrame.CHALLENGE,
            true,
            true,
            true
        ).criterion("custom", CUSTOM_CONDITION).build(consumer, "overlord:overlord/sus");
    }

    private void addBattleAdvancements(Consumer<Advancement> consumer) {
        Advancement firstBlood = Advancement.Builder.create().parent(root).display(
            Items.IRON_AXE,
            new TranslatableText("advancements.overlord.first_blood.title"),
            new TranslatableText("advancements.overlord.first_blood.description"),
            null,
            AdvancementFrame.TASK,
            true,
            true,
            false
        ).criterion("custom", CUSTOM_CONDITION).build(consumer, "overlord:overlord/first_blood");

        EntityPredicate.Builder isSkeletonType = EntityPredicate.Builder.create().type(OverlordEntities.OWNED_SKELETON_TYPE);
        OnKilledCriterion.Conditions onKilledSkeleton = OnKilledCriterion.Conditions.createPlayerKilledEntity(isSkeletonType);
        Advancement skeletonKiller = Advancement.Builder.create().parent(root).display(
                Items.IRON_SWORD,
                new TranslatableText("advancements.overlord.skeleton_killer.title"),
                new TranslatableText("advancements.overlord.skeleton_killer.description"),
                null,
                AdvancementFrame.TASK,
                true,
                true,
                false
            ).criterion("killed_army_skeleton", onKilledSkeleton)
            .build(consumer, "overlord:overlord/skeleton_killer");

        Advancement mirrorRoutine = Advancement.Builder.create().parent(firstBlood).display(
            Items.BOW,
            new TranslatableText("advancements.overlord.mirror_routine.title"),
            new TranslatableText("advancements.overlord.mirror_routine.description"),
            null,
            AdvancementFrame.CHALLENGE,
            true,
            true,
            false
        ).criterion("custom", CUSTOM_CONDITION).build(consumer, "overlord:overlord/mirror_routine");
    }
}
