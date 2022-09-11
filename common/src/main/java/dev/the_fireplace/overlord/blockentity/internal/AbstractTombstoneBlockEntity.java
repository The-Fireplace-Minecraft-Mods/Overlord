package dev.the_fireplace.overlord.blockentity.internal;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.blockentity.CasketBlockEntity;
import dev.the_fireplace.overlord.domain.blockentity.Tombstone;
import dev.the_fireplace.overlord.domain.entity.creation.SkeletonBuilder;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.entity.ai.aiconfig.movement.PositionSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.material.Material;

public abstract class AbstractTombstoneBlockEntity extends BlockEntity implements Tombstone, TickableBlockEntity
{
    protected final SkeletonBuilder skeletonBuilder;

    public AbstractTombstoneBlockEntity(BlockEntityType<?> type) {
        super(type);
        this.skeletonBuilder = OverlordConstants.getInjector().getInstance(SkeletonBuilder.class);
    }

    public void tick() {
        Level world = this.level;
        if (world == null || world.isClientSide()) {
            return;
        }
        if (!isNearMidnight(world)) {
            return;
        }
        Direction facing = world.getBlockState(getBlockPos()).getValue(HorizontalDirectionalBlock.FACING);
        BlockPos casketPos = this.getBlockPos().relative(facing).below(2);
        BlockEntity blockEntity = world.getBlockEntity(casketPos);
        if (!(blockEntity instanceof CasketBlockEntity)) {
            return;
        }
        CasketBlockEntity casketEntity = (CasketBlockEntity) blockEntity;
        BlockPos soilPos1 = casketPos.above();
        BlockPos soilPos2 = soilPos1.relative(facing);
        if (!isSoil(world, soilPos1) && !isSoil(world, soilPos2)) {
            return;
        }
        if (!world.canSeeSky(soilPos1.above()) || !world.canSeeSky(soilPos2.above()) || !world.canSeeSky(getBlockPos().above())) {
            return;
        }
        if (!skeletonBuilder.canBuildWithIngredients(casketEntity)) {
            return;
        }
        OwnedSkeletonEntity skeleton = skeletonBuilder.build(casketEntity, casketEntity.getLevel(), this);
        skeleton.absMoveTo(soilPos1.getX(), soilPos1.getY() + 1, soilPos1.getZ(), skeleton.yRot, skeleton.xRot);
        skeleton.getAISettings().getMovement().setHome(new PositionSetting(soilPos1.getX(), soilPos1.getY() + 1, soilPos1.getZ()));
        world.addFreshEntity(skeleton);
        //TODO dirt particles around skeleton
    }

    private static boolean isSoil(Level world, BlockPos blockPos) {
        return world.getBlockState(blockPos).getMaterial().equals(Material.DIRT);
    }

    private static boolean isNearMidnight(Level world) {
        if (world == null) {
            return false;
        }
        int dayLength = 24000;
        long timeOfDay = world.getDayTime() % dayLength;
        return timeOfDay >= 17500 && timeOfDay <= 18500 && (timeOfDay + 100) % 200 == 0;
    }
}
