package dev.the_fireplace.overlord.entity.ai.goal.combat;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.goal.AIEquipmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.BowItem;
import net.minecraft.util.Hand;

import java.util.EnumSet;

public class ArmyBowAttackGoal<T extends ArmyEntity & RangedAttackMob> extends Goal
{
    private final T armyEntity;
    private final double speed;
    private int attackInterval;
    private final float squaredRange;
    private int cooldown = -1;
    private int targetSeeingTicker;
    private boolean movingToLeft;
    private boolean backward;
    private int combatTicks = -1;
    protected final AIEquipmentHelper equipmentHelper;
    private long lastUpdateTime;

    public ArmyBowAttackGoal(T armyEntity, double speed, int attackInterval, float range) {
        this.armyEntity = armyEntity;
        this.speed = speed;
        this.attackInterval = attackInterval;
        this.squaredRange = range * range;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        this.equipmentHelper = DIContainer.get().getInstance(AIEquipmentHelper.class);
    }

    public void setAttackInterval(int attackInterval) {
        this.attackInterval = attackInterval;
    }

    @Override
    public boolean canStart() {
        long worldTime = this.armyEntity.world.getTime();
        if (worldTime - this.lastUpdateTime < 20) {
            return false;
        }
        this.lastUpdateTime = worldTime;
        return this.armyEntity.getTarget() != null && this.isHoldingBow();
    }

    protected boolean isHoldingBow() {
        return this.armyEntity.getMainHandStack().getItem() instanceof BowItem && equipmentHelper.hasAmmoEquipped(this.armyEntity);
    }

    @Override
    public boolean shouldContinue() {
        return (this.canStart() || !this.armyEntity.getNavigation().isIdle()) && this.isHoldingBow();
    }

    @Override
    public void start() {
        super.start();
        this.armyEntity.setAttacking(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.armyEntity.setAttacking(false);
        this.targetSeeingTicker = 0;
        this.cooldown = -1;
        this.armyEntity.clearActiveItem();
    }

    @Override
    public void tick() {
        LivingEntity target = this.armyEntity.getTarget();
        if (target == null) {
            return;
        }
        double squaredDistanceToTarget = this.armyEntity.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
        boolean canSeeTarget = this.armyEntity.getVisibilityCache().canSee(target);
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
            this.armyEntity.getNavigation().stop();
            ++this.combatTicks;
        } else {
            this.armyEntity.getNavigation().startMovingTo(target, this.speed);
            this.combatTicks = -1;
        }

        if (this.combatTicks >= 20) {
            if ((double) this.armyEntity.getRandom().nextFloat() < 0.3D) {
                this.movingToLeft = !this.movingToLeft;
            }

            if ((double) this.armyEntity.getRandom().nextFloat() < 0.3D) {
                this.backward = !this.backward;
            }

            this.combatTicks = 0;
        }

        if (this.combatTicks > -1) {
            if (squaredDistanceToTarget > (double) (this.squaredRange * 0.75F)) {
                this.backward = false;
            } else if (squaredDistanceToTarget < (double) (this.squaredRange * 0.25F)) {
                this.backward = true;
            }

            this.armyEntity.getMoveControl().strafeTo(this.backward ? -0.5F : 0.5F, this.movingToLeft ? 0.5F : -0.5F);
            this.armyEntity.lookAtEntity(target, 30.0F, 30.0F);
        } else {
            this.armyEntity.getLookControl().lookAt(target, 30.0F, 30.0F);
        }

        if (this.armyEntity.isUsingItem()) {
            if (!canSeeTarget && this.targetSeeingTicker < -60) {
                this.armyEntity.clearActiveItem();
            } else if (canSeeTarget) {
                int itemUseTime = this.armyEntity.getItemUseTime();
                if (itemUseTime >= 20) {
                    this.armyEntity.clearActiveItem();
                    this.armyEntity.attack(target, BowItem.getPullProgress(itemUseTime));
                    this.cooldown = this.attackInterval;
                }
            }
        } else if (--this.cooldown <= 0 && this.targetSeeingTicker >= -60) {
            this.armyEntity.setCurrentHand(Hand.MAIN_HAND);
        }

    }
}
