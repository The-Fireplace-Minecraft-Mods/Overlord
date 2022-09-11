package dev.the_fireplace.overlord.block;

import dev.the_fireplace.overlord.blockentity.ArmySkullBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

public abstract class AbstractArmySkullBlock extends BaseEntityBlock implements Wearable
{

    protected final SkullType skullType;

    public AbstractArmySkullBlock(SkullType skullType, Properties settings) {
        super(settings);
        this.skullType = skullType;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArmySkullBlockEntity(pos, state);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }

    public SkullType getSkullType() {
        return skullType;
    }

    public enum SkullType
    {
        MUSCLE_SKELETON,
        SKIN_SKELETON,
        MUSCLE_SKIN_SKELETON;
    }
}
