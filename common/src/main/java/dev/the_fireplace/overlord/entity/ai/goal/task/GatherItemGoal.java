package dev.the_fireplace.overlord.entity.ai.goal.task;

import dev.the_fireplace.overlord.entity.ArmyEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.Path;

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
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return super.canUse()
            && hasEmptySlot()
            && hasNearbyItem();
    }

    protected boolean hasEmptySlot() {
        return inventorySearcher.hasSlotMatching(armyEntity.getInventory(), ItemStack::isEmpty);
    }

    protected boolean hasNearbyItem() {
        return findOldestNearbyItem() != null;
    }

    private ItemEntity findOldestNearbyItem() {
        return armyEntity.getCommandSenderWorld().getEntitiesOfClass(
            ItemEntity.class,
            armyEntity.getBoundingBox().inflate(searchDistance),
            itemEntity -> !itemEntity.isInvisible()
                && !itemEntity.hasPickUpDelay()
                && this.armyEntity.getNavigation().createPath(itemEntity, 0) != null
                && this.armyEntity.isWithinRestriction(itemEntity.blockPosition())
        ).stream().max(Comparator.comparing(ItemEntity::getAge)).orElse(null);
    }

    @Override
    public boolean canContinueToUse() {
        return notInCombat() && hasEmptySlot() && hasItemTarget() && !this.armyEntity.getNavigation().isDone();
    }

    protected boolean hasItemTarget() {
        return item != null
            && item.isAlive()
            && !item.isInvisible()
            && this.armyEntity.isWithinRestriction(item.blockPosition());
    }

    @Override
    public void start() {
        super.start();
        this.postSwapCooldownTicks = 0;
        this.updateCountdownTicks = 0;
        this.item = findOldestNearbyItem();
        this.path = this.armyEntity.getNavigation().createPath(item, 0);
        this.armyEntity.getNavigation().moveTo(this.path, this.speed);
    }

    @Override
    public void stop() {
        super.stop();
        this.armyEntity.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.armyEntity.getLookControl().setLookAt(item, 30.0F, 30.0F);
        double squaredDistanceToItem = this.armyEntity.distanceToSqr(item.getX(), item.getY(), item.getZ());
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
        if (squaredDistanceToItem <= getSquaredMaxPickupDistance(item) && this.armyEntity.getOffhandItem().isEmpty()) {
            Random random = this.armyEntity.getRandom();
            this.armyEntity.level.playSound(null, this.armyEntity.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            this.armyEntity.setItemInHand(InteractionHand.OFF_HAND, item.getItem());
            item.discard();
            this.postSwapCooldownTicks = this.armyEntity.getEquipmentSwapTicks();
        }
    }

    protected double getSquaredMaxPickupDistance(ItemEntity entity) {
        return this.armyEntity.getBbWidth() * 2.0F * this.armyEntity.getBbWidth() * 2.0F + entity.getBbWidth();
    }

    private void moveToItem(double squaredDistanceToCow) {
        if (this.armyEntity.getSensing().hasLineOfSight(item)
            && this.updateCountdownTicks <= 0
            && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D
            || item.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0D
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

            if (!this.armyEntity.getNavigation().moveTo(item, this.speed)) {
                this.updateCountdownTicks += 15;
            }
        }
    }

    private boolean swapToEmpty() {
        if (!armyEntity.getOffhandItem().isEmpty()) {
            Container inventory = armyEntity.getInventory();
            int emptySlot = inventorySearcher.getFirstSlotMatching(inventory, ItemStack::isEmpty);
            int offHandSlot = armyEntity.getOffHandSlot();
            ItemStack oldOffHandStack = inventory.removeItemNoUpdate(offHandSlot);
            inventory.setItem(offHandSlot, ItemStack.EMPTY);
            inventory.setItem(emptySlot, oldOffHandStack);
            this.postSwapCooldownTicks = this.armyEntity.getEquipmentSwapTicks();
            return true;
        }
        return false;
    }
}
