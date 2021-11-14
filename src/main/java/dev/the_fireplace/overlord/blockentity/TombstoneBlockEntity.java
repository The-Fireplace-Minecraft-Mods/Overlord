package dev.the_fireplace.overlord.blockentity;

import dev.the_fireplace.overlord.blockentity.internal.AbstractTombstoneBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;

import javax.annotation.Nullable;
import java.util.UUID;

public class TombstoneBlockEntity extends AbstractTombstoneBlockEntity
{
    private String name = "";
    private UUID owner = null;

    public TombstoneBlockEntity() {
        super(OverlordBlockEntities.TOMBSTONE_BLOCK_ENTITY);
    }

    @Override
    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
        this.markDirty();
    }

    @Override
    public String getNameText() {
        return name;
    }

    @Override
    public void setNameText(String name) {
        this.name = name;
        this.markDirty();
    }

    @Override
    @Nullable
    public UUID getOwner() {
        return owner;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag = super.writeNbt(tag);
        if (owner != null) {
            tag.putUuid("owner", owner);
        }
        tag.putString("text", name);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, NbtCompound tag) {
        super.fromTag(state, tag);
        if (tag.containsUuid("owner")) {
            this.owner = tag.getUuid("owner");
        }
        this.name = tag.getString("text");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return writeNbt(new NbtCompound());
    }
}
