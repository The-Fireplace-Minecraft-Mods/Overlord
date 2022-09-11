package dev.the_fireplace.overlord.blockentity;

import dev.the_fireplace.overlord.OverlordConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ArmySkullBlockEntity extends BlockEntity
{
    public ArmySkullBlockEntity(BlockPos pos, BlockState state) {
        super(OverlordConstants.getInjector().getInstance(OverlordBlockEntities.class).getArmySkullBlockEntityType(), pos, state);
    }
}
