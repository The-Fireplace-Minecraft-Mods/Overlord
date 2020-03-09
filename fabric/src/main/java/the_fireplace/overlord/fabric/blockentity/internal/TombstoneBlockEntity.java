package the_fireplace.overlord.fabric.blockentity.internal;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import the_fireplace.overlord.api.Tombstone;

public abstract class TombstoneBlockEntity extends BlockEntity implements Tombstone {
    public TombstoneBlockEntity(BlockEntityType<?> type) {
        super(type);
    }
}
