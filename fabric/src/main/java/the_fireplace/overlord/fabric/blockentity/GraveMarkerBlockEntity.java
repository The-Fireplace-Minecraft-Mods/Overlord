package the_fireplace.overlord.fabric.blockentity;

import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import the_fireplace.overlord.api.Tombstone;
import the_fireplace.overlord.fabric.Overlord;
import the_fireplace.overlord.fabric.init.OverlordBlockEntities;
import the_fireplace.overlord.fabric.init.OverlordBlocks;

import java.util.Objects;

public class GraveMarkerBlockEntity extends BlockEntity implements Tombstone, Tickable {
    private Text name = new LiteralText("");

    public GraveMarkerBlockEntity(BlockEntityType<?> type) {
        super(type);
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
                        //TODO spawn skeleton
                    }//TODO else if has that one torch I'm going to make
                }
            }
        }
    }
}
