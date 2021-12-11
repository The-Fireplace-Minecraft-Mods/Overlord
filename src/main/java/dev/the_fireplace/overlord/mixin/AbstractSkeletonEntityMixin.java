package dev.the_fireplace.overlord.mixin;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.util.SkeletonSpawnUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeletonEntity.class)
public abstract class AbstractSkeletonEntityMixin extends HostileEntity
{
    @Shadow
    public abstract void equipStack(EquipmentSlot slot, ItemStack stack);

    protected AbstractSkeletonEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initEquipment", at = @At("RETURN"))
    public void addSansMask(LocalDifficulty difficulty, CallbackInfo ci) {
        DIContainer.get().getInstance(SkeletonSpawnUtils.class).addMask(this);
    }
}
