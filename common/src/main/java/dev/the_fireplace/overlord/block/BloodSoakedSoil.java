package dev.the_fireplace.overlord.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class BloodSoakedSoil extends SnowyDirtBlock implements BonemealableBlock
{
    public BloodSoakedSoil(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter world, BlockPos pos, BlockState state, boolean isClient) {
        return !state.getValue(SNOWY)
            && world.getBlockState(pos.above()).isAir()
            && world.getBlockState(pos.above(2)).isAir()
            && (!(world instanceof Level) || !((Level) world).isDay());
    }

    @Override
    public boolean isBonemealSuccess(Level world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel world, Random random, BlockPos pos, BlockState state) {
        world.setBlockAndUpdate(pos, Blocks.COARSE_DIRT.defaultBlockState());
        Zombie zombie = random.nextInt(10000) == 1 ? new ZombieVillager(EntityType.ZOMBIE_VILLAGER, world) : new Zombie(EntityType.ZOMBIE, world);
        if (random.nextInt(3) == 1) {
            zombie.setBaby(true);
        }
        zombie.absMoveTo(pos.getX(), pos.getY() + 1, pos.getZ());
        world.addFreshEntity(zombie);
    }
}
