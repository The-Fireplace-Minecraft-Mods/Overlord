package dev.the_fireplace.overlord.impl.world;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.world.MeleeAttackExecutor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

import java.util.List;

@Implementation
public final class MeleeAttackExecutorImpl implements MeleeAttackExecutor
{
    @Override
    public void attack(LivingEntity attacker, Entity target) {
        attack(attacker, target, 1);
    }

    @Override
    public void attack(LivingEntity attacker, Entity target, float cooldownProgress) {
        if (!target.isAttackable() || target.skipAttackInteraction(attacker)) {
            return;
        }
        float baseAttackDamage = (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float enchantmentAttackDamage = EnchantmentHelper.getDamageBonus(
            attacker.getMainHandItem(),
            target instanceof LivingEntity
                ? ((LivingEntity) target).getMobType()
                : MobType.UNDEFINED
        );

        baseAttackDamage *= 0.2F + cooldownProgress * cooldownProgress * 0.8F;
        enchantmentAttackDamage *= cooldownProgress;
        if (baseAttackDamage <= 0.0F && enchantmentAttackDamage <= 0.0F) {
            return;
        }
        boolean isNearFullStrength = cooldownProgress > 0.9F;
        int knockbackAmount = EnchantmentHelper.getKnockbackBonus(attacker);

        boolean isPerformingFallingAttack = isNearFullStrength
            && attacker.fallDistance > 0.0F
            && !attacker.isOnGround()
            && !attacker.onClimbable()
            && !attacker.isInWater()
            && !attacker.hasEffect(MobEffects.BLINDNESS)
            && !attacker.isPassenger()
            && target instanceof LivingEntity;
        if (isPerformingFallingAttack) {
            baseAttackDamage *= 1.5F;
        }

        baseAttackDamage += enchantmentAttackDamage;
        boolean isSweepingSword = false;
        double horizontalAcceleration = attacker.walkDist - attacker.walkDistO;
        if (isNearFullStrength
            && !isPerformingFallingAttack
            && attacker.isOnGround()
            && horizontalAcceleration < (double) attacker.getSpeed()
        ) {
            ItemStack itemStack = attacker.getItemInHand(InteractionHand.MAIN_HAND);
            if (itemStack.getItem() instanceof SwordItem) {
                isSweepingSword = true;
            }
        }

        float preDamageTargetHealth = 0.0F;
        if (target instanceof LivingEntity) {
            preDamageTargetHealth = ((LivingEntity) target).getHealth();
        }

        final Vec3 preDamageTargetVelocity = target.getDeltaMovement();
        boolean targetDamaged = target.hurt(DamageSource.mobAttack(attacker), baseAttackDamage);
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

            if (enchantmentAttackDamage > 0.0F) {
                spawnStandardAttackParticles(attacker, target);
            }

            attacker.setLastHurtMob(target);
            if (target instanceof LivingEntity) {
                EnchantmentHelper.doPostHurtEffects((LivingEntity) target, attacker);
            }

            EnchantmentHelper.doPostDamageEffects(attacker, target);
            ItemStack mainHandStack = attacker.getMainHandItem();
            Entity targetEntity = target;
            if (target instanceof EnderDragonPart) {
                targetEntity = ((EnderDragonPart) target).parentMob;
            }

            if (!attacker.level.isClientSide && !mainHandStack.isEmpty() && targetEntity instanceof LivingEntity) {
                mainHandStack.getItem().hurtEnemy(mainHandStack, (LivingEntity) targetEntity, attacker);
                if (mainHandStack.isEmpty()) {
                    attacker.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                }
            }

            if (target instanceof LivingEntity) {
                float damageDealt = preDamageTargetHealth - ((LivingEntity) target).getHealth();
                if (fireAspectLevel > 0) {
                    target.setSecondsOnFire(fireAspectLevel * 4);
                }

                if (attacker.getCommandSenderWorld() instanceof ServerLevel && damageDealt > 2.0F) {
                    spawnDamageIndicatorParticles((ServerLevel) attacker.getCommandSenderWorld(), target, damageDealt);
                }
            }
        } else {
            attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, attacker.getSoundSource(), 1.0F, 1.0F);
        }
    }

    private void spawnDamageIndicatorParticles(ServerLevel world, Entity target, double damageDealt) {
        int particleCount = (int) (damageDealt * 0.5D);
        world.sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5D), target.getZ(), particleCount, 0.1D, 0.0D, 0.1D, 0.2D);
    }

    private int applyFireAspectToTarget(LivingEntity attacker, Entity target) {
        int fireAspectLevel = EnchantmentHelper.getFireAspect(attacker);
        if (fireAspectLevel > 0 && !target.isOnFire()) {
            target.setSecondsOnFire(1);
        }
        return fireAspectLevel;
    }

    private void playStandardAttackSound(LivingEntity attacker, boolean isNearFullStrength) {
        if (isNearFullStrength) {
            attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, attacker.getSoundSource(), 1.0F, 1.0F);
        } else {
            attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, attacker.getSoundSource(), 1.0F, 1.0F);
        }
    }

    private void spawnFallingAttackIndicators(LivingEntity attacker, Entity target) {
        attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, attacker.getSoundSource(), 1.0F, 1.0F);
        addCritParticles(attacker, target);
    }

    private void sendPlayerTargetVelocityUpdate(Entity target, Vec3 targetVelocity) {
        if (target instanceof ServerPlayer && target.hurtMarked) {
            ((ServerPlayer) target).connection.send(new ClientboundSetEntityMotionPacket(target));
            target.hurtMarked = false;
            target.setDeltaMovement(targetVelocity);
        }
    }

    private void dealSweepDamage(LivingEntity attacker, Entity target, float baseAttackDamage) {
        float multiplier = 1.0F + EnchantmentHelper.getSweepingDamageRatio(attacker) * baseAttackDamage;
        List<LivingEntity> entitiesInSweepRange = attacker.level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(1.0D, 0.25D, 1.0D));
        for (LivingEntity livingEntity : entitiesInSweepRange) {
            if (
                livingEntity instanceof ArmorStand && ((ArmorStand) livingEntity).isMarker()
                    || attacker.isAlliedTo(livingEntity)
                    || livingEntity == target
                    || livingEntity == attacker
            ) {
                continue;
            }

            if (attacker.distanceToSqr(livingEntity) < 9.0D) {
                livingEntity.knockback(0.4F, Mth.sin(attacker.yRot * (float) Math.PI / 180), -Mth.cos(attacker.yRot * (float) Math.PI / 180));
                livingEntity.hurt(DamageSource.mobAttack(attacker), multiplier);
            }
        }

        attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, attacker.getSoundSource(), 1.0F, 1.0F);
        showSweepParticles(attacker);
    }

    private void knockbackTarget(LivingEntity attacker, Entity target, int knockback) {
        if (knockback > 0) {
            if (target instanceof LivingEntity) {
                ((LivingEntity) target).knockback((float) knockback * 0.5F, Mth.sin(attacker.yRot * (float) Math.PI / 180), -Mth.cos(attacker.yRot * (float) Math.PI / 180));
            } else {
                target.push(-Mth.sin(attacker.yRot * (float) Math.PI / 180) * (float) knockback * 0.5F, 0.1D, Mth.cos(attacker.yRot * (float) Math.PI / 180) * (float) knockback * 0.5F);
            }

            attacker.setDeltaMovement(attacker.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            attacker.setSprinting(false);
        }
    }

    public void addCritParticles(LivingEntity attacker, Entity target) {
        if (attacker.level instanceof ServerLevel)//TODO test that this works
        {
            ((ServerLevel) attacker.level).getChunkSource().broadcastAndSend(attacker, new ClientboundAnimatePacket(target, 4));
        }
    }

    public void spawnStandardAttackParticles(LivingEntity attacker, Entity target) {
        if (attacker.level instanceof ServerLevel)//TODO test that this works
        {
            ((ServerLevel) attacker.level).getChunkSource().broadcastAndSend(attacker, new ClientboundAnimatePacket(target, 5));
        }
    }

    public void showSweepParticles(LivingEntity attacker) {
        double d = -Mth.sin(attacker.yRot * (float) Math.PI / 180);
        double e = Mth.cos(attacker.yRot * (float) Math.PI / 180);
        if (attacker.level instanceof ServerLevel) {
            ((ServerLevel) attacker.level).sendParticles(ParticleTypes.SWEEP_ATTACK, attacker.getX() + d, attacker.getY(0.5D), attacker.getZ() + e, 0, d, 0.0D, e, 0.0D);
        }
    }
}
