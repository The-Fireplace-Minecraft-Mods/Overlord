package dev.the_fireplace.overlord.mixin;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.domain.entity.logic.EntityAlliances;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfEntity.class)
public class WolfEntityMixin
{
    @Inject(method = "canAttackWithOwner", at = @At("HEAD"), cancellable = true)
    private void doNotAttackAlliesWithOwner(LivingEntity target, LivingEntity owner, CallbackInfoReturnable<Boolean> cir) {
        if (DIContainer.get().getInstance(EntityAlliances.class).isAlliedTo((LivingEntity) (Object) this, target)) {
            cir.setReturnValue(false);
        }
    }
}
