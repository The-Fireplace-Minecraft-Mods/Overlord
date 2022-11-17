package dev.the_fireplace.overlord.mixin;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.util.SkeletonSpawnUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonEntityMixin extends Monster
{
    @Shadow
    public abstract void setItemSlot(EquipmentSlot slot, ItemStack stack);

    protected AbstractSkeletonEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "populateDefaultEquipmentSlots", at = @At("RETURN"))
    public void addSansMask(RandomSource randomSource, DifficultyInstance $$1, CallbackInfo ci) {
        OverlordConstants.getInjector().getInstance(SkeletonSpawnUtils.class).addMask(this);
    }
}
