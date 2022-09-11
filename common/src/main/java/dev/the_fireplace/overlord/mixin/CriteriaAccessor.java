package dev.the_fireplace.overlord.mixin;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CriteriaTriggers.class)
public interface CriteriaAccessor
{
    @Invoker
    static <T extends CriterionTrigger<?>> T callRegister(T criterion) {
        return criterion;
    }
}
