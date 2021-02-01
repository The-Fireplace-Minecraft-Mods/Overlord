package the_fireplace.overlord.blockentity;

import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import the_fireplace.overlord.blockentity.internal.TombstoneBlockEntity;
import the_fireplace.overlord.entity.OwnedSkeletonEntity;
import the_fireplace.overlord.init.OverlordBlockEntities;
import the_fireplace.overlord.init.OverlordBlocks;
import the_fireplace.overlord.util.SkeletonBuilder;

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
                    BlockPos torchPos1 = this.getPos().offset(facing.rotateYClockwise());
                    BlockPos torchPos2 = this.getPos().offset(facing.rotateYCounterclockwise());
                    BlockPos torchPos3 = torchPos1.offset(facing, 2);
                    BlockPos torchPos4 = torchPos2.offset(facing, 2);
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
                            //TODO Dirt particles around skeleton
                        }
                    } else if(world.getBlockState(torchPos1).getBlock().equals(OverlordBlocks.TORCH_OF_THE_DEAD)
                        && world.getBlockState(torchPos2).getBlock().equals(OverlordBlocks.TORCH_OF_THE_DEAD)
                        && world.getBlockState(torchPos3).getBlock().equals(OverlordBlocks.TORCH_OF_THE_DEAD)
                        && world.getBlockState(torchPos4).getBlock().equals(OverlordBlocks.TORCH_OF_THE_DEAD)
                        && (world.getBlockState(soilPos1).getMaterial().equals(Material.SOIL)
                        || world.getBlockState(soilPos2).getMaterial().equals(Material.SOIL))) {
                        if(SkeletonBuilder.hasEssentialContents(casketEntity)) {
                            OwnedSkeletonEntity skeleton = SkeletonBuilder.build(casketEntity, casketEntity.getWorld(), this);
                            skeleton.updatePosition(soilPos1.getX(), soilPos1.getY() + 1, soilPos1.getZ());
                            world.spawnEntity(skeleton);
                            world.setBlockState(torchPos1, OverlordBlocks.SCORCHED_TORCH.getDefaultState());
                            world.setBlockState(torchPos2, OverlordBlocks.SCORCHED_TORCH.getDefaultState());
                            world.setBlockState(torchPos3, OverlordBlocks.SCORCHED_TORCH.getDefaultState());
                            world.setBlockState(torchPos4, OverlordBlocks.SCORCHED_TORCH.getDefaultState());
                            //TODO Flame and/or dirt particles around skeleton
                        }
                    }
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
