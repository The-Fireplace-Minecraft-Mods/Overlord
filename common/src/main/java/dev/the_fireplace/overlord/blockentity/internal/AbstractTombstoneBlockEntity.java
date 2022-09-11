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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public abstract class AbstractTombstoneBlockEntity extends BlockEntity implements Tombstone
{
    protected final SkeletonBuilder skeletonBuilder;

    public AbstractTombstoneBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.skeletonBuilder = OverlordConstants.getInjector().getInstance(SkeletonBuilder.class);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, AbstractTombstoneBlockEntity be) {
        if (world.isClientSide()) {
            return;
        }
        if (!isNearMidnight(world)) {
            return;
        }
        Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
        BlockPos casketPos = pos.relative(facing).below(2);
        BlockEntity blockEntityAtCasketPos = world.getBlockEntity(casketPos);
        if (!(blockEntityAtCasketPos instanceof CasketBlockEntity)) {
            return;
        }
        CasketBlockEntity casketEntity = (CasketBlockEntity) blockEntityAtCasketPos;
        BlockPos soilPos1 = casketPos.above();
        BlockPos soilPos2 = soilPos1.relative(facing);
        if (!isSoil(world, soilPos1) && !isSoil(world, soilPos2)) {
            return;
        }
        if (!world.canSeeSky(soilPos1.above()) || !world.canSeeSky(soilPos2.above()) || !world.canSeeSky(pos.above())) {
            return;
        }
        if (!be.skeletonBuilder.canBuildWithIngredients(casketEntity)) {
            return;
        }
        OwnedSkeletonEntity skeleton = be.skeletonBuilder.build(casketEntity, casketEntity.getLevel(), be);
        skeleton.absMoveTo(soilPos1.getX(), soilPos1.getY() + 1, soilPos1.getZ());
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
