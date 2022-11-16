package dev.the_fireplace.overlord.blockentity;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.blockentity.internal.AbstractTombstoneBlockEntity;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import java.util.UUID;

public class GraveMarkerBlockEntity extends AbstractTombstoneBlockEntity
{
    private UUID owner = null;

    public GraveMarkerBlockEntity() {
        super(OverlordConstants.getInjector().getInstance(OverlordBlockEntities.class).getGraveMarkerBlockEntityType());
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
    public CompoundTag save(CompoundTag tag) {
        tag = super.save(tag);
        if (owner != null) {
            tag.putUUID("owner", owner);
        }
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("owner")) {
            this.owner = tag.getUUID("owner");
        }
    }
}