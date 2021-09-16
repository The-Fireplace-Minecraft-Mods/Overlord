package dev.the_fireplace.overlord.impl.world;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.world.BreakSpeedModifiers;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.FluidTags;

import java.util.Objects;

@Implementation
public final class BreakSpeedModifiersImpl implements BreakSpeedModifiers {
    @Override
    public float applyApplicable(LivingEntity entity, float baseBreakSpeed) {
        float breakSpeed = applyEfficiencyToBreakSpeed(entity, baseBreakSpeed);
        breakSpeed = applyHasteToBreakSpeed(entity, breakSpeed);
        breakSpeed = applyMiningFatigueToBreakSpeed(entity, breakSpeed);
        breakSpeed = applyWaterToBreakSpeed(entity, breakSpeed);
        breakSpeed = applyOffGroundToBreakSpeed(entity, breakSpeed);

        return breakSpeed;
    }

    private float applyEfficiencyToBreakSpeed(LivingEntity entity, float breakSpeed) {
        if (breakSpeed > 1.0F) {
            int efficiencyLevel = EnchantmentHelper.getEfficiency(entity);
            ItemStack itemStack = entity.getMainHandStack();
            if (efficiencyLevel > 0 && !itemStack.isEmpty()) {
                breakSpeed += (float) (efficiencyLevel * efficiencyLevel + 1);
            }
        }
        return breakSpeed;
    }

    private float applyHasteToBreakSpeed(LivingEntity entity, float breakSpeed) {
        if (StatusEffectUtil.hasHaste(entity)) {
            breakSpeed *= 1.0F + (float) (StatusEffectUtil.getHasteAmplifier(entity) + 1) * 0.2F;
        }
        return breakSpeed;
    }

    private float applyMiningFatigueToBreakSpeed(LivingEntity entity, float breakSpeed) {
        if (entity.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            float fatigueMultiplier;
            switch (Objects.requireNonNull(entity.getStatusEffect(StatusEffects.MINING_FATIGUE)).getAmplifier()) {
                case 0:
                    fatigueMultiplier = 0.3F;
                    break;
                case 1:
                    fatigueMultiplier = 0.09F;
                    break;
                case 2:
                    fatigueMultiplier = 0.0027F;
                    break;
                case 3:
                default:
                    fatigueMultiplier = 8.1E-4F;
            }

            breakSpeed *= fatigueMultiplier;
        }

        return breakSpeed;
    }

    private float applyWaterToBreakSpeed(LivingEntity entity, float breakSpeed) {
        if (entity.isInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(entity)) {
            breakSpeed /= 5.0F;
        }

        return breakSpeed;
    }

    private float applyOffGroundToBreakSpeed(LivingEntity entity, float breakSpeed) {
        if (!entity.onGround) {
            breakSpeed /= 5.0F;
        }

        return breakSpeed;
    }
}
