package dev.the_fireplace.overlord.blockentity;

import dev.the_fireplace.overlord.blockentity.internal.TombstoneBlockEntity;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.init.OverlordBlockEntities;
import dev.the_fireplace.overlord.model.aiconfig.movement.PositionSetting;
import dev.the_fireplace.overlord.util.SkeletonBuilder;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class GraveMarkerBlockEntity extends TombstoneBlockEntity implements Tickable
{
    private String name = "";

    private UUID owner = null;

    public GraveMarkerBlockEntity() {
        super(OverlordBlockEntities.GRAVE_MARKER_BLOCK_ENTITY);
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
    }

    @Override
    public String getNameText() {
        return name.toString();
    }

    @Override
    public void setNameText(String name) {
        this.name = name;
    }

    @Override
    @Nullable
    public UUID getOwner() {
        return owner;
    }

    @Override
    public void tick() {
        if (!hasWorld() || Objects.requireNonNull(getWorld()).isClient) {
            return;
        }
        assert world != null;
        if (!isNearMidnight()) {
            return;
        }
        Direction facing = this.world.getBlockState(getPos()).get(HorizontalFacingBlock.FACING);
        BlockPos casketPos = this.getPos().offset(facing).down(2);
        BlockEntity blockEntity = world.getBlockEntity(casketPos);
        if (!(blockEntity instanceof CasketBlockEntity)) {
            return;
        }
        CasketBlockEntity casketEntity = (CasketBlockEntity) blockEntity;
        BlockPos soilPos1 = casketPos.up();
        BlockPos soilPos2 = soilPos1.offset(facing);
        if (!world.getBlockState(soilPos1).getMaterial().equals(Material.SOIL) && !world.getBlockState(soilPos2).getMaterial().equals(Material.SOIL)) {
            return;
        }
        if (!SkeletonBuilder.hasEssentialContents(casketEntity)) {
            return;
        }
        OwnedSkeletonEntity skeleton = SkeletonBuilder.build(casketEntity, casketEntity.getWorld(), this);
        skeleton.updatePosition(soilPos1.getX(), soilPos1.getY() + 1, soilPos1.getZ());
        skeleton.getAISettings().getMovement().setHome(new PositionSetting(soilPos1.getX(), soilPos1.getY() + 1, soilPos1.getZ()));
        world.spawnEntity(skeleton);
        //TODO dirt particles around skeleton
    }

    private boolean isNearMidnight() {
        return world != null && world.getTimeOfDay() >= 17500 && world.getTimeOfDay() <= 18500 && (world.getTimeOfDay() + 100) % 200 == 0;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        tag.putUuid("owner", owner);
        tag.putString("text", name);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.owner = tag.getUuid("owner");
        this.name = tag.getString("text");
    }
}
