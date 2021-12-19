package dev.the_fireplace.overlord.entity.ai.goal.task;

import dev.the_fireplace.overlord.advancement.OverlordCriterions;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Predicate;

public class GatherMilkGoal extends TaskGoal
{
    protected static final Predicate<ItemStack> EMPTY_BUCKET_MATCHER = stack -> stack.getItem().equals(Items.BUCKET);
    protected final short searchDistance;

    protected byte postSwapCooldownTicks;
    protected CowEntity cow;
    protected Path path;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int updateCountdownTicks;
    protected float speed = 1;

    public GatherMilkGoal(ArmyEntity armyEntity, short searchDistance) {
        super(armyEntity);
        this.searchDistance = searchDistance;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return super.canStart()
            && hasEmptyBucket()
            && hasNearbyCow();
    }

    protected boolean hasEmptyBucket() {
        return inventorySearcher.hasSlotMatching(armyEntity.getInventory(), EMPTY_BUCKET_MATCHER);
    }

    protected boolean hasNearbyCow() {
        return findNearbyCow() != null;
    }

    @Nullable
    private CowEntity findNearbyCow() {
        return armyEntity.getEntityWorld().getClosestEntity(
            CowEntity.class,
            new TargetPredicate().setPredicate(
                cow -> !cow.isBaby()
                    && !cow.isInvisible()
                    && this.armyEntity.getNavigation().findPathTo(cow, 0) != null
                    && this.armyEntity.isInWalkTargetRange(cow.getBlockPos())
            ),
            armyEntity,
            armyEntity.getX(),
            armyEntity.getY(),
            armyEntity.getZ(),
            armyEntity.getBoundingBox().expand(searchDistance)
        );
    }

    @Override
    public boolean shouldContinue() {
        return notInCombat() && hasEmptyBucket() && hasCowTarget() && !this.armyEntity.getNavigation().isIdle();
    }

    protected boolean hasCowTarget() {
        return cow != null
            && cow.isAlive()
            && !cow.isBaby()
            && !cow.isInvisible()
            && this.armyEntity.isInWalkTargetRange(cow.getBlockPos());
    }

    @Override
    public void start() {
        super.start();
        this.postSwapCooldownTicks = 0;
        this.updateCountdownTicks = 0;
        this.cow = findNearbyCow();
        this.path = this.armyEntity.getNavigation().findPathTo(cow, 0);
        this.armyEntity.getNavigation().startMovingAlong(this.path, this.speed);
    }

    @Override
    public void stop() {
        super.stop();
        this.armyEntity.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.armyEntity.getLookControl().lookAt(cow, 30.0F, 30.0F);
        double squaredDistanceToCow = this.armyEntity.squaredDistanceTo(cow.getX(), cow.getY(), cow.getZ());
        moveToCow(squaredDistanceToCow);
        if (this.postSwapCooldownTicks > 0) {
            this.postSwapCooldownTicks--;
            return;
        }
        if (swapToBucket()) {
            return;
        }
        gatherMilk(squaredDistanceToCow);
    }

    private void gatherMilk(double squaredDistanceToCow) {
        if (squaredDistanceToCow <= getSquaredMaxMilkDistance(cow)) {
            this.armyEntity.getOffHandStack().decrement(1);
            if (this.armyEntity.getOffHandStack().isEmpty()) {
                this.armyEntity.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
            }
            this.armyEntity.giveItemStack(new ItemStack(Items.MILK_BUCKET));
            this.postSwapCooldownTicks = this.armyEntity.getEquipmentSwapTicks();

            if (this.armyEntity.getOwner() instanceof ServerPlayerEntity serverPlayer) {
                OverlordCriterions.ARMY_MEMBER_MILKED_COW.trigger(serverPlayer, this.armyEntity.getType());
            }
            //TODO maybe spook the cow if the skeleton isn't playerlike?
        }
    }

    protected double getSquaredMaxMilkDistance(LivingEntity entity) {
        return this.armyEntity.getWidth() * 2.0F * this.armyEntity.getWidth() * 2.0F + entity.getWidth();
    }

    private void moveToCow(double squaredDistanceToCow) {
        if (this.armyEntity.getVisibilityCache().canSee(cow)
            && this.updateCountdownTicks <= 0
            && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D
            || cow.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0D
            || this.armyEntity.getRandom().nextFloat() < 0.05F)
        ) {
            this.targetX = cow.getX();
            this.targetY = cow.getY();
            this.targetZ = cow.getZ();
            this.updateCountdownTicks = 4 + this.armyEntity.getRandom().nextInt(7);
            if (squaredDistanceToCow > 1024.0D) {
                this.updateCountdownTicks += 10;
            } else if (squaredDistanceToCow > 256.0D) {
                this.updateCountdownTicks += 5;
            }

            if (!this.armyEntity.getNavigation().startMovingTo(cow, this.speed)) {
                this.updateCountdownTicks += 15;
            }
        }
    }

    private boolean swapToBucket() {
        if (!armyEntity.getOffHandStack().getItem().equals(Items.BUCKET)) {
            Inventory inventory = armyEntity.getInventory();
            int emptyBucketSlot = inventorySearcher.getFirstSlotMatching(inventory, EMPTY_BUCKET_MATCHER);
            int offHandSlot = armyEntity.getOffHandSlot();
            ItemStack oldOffHandStack = inventory.removeStack(offHandSlot);
            ItemStack emptyBucketStack = inventory.removeStack(emptyBucketSlot);
            inventory.setStack(offHandSlot, emptyBucketStack);
            inventory.setStack(emptyBucketSlot, oldOffHandStack);
            this.postSwapCooldownTicks = this.armyEntity.getEquipmentSwapTicks();
            return true;
        }
        return false;
    }
}
