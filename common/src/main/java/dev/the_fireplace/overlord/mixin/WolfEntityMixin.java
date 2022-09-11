package dev.the_fireplace.overlord.mixin;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.entity.logic.EntityAlliances;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public class WolfEntityMixin
{
    @Inject(method = "wantsToAttack", at = @At("HEAD"), cancellable = true)
    private void doNotAttackAlliesWithOwner(LivingEntity target, LivingEntity owner, CallbackInfoReturnable<Boolean> cir) {
        if (OverlordConstants.getInjector().getInstance(EntityAlliances.class).isAlliedTo((LivingEntity) (Object) this, target)) {
            cir.setReturnValue(false);
        }
    }
}
