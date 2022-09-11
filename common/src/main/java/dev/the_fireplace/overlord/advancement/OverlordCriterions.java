package dev.the_fireplace.overlord.advancement;

import dev.the_fireplace.overlord.mixin.CriteriaAccessor;
import net.minecraft.advancements.CriterionTrigger;

public final class OverlordCriterions
{
    public static final ObtainedArmyMemberCriterion OBTAINED_ARMY_MEMBER = new ObtainedArmyMemberCriterion();
    public static final SkeletonInventoryChangedCriterion SKELETON_INVENTORY_CHANGED = new SkeletonInventoryChangedCriterion();
    public static final ArmyMemberMilkedCowCriterion ARMY_MEMBER_MILKED_COW = new ArmyMemberMilkedCowCriterion();
    public static final SkeletonDrankMilkCriterion SKELETON_DRANK_MILK = new SkeletonDrankMilkCriterion();
    public static final SkeletonGrowthPhaseCriterion SKELETON_ACHIEVED_GROWTH_PHASE = new SkeletonGrowthPhaseCriterion();

    private static void registerCriterion(CriterionTrigger<?> criterion) {
        CriteriaAccessor.callRegister(criterion);
    }

    public static void register() {
        registerCriterion(OBTAINED_ARMY_MEMBER);
        registerCriterion(SKELETON_INVENTORY_CHANGED);
        registerCriterion(ARMY_MEMBER_MILKED_COW);
        registerCriterion(SKELETON_DRANK_MILK);
        registerCriterion(SKELETON_ACHIEVED_GROWTH_PHASE);
    }
}
