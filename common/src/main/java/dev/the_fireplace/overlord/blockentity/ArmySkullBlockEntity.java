package dev.the_fireplace.overlord.blockentity;

import dev.the_fireplace.overlord.OverlordConstants;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ArmySkullBlockEntity extends BlockEntity
{
    public ArmySkullBlockEntity() {
        super(OverlordConstants.getInjector().getInstance(OverlordBlockEntities.class).getArmySkullBlockEntityType());
    }
}
