package dev.the_fireplace.overlord.blockentity.internal;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.blockentity.CasketBlockEntity;
import dev.the_fireplace.overlord.domain.blockentity.Tombstone;
import dev.the_fireplace.overlord.domain.entity.creation.SkeletonBuilder;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.entity.ai.aiconfig.movement.PositionSetting;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class AbstractTombstoneBlockEntity extends BlockEntity implements Tombstone
{
    protected final SkeletonBuilder skeletonBuilder;

    public AbstractTombstoneBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.skeletonBuilder = DIContainer.get().getInstance(SkeletonBuilder.class);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AbstractTombstoneBlockEntity be) {
        if (world.isClient()) {
            return;
        }
        if (!isNearMidnight(world)) {
            return;
        }
        Direction facing = state.get(HorizontalFacingBlock.FACING);
        BlockPos casketPos = pos.offset(facing).down(2);
        BlockEntity blockEntityAtCasketPos = world.getBlockEntity(casketPos);
        if (!(blockEntityAtCasketPos instanceof CasketBlockEntity)) {
            return;
        }
        CasketBlockEntity casketEntity = (CasketBlockEntity) blockEntityAtCasketPos;
        BlockPos soilPos1 = casketPos.up();
        BlockPos soilPos2 = soilPos1.offset(facing);
        if (!isSoil(world, soilPos1) && !isSoil(world, soilPos2)) {
            return;
        }
        if (!world.isSkyVisible(soilPos1.up()) || !world.isSkyVisible(soilPos2.up()) || !world.isSkyVisible(pos.up())) {
            return;
        }
        if (!be.skeletonBuilder.canBuildWithIngredients(casketEntity)) {
            return;
        }
        OwnedSkeletonEntity skeleton = be.skeletonBuilder.build(casketEntity, casketEntity.getWorld(), be);
        skeleton.updatePosition(soilPos1.getX(), soilPos1.getY() + 1, soilPos1.getZ());
        skeleton.getAISettings().getMovement().setHome(new PositionSetting(soilPos1.getX(), soilPos1.getY() + 1, soilPos1.getZ()));
        world.spawnEntity(skeleton);
        //TODO dirt particles around skeleton
    }

    private static boolean isSoil(World world, BlockPos blockPos) {
        return world.getBlockState(blockPos).getMaterial().equals(Material.SOIL);
    }

    private static boolean isNearMidnight(World world) {
        if (world == null) {
            return false;
        }
        int dayLength = 24000;
        long timeOfDay = world.getTimeOfDay() % dayLength;
        return timeOfDay >= 17500 && timeOfDay <= 18500 && (timeOfDay + 100) % 200 == 0;
    }
}
