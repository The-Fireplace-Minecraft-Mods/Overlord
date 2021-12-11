package dev.the_fireplace.overlord.entity.ai.goal.combat;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.goal.AIEquipmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.Hand;

import java.util.EnumSet;

public class ArmyInPlaceMeleeAttackGoal extends Goal
{
    protected final ArmyEntity armyEntity;
    protected final AIEquipmentHelper equipmentHelper;

    private int updateCountdownTicks;
    private int cooldown;
    private long lastUpdateTime;

    public ArmyInPlaceMeleeAttackGoal(ArmyEntity armyEntity) {
        super();
        this.armyEntity = armyEntity;
        this.equipmentHelper = DIContainer.get().getInstance(AIEquipmentHelper.class);

        this.setControls(EnumSet.of(Control.LOOK));
    }

    private boolean meleeCanStart() {
        long l = this.armyEntity.world.getTime();
        if (l - this.lastUpdateTime < 20L) {
            return false;
        } else {
            this.lastUpdateTime = l;
            LivingEntity livingEntity = this.armyEntity.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else {
                return isWithinAttackDistance(livingEntity);
            }
        }
    }

    private boolean isWithinAttackDistance(LivingEntity livingEntity) {
        return this.getSquaredMaxAttackDistance(livingEntity) >= this.armyEntity.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
    }

    @Override
    public boolean canStart() {
        return meleeCanStart() && shouldAttackWithMelee();
    }

    private boolean shouldAttackWithMelee() {
        return !equipmentHelper.isUsingRanged(armyEntity) || !equipmentHelper.hasAmmoEquipped(armyEntity);
    }

    private boolean meleeShouldContinue() {
        LivingEntity livingEntity = this.armyEntity.getTarget();
        if (livingEntity == null) {
            return false;
        } else if (!livingEntity.isAlive()) {
            return false;
        } else if (!this.isWithinAttackDistance(livingEntity)) {
            return false;
        } else {
            return !(livingEntity instanceof PlayerEntity) || !livingEntity.isSpectator() && !((PlayerEntity) livingEntity).isCreative();
        }
    }

    @Override
    public boolean shouldContinue() {
        return meleeShouldContinue() && shouldAttackWithMelee();
    }

    public void start() {
        this.armyEntity.setAttacking(true);
        this.updateCountdownTicks = 0;
        this.cooldown = 0;
    }

    public void stop() {
        LivingEntity livingEntity = this.armyEntity.getTarget();
        if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
            this.armyEntity.setTarget(null);
        }

        this.armyEntity.setAttacking(false);
        this.armyEntity.getNavigation().stop();
    }

    public void tick() {
        LivingEntity livingEntity = this.armyEntity.getTarget();
        this.armyEntity.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
        double d = this.armyEntity.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);
        if (this.armyEntity.getVisibilityCache().canSee(livingEntity) && this.updateCountdownTicks <= 0 && this.armyEntity.getRandom().nextFloat() < 0.05F) {
            this.updateCountdownTicks = 4 + this.armyEntity.getRandom().nextInt(7);
            if (d > 1024.0D) {
                this.updateCountdownTicks += 10;
            } else if (d > 256.0D) {
                this.updateCountdownTicks += 5;
            }
        }

        this.cooldown = Math.max(this.cooldown - 1, 0);
        this.attack(livingEntity, d);
    }

    protected void attack(LivingEntity target, double squaredDistance) {
        double d = this.getSquaredMaxAttackDistance(target);
        if (squaredDistance <= d && this.isCooledDown()) {
            if (!this.armyEntity.getEntityWorld().isClient()) {
                double attackSpeed = this.armyEntity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED);
                this.cooldown = Math.max(1, (int) Math.ceil(20D / attackSpeed - 0.5));
            }
            this.armyEntity.swingHand(Hand.MAIN_HAND);
            this.armyEntity.tryAttack(target);
        }
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return this.armyEntity.getWidth() * 2.0F * this.armyEntity.getWidth() * 2.0F + entity.getWidth();
    }

    protected boolean isCooledDown() {
        return this.cooldown <= 0;
    }
}
