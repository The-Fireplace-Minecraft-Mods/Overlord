package dev.the_fireplace.overlord.block;

import dev.the_fireplace.overlord.blockentity.ArmySkullBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.Wearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public abstract class AbstractArmySkullBlock extends BlockWithEntity implements Wearable
{

    protected final SkullType skullType;

    public AbstractArmySkullBlock(SkullType skullType, Settings settings) {
        super(settings);
        this.skullType = skullType;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ArmySkullBlockEntity(pos, state);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
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
