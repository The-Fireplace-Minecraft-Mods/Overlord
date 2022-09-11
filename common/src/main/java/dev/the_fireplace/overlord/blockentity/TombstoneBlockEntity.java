package dev.the_fireplace.overlord.blockentity;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.blockentity.internal.AbstractTombstoneBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class TombstoneBlockEntity extends AbstractTombstoneBlockEntity
{
    private String name = "";
    private UUID owner = null;

    public TombstoneBlockEntity() {
        super(OverlordConstants.getInjector().getInstance(OverlordBlockEntities.class).getTombstoneBlockEntityType());
    }

    @Override
    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
        this.setChanged();
    }

    @Override
    public String getNameText() {
        return name;
    }

    @Override
    public void setNameText(String name) {
        this.name = name;
        this.setChanged();
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
        tag.putString("text", name);
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundTag tag) {
        super.load(state, tag);
        if (tag.hasUUID("owner")) {
            this.owner = tag.getUUID("owner");
        }
        this.name = tag.getString("text");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag = save(tag);
        return tag;
    }
}
