package dev.the_fireplace.overlord.impl.world;

import dev.the_fireplace.overlord.api.world.MeleeAttackExecutor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public final class MeleeAttackExecutorImpl implements MeleeAttackExecutor {
    @Deprecated
    public static final MeleeAttackExecutor INSTANCE = new MeleeAttackExecutorImpl();

    private MeleeAttackExecutorImpl() {}

    @Override
    public void attack(LivingEntity attacker, Entity target) {
        attack(attacker, target, 1);
    }

    @Override
    public void attack(LivingEntity attacker, Entity target, float cooldownProgress) {
        if (!target.isAttackable() || target.handleAttack(attacker)) {
            return;
        }
        float baseAttackDamage = (float) attacker.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue();
        float attackDamage = EnchantmentHelper.getAttackDamage(
            attacker.getMainHandStack(),
            target instanceof LivingEntity
                ? ((LivingEntity) target).getGroup()
                : EntityGroup.DEFAULT
        );

        baseAttackDamage *= 0.2F + cooldownProgress * cooldownProgress * 0.8F;
        attackDamage *= cooldownProgress;
        if (baseAttackDamage <= 0.0F && attackDamage <= 0.0F) {
            return;
        }
        boolean isNearFullStrength = cooldownProgress > 0.9F;
        int knockbackAmount = EnchantmentHelper.getKnockback(attacker);

        boolean isPerformingFallingAttack = isNearFullStrength
            && attacker.fallDistance > 0.0F
            && !attacker.onGround
            && !attacker.isClimbing()
            && !attacker.isTouchingWater()
            && !attacker.hasStatusEffect(StatusEffects.BLINDNESS)
            && !attacker.hasVehicle()
            && target instanceof LivingEntity;
        if (isPerformingFallingAttack) {
            baseAttackDamage *= 1.5F;
        }

        baseAttackDamage += attackDamage;
        boolean isSweepingSword = false;
        double horizontalAcceleration = attacker.horizontalSpeed - attacker.prevHorizontalSpeed;
        if (isNearFullStrength
            && !isPerformingFallingAttack
            && attacker.onGround
            && horizontalAcceleration < (double) attacker.getMovementSpeed()
        ) {
            ItemStack itemStack = attacker.getStackInHand(Hand.MAIN_HAND);
            if (itemStack.getItem() instanceof SwordItem) {
                isSweepingSword = true;
            }
        }

        float preDamageTargetHealth = 0.0F;
        if (target instanceof LivingEntity) {
            preDamageTargetHealth = ((LivingEntity) target).getHealth();
        }

        final Vec3d preDamageTargetVelocity = target.getVelocity();
        boolean targetDamaged = target.damage(DamageSource.mob(attacker), baseAttackDamage);
        if (targetDamaged) {
            int fireAspectLevel = applyFireAspectToTarget(attacker, target);
            knockbackTarget(attacker, target, knockbackAmount);
            if (isSweepingSword) {
                dealSweepDamage(attacker, target, baseAttackDamage);
            }
            sendPlayerTargetVelocityUpdate(target, preDamageTargetVelocity);

            if (isPerformingFallingAttack) {
                spawnFallingAttackIndicators(attacker, target);
            } else if (!isSweepingSword) {
                playStandardAttackSound(attacker, isNearFullStrength);
            }

            if (attackDamage > 0.0F) {
                spawnStandardAttackParticles(attacker, target);
            }

            attacker.onAttacking(target);
            if (target instanceof LivingEntity) {
                EnchantmentHelper.onUserDamaged((LivingEntity) target, attacker);
            }

            EnchantmentHelper.onTargetDamaged(attacker, target);
            ItemStack mainHandStack = attacker.getMainHandStack();
            Entity targetEntity = target;
            if (target instanceof EnderDragonPart) {
                targetEntity = ((EnderDragonPart) target).owner;
            }

            if (!attacker.world.isClient && !mainHandStack.isEmpty() && targetEntity instanceof LivingEntity) {
                mainHandStack.getItem().postHit(mainHandStack, (LivingEntity) targetEntity, attacker);
                if (mainHandStack.isEmpty())
                    attacker.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            }

            if (target instanceof LivingEntity) {
                float damageDealt = preDamageTargetHealth - ((LivingEntity) target).getHealth();
                if (fireAspectLevel > 0) {
                    target.setOnFireFor(fireAspectLevel * 4);
                }

                if (attacker.getEntityWorld() instanceof ServerWorld && damageDealt > 2.0F) {
                    spawnDamageIndicatorParticles((ServerWorld) attacker.getEntityWorld(), target, damageDealt);
                }
            }
        } else {
            attacker.world.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, attacker.getSoundCategory(), 1.0F, 1.0F);
        }
    }

    private void spawnDamageIndicatorParticles(ServerWorld world, Entity target, double damageDealt) {
        int particleCount = (int) (damageDealt * 0.5D);
        world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getBodyY(0.5D), target.getZ(), particleCount, 0.1D, 0.0D, 0.1D, 0.2D);
    }

    private int applyFireAspectToTarget(LivingEntity attacker, Entity target) {
        int fireAspectLevel = EnchantmentHelper.getFireAspect(attacker);
        if (fireAspectLevel > 0 && !target.isOnFire()) {
            target.setOnFireFor(1);
        }
        return fireAspectLevel;
    }

    private void playStandardAttackSound(LivingEntity attacker, boolean isNearFullStrength) {
        if (isNearFullStrength) {
            attacker.world.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, attacker.getSoundCategory(), 1.0F, 1.0F);
        } else {
            attacker.world.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, attacker.getSoundCategory(), 1.0F, 1.0F);
        }
    }

    private void spawnFallingAttackIndicators(LivingEntity attacker, Entity target) {
        attacker.world.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, attacker.getSoundCategory(), 1.0F, 1.0F);
        addCritParticles(attacker, target);
    }

    private void sendPlayerTargetVelocityUpdate(Entity target, Vec3d targetVelocity) {
        if (target instanceof ServerPlayerEntity && target.velocityModified) {
            ((ServerPlayerEntity) target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
            target.velocityModified = false;
            target.setVelocity(targetVelocity);
        }
    }

    private void dealSweepDamage(LivingEntity attacker, Entity target, float baseAttackDamage) {
        float multiplier = 1.0F + EnchantmentHelper.getSweepingMultiplier(attacker) * baseAttackDamage;
        List<LivingEntity> entitiesInSweepRange = attacker.world.getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0D, 0.25D, 1.0D));
        for (LivingEntity livingEntity : entitiesInSweepRange) {
            if (
                livingEntity instanceof ArmorStandEntity && ((ArmorStandEntity) livingEntity).isMarker()
                    || attacker.isTeammate(livingEntity)
                    || livingEntity == target
                    || livingEntity == attacker
            ) {
                continue;
            }

            if (attacker.squaredDistanceTo(livingEntity) < 9.0D) {
                livingEntity.takeKnockback(attacker, 0.4F, MathHelper.sin(attacker.yaw * (float) Math.PI / 180), -MathHelper.cos(attacker.yaw * (float) Math.PI / 180));
                livingEntity.damage(DamageSource.mob(attacker), multiplier);
            }
        }

        attacker.world.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, attacker.getSoundCategory(), 1.0F, 1.0F);
        showSweepParticles(attacker);
    }

    private void knockbackTarget(LivingEntity attacker, Entity target, int knockback) {
        if (knockback > 0) {
            if (target instanceof LivingEntity)
                ((LivingEntity) target).takeKnockback(attacker, (float) knockback * 0.5F, MathHelper.sin(attacker.yaw * (float) Math.PI / 180), -MathHelper.cos(attacker.yaw * (float) Math.PI / 180));
            else
                target.addVelocity(-MathHelper.sin(attacker.yaw * (float) Math.PI / 180) * (float) knockback * 0.5F, 0.1D, MathHelper.cos(attacker.yaw * (float) Math.PI / 180) * (float) knockback * 0.5F);

            attacker.setVelocity(attacker.getVelocity().multiply(0.6D, 1.0D, 0.6D));
            attacker.setSprinting(false);
        }
    }

    public void addCritParticles(LivingEntity attacker, Entity target) {
        if(attacker.world instanceof ServerWorld)//TODO test that this works
            ((ServerWorld)attacker.world).getChunkManager().sendToNearbyPlayers(attacker, new EntityAnimationS2CPacket(target, 4));
    }

    public void spawnStandardAttackParticles(LivingEntity attacker, Entity target) {
        if(attacker.world instanceof ServerWorld)//TODO test that this works
            ((ServerWorld)attacker.world).getChunkManager().sendToNearbyPlayers(attacker, new EntityAnimationS2CPacket(target, 5));
    }

    public void showSweepParticles(LivingEntity attacker) {
        double d = -MathHelper.sin(attacker.yaw * (float) Math.PI/180);
        double e = MathHelper.cos(attacker.yaw * (float) Math.PI/180);
        if (attacker.world instanceof ServerWorld)
            ((ServerWorld)attacker.world).spawnParticles(ParticleTypes.SWEEP_ATTACK, attacker.getX() + d, attacker.getBodyY(0.5D), attacker.getZ() + e, 0, d, 0.0D, e, 0.0D);
    }
}
