package dev.the_fireplace.overlord.block;

import dev.the_fireplace.overlord.init.OverlordParticleTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

public class WallScorchedTorchBlock extends WallTorchBlock {
    public WallScorchedTorchBlock(Settings settings) {
        super(settings);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if(random.nextInt(2) != 1)
            return;
        Direction direction = state.get(FACING);
        double d = (double)pos.getX() + 0.5D;
        double e = (double)pos.getY() + 0.7D;
        double f = (double)pos.getZ() + 0.5D;
        Direction direction2 = direction.getOpposite();
        world.addParticle(ParticleTypes.SMOKE, d + 0.27D * (double)direction2.getOffsetX(), e + 0.22D, f + 0.27D * (double)direction2.getOffsetZ(), 0.0D, 0.0D, 0.0D);
        world.addParticle(OverlordParticleTypes.SCORCHED_FLAME, d + 0.27D * (double)direction2.getOffsetX(), e + 0.22D, f + 0.27D * (double)direction2.getOffsetZ(), 0.0D, 0.0D, 0.0D);
    }


}
