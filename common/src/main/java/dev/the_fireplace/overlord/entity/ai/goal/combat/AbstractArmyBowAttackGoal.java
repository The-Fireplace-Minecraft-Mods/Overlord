package dev.the_fireplace.overlord.entity.ai.goal.combat;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.goal.AIEquipmentHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.BowItem;

public abstract class AbstractArmyBowAttackGoal<T extends ArmyEntity & RangedAttackMob> extends Goal
{
    protected final T armyEntity;
    protected int attackInterval;
    protected final float squaredRange;
    protected int cooldown = -1;
    protected int targetSeeingTicker;
    protected int combatTicks = -1;
    protected final AIEquipmentHelper equipmentHelper;
    protected long lastUpdateTime;

    public AbstractArmyBowAttackGoal(T armyEntity, int attackInterval, float range) {
        this.armyEntity = armyEntity;
        this.attackInterval = attackInterval;
        this.squaredRange = range * range;
        this.equipmentHelper = OverlordConstants.getInjector().getInstance(AIEquipmentHelper.class);
    }

    public void setAttackInterval(int attackInterval) {
        this.attackInterval = attackInterval;
    }

    @Override
    public boolean canUse() {
        long worldTime = this.armyEntity.level.getGameTime();
        if (worldTime - this.lastUpdateTime < 20) {
            return false;
        }
        this.lastUpdateTime = worldTime;
        return this.hasAliveTarget() && this.isHoldingBow();
    }

    protected boolean isHoldingBow() {
        return this.armyEntity.getMainHandItem().getItem() instanceof BowItem && equipmentHelper.hasAmmoEquipped(this.armyEntity);
    }

    private boolean hasAliveTarget() {
        return this.armyEntity.getTarget() != null && this.armyEntity.getTarget().isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        return this.hasAliveTarget() && this.isHoldingBow();
    }

    @Override
    public void start() {
        super.start();
        this.armyEntity.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.armyEntity.setAggressive(false);
        this.targetSeeingTicker = 0;
        this.cooldown = -1;
        this.armyEntity.stopUsingItem();
    }

    @Override
    public void tick() {
        LivingEntity target = this.armyEntity.getTarget();
        if (target == null) {
            return;
        }
        double squaredDistanceToTarget = this.armyEntity.distanceToSqr(target.getX(), target.getY(), target.getZ());
        boolean canSeeTarget = this.armyEntity.getSensing().hasLineOfSight(target);
        boolean remembersSeeingTarget = this.targetSeeingTicker > 0;
        if (canSeeTarget != remembersSeeingTarget) {
            this.targetSeeingTicker = 0;
        }

        if (canSeeTarget) {
            ++this.targetSeeingTicker;
        } else {
            --this.targetSeeingTicker;
        }

        if (!(squaredDistanceToTarget > (double) this.squaredRange) && this.targetSeeingTicker >= 20) {
            ++this.combatTicks;
        } else {
            this.combatTicks = -1;
        }

        handleCombatMovement(target, squaredDistanceToTarget);

        if (this.combatTicks > -1) {
            this.armyEntity.lookAt(target, 30.0F, 30.0F);
        } else {
            this.armyEntity.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        if (this.armyEntity.isUsingItem()) {
            if (!canSeeTarget && this.targetSeeingTicker < -60) {
                this.armyEntity.stopUsingItem();
            } else if (canSeeTarget) {
                int itemUseTime = this.armyEntity.getTicksUsingItem();
                if (itemUseTime >= 20) {
                    this.armyEntity.stopUsingItem();
                    this.armyEntity.performRangedAttack(target, BowItem.getPowerForTime(itemUseTime));
                    this.cooldown = this.attackInterval;
                }
            }
        } else if (--this.cooldown <= 0 && this.targetSeeingTicker >= -60) {
            this.armyEntity.startUsingItem(InteractionHand.MAIN_HAND);
        }

    }

    protected void handleCombatMovement(LivingEntity target, double squaredDistanceToTarget) {

    }
}
