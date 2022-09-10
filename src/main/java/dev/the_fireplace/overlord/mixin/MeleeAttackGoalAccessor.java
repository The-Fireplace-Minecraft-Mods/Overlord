package dev.the_fireplace.overlord.mixin;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MeleeAttackGoal.class)
public interface MeleeAttackGoalAccessor
{
    @Accessor
    void setCooldown(int ticksUntilAttack);
}