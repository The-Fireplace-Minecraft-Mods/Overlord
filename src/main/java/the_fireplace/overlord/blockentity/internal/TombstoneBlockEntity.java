package the_fireplace.overlord.blockentity.internal;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import the_fireplace.overlord.api.mechanic.Tombstone;

public abstract class TombstoneBlockEntity extends BlockEntity implements Tombstone {
    public TombstoneBlockEntity(BlockEntityType<?> type) {
        super(type);
    }
}
