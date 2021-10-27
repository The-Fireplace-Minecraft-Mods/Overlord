package dev.the_fireplace.overlord.mixin;

import dev.the_fireplace.overlord.init.OverlordBlocks;
import dev.the_fireplace.overlord.tags.OverlordBlockTags;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
    @Shadow
    public abstract boolean isUndead();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onKilledBy", at = @At("RETURN"))
    public void spawnBloodSoakedSoil(@Nullable LivingEntity adversary, CallbackInfo ci) {
        if (!world.isClient() && !isUndead() && adversary != null && !adversary.getActiveItem().isEmpty()) {
            if (FabricToolTags.SHOVELS.contains(adversary.getActiveItem().getItem())) {
                BlockPos pos = getBlockPos().down();
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++) {
                        BlockPos pos2 = pos.add(x, 0, z);
                        if (world.getBlockState(pos2).getBlock().matches(OverlordBlockTags.DIRT)) {
                            world.setBlockState(pos2, OverlordBlocks.BLOOD_SOAKED_SOIL.getDefaultState());
                        }
                    }
                }
            }
        }
    }
}
