package dev.the_fireplace.overlord.block;

import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class BloodSoakedSoil extends SnowyBlock implements Fertilizable
{
    public BloodSoakedSoil(Block.Settings settings) {
        super(settings);
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return !state.get(SNOWY)
            && world.getBlockState(pos.up()).isAir()
            && world.getBlockState(pos.up(2)).isAir()
            && (!(world instanceof World) || !((World) world).isDay());
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        world.setBlockState(pos, Blocks.COARSE_DIRT.getDefaultState());
        ZombieEntity zombie = random.nextInt(10000) == 1 ? new ZombieVillagerEntity(EntityType.ZOMBIE_VILLAGER, world) : new ZombieEntity(EntityType.ZOMBIE, world);
        if (random.nextInt(3) == 1) {
            zombie.setBaby(true);
        }
        zombie.updatePosition(pos.getX(), pos.getY() + 1, pos.getZ());
        world.spawnEntity(zombie);
    }
}
