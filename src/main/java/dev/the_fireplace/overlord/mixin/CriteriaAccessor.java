package dev.the_fireplace.overlord.mixin;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Criteria.class)
public interface CriteriaAccessor
{
    @Invoker
    static <T extends Criterion<?>> T callRegister(T criterion) {
        return criterion;
    }
}
