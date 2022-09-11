package dev.the_fireplace.overlord.blockentity;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.blockentity.internal.AbstractTombstoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class TombstoneBlockEntity extends AbstractTombstoneBlockEntity
{
    private String name = "";
    private UUID owner = null;

    public TombstoneBlockEntity(BlockPos pos, BlockState state) {
        super(OverlordConstants.getInjector().getInstance(OverlordBlockEntities.class).getTombstoneBlockEntityType(), pos, state);
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
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (owner != null) {
            tag.putUUID("owner", owner);
        }
        tag.putString("text", name);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID("owner")) {
            this.owner = tag.getUUID("owner");
        }
        this.name = tag.getString("text");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }
}
