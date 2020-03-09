package the_fireplace.overlord.fabric.blockentity;

import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import the_fireplace.overlord.fabric.blockentity.internal.TombstoneBlockEntity;
import the_fireplace.overlord.fabric.init.OverlordBlockEntities;
import the_fireplace.overlord.fabric.init.OverlordBlocks;
import the_fireplace.overlord.fabric.util.SkeletonBuilder;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class GraveMarkerBlockEntity extends TombstoneBlockEntity implements Tickable {
    private Text name = new LiteralText("");

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
        this.name = new LiteralText(name);
    }

    @Override
    @Nullable
    public UUID getOwner() {
        return owner;
    }

    @Override
    public void tick() {
        if(hasWorld() && !Objects.requireNonNull(getWorld()).isClient) {
            if(getWorld().getTimeOfDay() >= 17500 && getWorld().getTimeOfDay() <= 18500 && getWorld().getTimeOfDay() % 200 == 0) {
                Direction facing = this.getWorld().getBlockState(getPos()).get(HorizontalFacingBlock.FACING);
                BlockPos casketPos = this.getPos().offset(facing).down(2);
                BlockEntity blockEntity = getWorld().getBlockEntity(casketPos);
                if(blockEntity instanceof CasketBlockEntity) {
                    CasketBlockEntity casketEntity = (CasketBlockEntity) blockEntity;
                    BlockPos soilPos1 = casketPos.up(1);
                    BlockPos soilPos2 = soilPos1.offset(facing);
                    if(getWorld().getBlockState(soilPos1).getBlock().equals(OverlordBlocks.BLOOD_SOAKED_SOIL) && getWorld().getBlockState(soilPos2).getBlock().equals(OverlordBlocks.BLOOD_SOAKED_SOIL)) {
                        if(SkeletonBuilder.hasEssentialContents(casketEntity)) {
                            SkeletonBuilder.build(casketEntity, casketEntity.getWorld(), this);
                            getWorld().setBlockState(soilPos1, Blocks.COARSE_DIRT.getDefaultState());
                            getWorld().setBlockState(soilPos2, Blocks.COARSE_DIRT.getDefaultState());
                        }
                    }//TODO else if has that one torch I'm going to make
                }
            }
        }
    }
}
