package the_fireplace.overlord.fabric.blockentity;

import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import the_fireplace.overlord.fabric.blockentity.internal.TombstoneBlockEntity;
import the_fireplace.overlord.fabric.entity.OwnedSkeletonEntity;
import the_fireplace.overlord.fabric.init.OverlordBlockEntities;
import the_fireplace.overlord.fabric.init.OverlordBlocks;
import the_fireplace.overlord.fabric.util.SkeletonBuilder;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class GraveMarkerBlockEntity extends TombstoneBlockEntity implements Tickable {
    private String name = "";

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
    }

    private UUID owner = null;

    public GraveMarkerBlockEntity() {
        super(OverlordBlockEntities.GRAVE_MARKER_BLOCK_ENTITY);
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
        if(hasWorld() && !Objects.requireNonNull(getWorld()).isClient) {
            assert world != null;
            if(world.getTimeOfDay() >= 17500 && world.getTimeOfDay() <= 18500 && (world.getTimeOfDay()+100) % 200 == 0) {
                Direction facing = this.world.getBlockState(getPos()).get(HorizontalFacingBlock.FACING);
                BlockPos casketPos = this.getPos().offset(facing).down(2);
                BlockEntity blockEntity = world.getBlockEntity(casketPos);
                if(blockEntity instanceof CasketBlockEntity) {
                    CasketBlockEntity casketEntity = (CasketBlockEntity) blockEntity;
                    BlockPos soilPos1 = casketPos.up();
                    BlockPos soilPos2 = soilPos1.offset(facing);
                    if(world.getBlockState(soilPos1).getBlock().equals(OverlordBlocks.BLOOD_SOAKED_SOIL)
                        && world.getBlockState(soilPos2).getBlock().equals(OverlordBlocks.BLOOD_SOAKED_SOIL)
                        && world.getBlockState(soilPos1.up()).isAir()
                        && world.getBlockState(soilPos1.up(2)).isAir()) {
                        if(SkeletonBuilder.hasEssentialContents(casketEntity)) {
                            OwnedSkeletonEntity skeleton = SkeletonBuilder.build(casketEntity, casketEntity.getWorld(), this);
                            skeleton.updatePosition(soilPos1.getX(), soilPos1.getY()+1, soilPos1.getZ());
                            world.spawnEntity(skeleton);
                            world.setBlockState(soilPos1, Blocks.COARSE_DIRT.getDefaultState());
                            world.setBlockState(soilPos2, Blocks.COARSE_DIRT.getDefaultState());
                        }
                    }//TODO else if has that one torch I'm going to make
                }
            }
        }
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
