package dev.the_fireplace.overlord.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ArmySkullBlockEntity extends BlockEntity
{
    public ArmySkullBlockEntity(BlockPos pos, BlockState state) {
        super(OverlordBlockEntities.ARMY_SKULL_BLOCK_ENTITY, pos, state);
    }
}
