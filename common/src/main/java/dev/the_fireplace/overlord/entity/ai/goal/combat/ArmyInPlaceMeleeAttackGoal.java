package dev.the_fireplace.overlord.entity.ai.goal.combat;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.goal.AIEquipmentHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

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
        this.equipmentHelper = OverlordConstants.getInjector().getInstance(AIEquipmentHelper.class);

        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    private boolean meleeCanStart() {
        long l = this.armyEntity.level.getGameTime();
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
        return this.getSquaredMaxAttackDistance(livingEntity) >= this.armyEntity.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
    }

    @Override
    public boolean canUse() {
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
            return !(livingEntity instanceof Player) || !livingEntity.isSpectator() && !((Player) livingEntity).isCreative();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return meleeShouldContinue() && shouldAttackWithMelee();
    }

    public void start() {
        this.armyEntity.setAggressive(true);
        this.updateCountdownTicks = 0;
        this.cooldown = 0;
    }

    public void stop() {
        LivingEntity livingEntity = this.armyEntity.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
            this.armyEntity.setTarget(null);
        }

        this.armyEntity.setAggressive(false);
        this.armyEntity.getNavigation().stop();
    }

    public void tick() {
        LivingEntity livingEntity = this.armyEntity.getTarget();
        this.armyEntity.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
        double d = this.armyEntity.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);
        if (this.armyEntity.getSensing().hasLineOfSight(livingEntity) && this.updateCountdownTicks <= 0 && this.armyEntity.getRandom().nextFloat() < 0.05F) {
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
            if (!this.armyEntity.getCommandSenderWorld().isClientSide()) {
                double attackSpeed = this.armyEntity.getAttributeValue(Attributes.ATTACK_SPEED);
                this.cooldown = Math.max(1, (int) Math.ceil(20D / attackSpeed - 0.5));
            }
            this.armyEntity.swing(InteractionHand.MAIN_HAND);
            this.armyEntity.doHurtTarget(target);
        }
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return this.armyEntity.getBbWidth() * 2.0F * this.armyEntity.getBbWidth() * 2.0F + entity.getBbWidth();
    }

    protected boolean isCooledDown() {
        return this.cooldown <= 0;
    }
}
