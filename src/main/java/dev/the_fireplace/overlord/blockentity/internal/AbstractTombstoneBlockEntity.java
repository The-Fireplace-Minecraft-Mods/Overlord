package dev.the_fireplace.overlord.blockentity.internal;

import dev.the_fireplace.overlord.blockentity.CasketBlockEntity;
import dev.the_fireplace.overlord.domain.mechanic.Tombstone;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.model.aiconfig.movement.PositionSetting;
import dev.the_fireplace.overlord.util.SkeletonBuilder;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Objects;

public abstract class AbstractTombstoneBlockEntity extends BlockEntity implements Tombstone, Tickable
{
    public AbstractTombstoneBlockEntity(BlockEntityType<?> type) {
        super(type);
    }

    @Override
    public void tick() {
        if (!hasWorld() || Objects.requireNonNull(getWorld()).isClient) {
            return;
        }
        assert world != null;
        if (!isNearMidnight()) {
            return;
        }
        Direction facing = this.world.getBlockState(getPos()).get(HorizontalFacingBlock.FACING);
        BlockPos casketPos = this.getPos().offset(facing).down(2);
        BlockEntity blockEntity = world.getBlockEntity(casketPos);
        if (!(blockEntity instanceof CasketBlockEntity)) {
            return;
        }
        CasketBlockEntity casketEntity = (CasketBlockEntity) blockEntity;
        BlockPos soilPos1 = casketPos.up();
        BlockPos soilPos2 = soilPos1.offset(facing);
        if (!isSoil(soilPos1) && !isSoil(soilPos2)) {
            return;
        }
        if (!world.isSkyVisible(soilPos1.up()) || !world.isSkyVisible(soilPos2.up()) || !world.isSkyVisible(pos.up())) {
            return;
        }
        if (!SkeletonBuilder.hasEssentialContents(casketEntity)) {
            return;
        }
        OwnedSkeletonEntity skeleton = SkeletonBuilder.build(casketEntity, casketEntity.getWorld(), this);
        skeleton.updatePosition(soilPos1.getX(), soilPos1.getY() + 1, soilPos1.getZ());
        skeleton.getAISettings().getMovement().setHome(new PositionSetting(soilPos1.getX(), soilPos1.getY() + 1, soilPos1.getZ()));
        world.spawnEntity(skeleton);
        //TODO dirt particles around skeleton
    }

    private boolean isSoil(BlockPos blockPos) {
        return world != null && world.getBlockState(blockPos).getMaterial().equals(Material.SOIL);
    }

    private boolean isNearMidnight() {
        return world != null && world.getTimeOfDay() >= 17500 && world.getTimeOfDay() <= 18500 && (world.getTimeOfDay() + 100) % 200 == 0;
    }
}
