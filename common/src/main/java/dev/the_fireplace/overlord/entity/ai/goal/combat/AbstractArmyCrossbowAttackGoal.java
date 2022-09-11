package dev.the_fireplace.overlord.entity.ai.goal.combat;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.goal.AIEquipmentHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractArmyCrossbowAttackGoal<T extends ArmyEntity & RangedAttackMob & CrossbowAttackMob> extends Goal
{
    protected final T armyEntity;
    protected final float squaredRange;
    protected final AIEquipmentHelper equipmentHelper;
    protected Stage stage;
    protected int seeingTargetTicker;
    protected int chargedTicksLeft;
    protected long lastUpdateTime;

    public AbstractArmyCrossbowAttackGoal(T armyEntity, float range) {
        this.armyEntity = armyEntity;
        this.squaredRange = range * range;
        this.equipmentHelper = OverlordConstants.getInjector().getInstance(AIEquipmentHelper.class);

        this.stage = Stage.UNCHARGED;
    }

    @Override
    public boolean canUse() {
        long worldTime = this.armyEntity.level.getGameTime();
        if (worldTime - this.lastUpdateTime < 20) {
            return false;
        }
        this.lastUpdateTime = worldTime;
        return this.hasAliveTarget() && this.isEntityHoldingCrossbow();
    }

    private boolean isEntityHoldingCrossbow() {
        return this.armyEntity.getMainHandItem().getItem() instanceof CrossbowItem
            && (equipmentHelper.hasAmmoEquipped(this.armyEntity) || CrossbowItem.isCharged(this.armyEntity.getMainHandItem()));
    }

    @Override
    public boolean canContinueToUse() {
        return this.hasAliveTarget() && this.isEntityHoldingCrossbow();
    }

    private boolean hasAliveTarget() {
        return this.armyEntity.getTarget() != null && this.armyEntity.getTarget().isAlive();
    }

    @Override
    public void stop() {
        this.armyEntity.setAggressive(false);
        this.armyEntity.setTarget(null);
        this.seeingTargetTicker = 0;
        if (this.armyEntity.isUsingItem()) {
            this.armyEntity.stopUsingItem();
            this.armyEntity.setChargingCrossbow(false);
            CrossbowItem.setCharged(this.armyEntity.getUseItem(), false);
        }
    }

    @Override
    public void tick() {
        LivingEntity target = this.armyEntity.getTarget();
        if (target == null) {
            return;
        }
        boolean canSeeTarget = this.armyEntity.getSensing().canSee(target);
        boolean remembersSeeingTarget = this.seeingTargetTicker > 0;
        if (canSeeTarget != remembersSeeingTarget) {
            this.seeingTargetTicker = 0;
        }

        if (canSeeTarget) {
            ++this.seeingTargetTicker;
        } else {
            --this.seeingTargetTicker;
        }

        double squaredDistanceToTarget = this.armyEntity.distanceToSqr(target);
        boolean targetIsOutOfRange = (squaredDistanceToTarget > (double) this.squaredRange || this.seeingTargetTicker < 5) && this.chargedTicksLeft == 0;
        handleMoveToTarget(target, targetIsOutOfRange);

        this.armyEntity.getLookControl().setLookAt(target, 30.0F, 30.0F);
        if (this.stage == Stage.UNCHARGED) {
            if (!targetIsOutOfRange) {
                this.armyEntity.startUsingItem(InteractionHand.MAIN_HAND);
                this.stage = Stage.CHARGING;
                this.armyEntity.setChargingCrossbow(true);
            }
        } else if (this.stage == Stage.CHARGING) {
            if (!this.armyEntity.isUsingItem()) {
                this.stage = Stage.UNCHARGED;
            }

            int itemUseTime = this.armyEntity.getTicksUsingItem();
            ItemStack crossbowStack = this.armyEntity.getUseItem();
            if (itemUseTime >= CrossbowItem.getChargeDuration(crossbowStack)) {
                this.armyEntity.releaseUsingItem();
                this.stage = Stage.CHARGED;
                this.chargedTicksLeft = 20 + this.armyEntity.getRandom().nextInt(20);
                this.armyEntity.setChargingCrossbow(false);
            }
        } else if (this.stage == Stage.CHARGED) {
            --this.chargedTicksLeft;
            if (this.chargedTicksLeft == 0) {
                this.stage = Stage.READY_TO_ATTACK;
            }
        } else if (this.stage == Stage.READY_TO_ATTACK && canSeeTarget) {
            this.armyEntity.performRangedAttack(target, 1.0F);
            ItemStack crossbowStack = this.armyEntity.getItemInHand(InteractionHand.MAIN_HAND);
            CrossbowItem.setCharged(crossbowStack, false);
            this.stage = Stage.UNCHARGED;
        }
    }

    protected void handleMoveToTarget(LivingEntity target, boolean targetIsOutOfRange) {

    }

    protected boolean isUncharged() {
        return this.stage == Stage.UNCHARGED;
    }

    enum Stage
    {
        UNCHARGED,
        CHARGING,
        CHARGED,
        READY_TO_ATTACK
    }
}
