package dev.the_fireplace.overlord.entity.ai.goal.task;

import dev.the_fireplace.overlord.advancement.OverlordCriterions;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Predicate;

public class GatherMilkGoal extends TaskGoal
{
    protected static final Predicate<ItemStack> EMPTY_BUCKET_MATCHER = stack -> stack.is(Items.BUCKET);
    protected final short searchDistance;

    protected byte postSwapCooldownTicks;
    protected Cow cow;
    protected Path path;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int updateCountdownTicks;
    protected float speed = 1;

    public GatherMilkGoal(ArmyEntity armyEntity, short searchDistance) {
        super(armyEntity);
        this.searchDistance = searchDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return super.canUse()
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
    private Cow findNearbyCow() {
        return armyEntity.getCommandSenderWorld().getNearestEntity(
            Cow.class,
            TargetingConditions.forNonCombat().selector(
                cow -> !cow.isBaby()
                    && !cow.isInvisible()
                    && this.armyEntity.getNavigation().createPath(cow, 0) != null
                    && this.armyEntity.isWithinRestriction(cow.blockPosition())
            ),
            armyEntity,
            armyEntity.getX(),
            armyEntity.getY(),
            armyEntity.getZ(),
            armyEntity.getBoundingBox().inflate(searchDistance)
        );
    }

    @Override
    public boolean canContinueToUse() {
        return notInCombat() && hasEmptyBucket() && hasCowTarget() && !this.armyEntity.getNavigation().isDone();
    }

    protected boolean hasCowTarget() {
        return cow != null
            && cow.isAlive()
            && !cow.isBaby()
            && !cow.isInvisible()
            && this.armyEntity.isWithinRestriction(cow.blockPosition());
    }

    @Override
    public void start() {
        super.start();
        this.postSwapCooldownTicks = 0;
        this.updateCountdownTicks = 0;
        this.cow = findNearbyCow();
        this.path = this.armyEntity.getNavigation().createPath(cow, 0);
        this.armyEntity.getNavigation().moveTo(this.path, this.speed);
    }

    @Override
    public void stop() {
        super.stop();
        this.armyEntity.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.armyEntity.getLookControl().setLookAt(cow, 30.0F, 30.0F);
        double squaredDistanceToCow = this.armyEntity.distanceToSqr(cow.getX(), cow.getY(), cow.getZ());
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
            this.armyEntity.getOffhandItem().shrink(1);
            if (this.armyEntity.getOffhandItem().isEmpty()) {
                this.armyEntity.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }
            this.armyEntity.giveItemStack(new ItemStack(Items.MILK_BUCKET));
            this.postSwapCooldownTicks = this.armyEntity.getEquipmentSwapTicks();

            if (this.armyEntity.getOwner() instanceof ServerPlayer serverPlayer) {
                OverlordCriterions.ARMY_MEMBER_MILKED_COW.trigger(serverPlayer, this.armyEntity.getType());
            }
            //TODO maybe spook the cow if the skeleton isn't playerlike?
        }
    }

    protected double getSquaredMaxMilkDistance(LivingEntity entity) {
        return this.armyEntity.getBbWidth() * 2.0F * this.armyEntity.getBbWidth() * 2.0F + entity.getBbWidth();
    }

    private void moveToCow(double squaredDistanceToCow) {
        if (this.armyEntity.getSensing().hasLineOfSight(cow)
            && this.updateCountdownTicks <= 0
            && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D
            || cow.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0D
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

            if (!this.armyEntity.getNavigation().moveTo(cow, this.speed)) {
                this.updateCountdownTicks += 15;
            }
        }
    }

    private boolean swapToBucket() {
        if (!armyEntity.getOffhandItem().is(Items.BUCKET)) {
            Container inventory = armyEntity.getInventory();
            int emptyBucketSlot = inventorySearcher.getFirstSlotMatching(inventory, EMPTY_BUCKET_MATCHER);
            int offHandSlot = armyEntity.getOffHandSlot();
            ItemStack oldOffHandStack = inventory.removeItemNoUpdate(offHandSlot);
            ItemStack emptyBucketStack = inventory.removeItemNoUpdate(emptyBucketSlot);
            inventory.setItem(offHandSlot, emptyBucketStack);
            inventory.setItem(emptyBucketSlot, oldOffHandStack);
            this.postSwapCooldownTicks = this.armyEntity.getEquipmentSwapTicks();
            return true;
        }
        return false;
    }
}
