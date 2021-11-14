package dev.the_fireplace.overlord.blockentity;

import dev.the_fireplace.overlord.blockentity.internal.AbstractTombstoneBlockEntity;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import java.util.UUID;

public class GraveMarkerBlockEntity extends AbstractTombstoneBlockEntity
{
    private UUID owner = null;

    public GraveMarkerBlockEntity() {
        super(OverlordBlockEntities.GRAVE_MARKER_BLOCK_ENTITY);
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
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        tag.putUuid("owner", owner);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.owner = tag.getUuid("owner");
    }
}
