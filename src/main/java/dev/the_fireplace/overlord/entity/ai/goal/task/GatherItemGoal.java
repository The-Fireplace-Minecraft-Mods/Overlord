package dev.the_fireplace.overlord.entity.ai.goal.task;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Random;

public class GatherItemGoal extends TaskGoal
{
    protected final short searchDistance;

    protected byte postSwapCooldownTicks;
    protected ItemEntity item;
    protected Path path;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int updateCountdownTicks;
    protected float speed = 1;

    public GatherItemGoal(ArmyEntity armyEntity, short searchDistance) {
        super(armyEntity);
        this.searchDistance = searchDistance;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return super.canStart()
            && hasEmptySlot()
            && hasNearbyItem();
    }

    protected boolean hasEmptySlot() {
        return inventorySearcher.hasSlotMatching(armyEntity.getInventory(), ItemStack::isEmpty);
    }

    protected boolean hasNearbyItem() {
        return findOldestNearbyItem() != null;
    }

    @Nullable
    private ItemEntity findOldestNearbyItem() {
        return armyEntity.getEntityWorld().getEntities(
            ItemEntity.class,
            armyEntity.getBoundingBox().expand(searchDistance),
            itemEntity -> !itemEntity.isInvisible()
                && !itemEntity.cannotPickup()
                && this.armyEntity.getNavigation().findPathTo(itemEntity, 0) != null
                && this.armyEntity.isInWalkTargetRange(itemEntity.getBlockPos())
        ).stream().max(Comparator.comparing(ItemEntity::getAge)).orElse(null);
    }

    @Override
    public boolean shouldContinue() {
        return notInCombat() && hasEmptySlot() && hasItemTarget() && !this.armyEntity.getNavigation().isIdle();
    }

    protected boolean hasItemTarget() {
        return item != null
            && item.isAlive()
            && !item.isInvisible()
            && this.armyEntity.isInWalkTargetRange(item.getBlockPos());
    }

    @Override
    public void start() {
        super.start();
        this.postSwapCooldownTicks = 0;
        this.updateCountdownTicks = 0;
        this.item = findOldestNearbyItem();
        this.path = this.armyEntity.getNavigation().findPathTo(item, 0);
        this.armyEntity.getNavigation().startMovingAlong(this.path, this.speed);
    }

    @Override
    public void stop() {
        super.stop();
        this.armyEntity.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.armyEntity.getLookControl().lookAt(item, 30.0F, 30.0F);
        double squaredDistanceToItem = this.armyEntity.squaredDistanceTo(item.getX(), item.getY(), item.getZ());
        moveToItem(squaredDistanceToItem);
        if (this.postSwapCooldownTicks > 0) {
            this.postSwapCooldownTicks--;
            return;
        }
        if (swapToEmpty()) {
            return;
        }
        gatherItem(squaredDistanceToItem);
    }

    private void gatherItem(double squaredDistanceToItem) {
        if (squaredDistanceToItem <= getSquaredMaxPickupDistance(item) && this.armyEntity.getOffHandStack().isEmpty()) {
            Random random = this.armyEntity.getRandom();
            this.armyEntity.world.playSound(null, this.armyEntity.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.NEUTRAL, 0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            this.armyEntity.setStackInHand(Hand.OFF_HAND, item.getStack());
            item.remove();
            this.postSwapCooldownTicks = this.armyEntity.getEquipmentSwapTicks();
        }
    }

    protected double getSquaredMaxPickupDistance(ItemEntity entity) {
        return this.armyEntity.getWidth() * 2.0F * this.armyEntity.getWidth() * 2.0F + entity.getWidth();
    }

    private void moveToItem(double squaredDistanceToCow) {
        if (this.armyEntity.getVisibilityCache().canSee(item)
            && this.updateCountdownTicks <= 0
            && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D
            || item.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0D
            || this.armyEntity.getRandom().nextFloat() < 0.05F)
        ) {
            this.targetX = item.getX();
            this.targetY = item.getY();
            this.targetZ = item.getZ();
            this.updateCountdownTicks = 4 + this.armyEntity.getRandom().nextInt(7);
            if (squaredDistanceToCow > 1024.0D) {
                this.updateCountdownTicks += 10;
            } else if (squaredDistanceToCow > 256.0D) {
                this.updateCountdownTicks += 5;
            }

            if (!this.armyEntity.getNavigation().startMovingTo(item, this.speed)) {
                this.updateCountdownTicks += 15;
            }
        }
    }

    private boolean swapToEmpty() {
        if (!armyEntity.getOffHandStack().isEmpty()) {
            Inventory inventory = armyEntity.getInventory();
            int emptySlot = inventorySearcher.getFirstSlotMatching(inventory, ItemStack::isEmpty);
            int offHandSlot = armyEntity.getOffHandSlot();
            ItemStack oldOffHandStack = inventory.removeInvStack(offHandSlot);
            inventory.setInvStack(offHandSlot, ItemStack.EMPTY);
            inventory.setInvStack(emptySlot, oldOffHandStack);
            this.postSwapCooldownTicks = this.armyEntity.getEquipmentSwapTicks();
            return true;
        }
        return false;
    }
}
