package dev.the_fireplace.overlord.blockentity.internal;

import dev.the_fireplace.overlord.api.mechanic.Tombstone;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public abstract class TombstoneBlockEntity extends BlockEntity implements Tombstone {
    public TombstoneBlockEntity(BlockEntityType<?> type) {
        super(type);
    }
}
