package dev.the_fireplace.overlord.blockentity;

import dev.the_fireplace.overlord.blockentity.internal.AbstractTombstoneBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.UUID;

public class GraveMarkerBlockEntity extends AbstractTombstoneBlockEntity
{
    private UUID owner = null;

    public GraveMarkerBlockEntity(BlockPos pos, BlockState state) {
        super(OverlordBlockEntities.GRAVE_MARKER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
    }

    @Override
    public String getNameText() {
        return "";
    }

    @Override
    public void setNameText(String name) {

    }

    @Override
    @Nullable
    public UUID getOwner() {
        return owner;
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putUuid("owner", owner);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if (tag.contains("owner")) {
            this.owner = tag.getUuid("owner");
        }
    }
}
