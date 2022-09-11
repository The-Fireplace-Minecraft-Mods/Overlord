package dev.the_fireplace.overlord.loader;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.blockentity.OverlordBlockEntities;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

@Implementation
public final class FabricBlockEntityLoaderHelper implements BlockEntityLoaderHelper
{
    @Override
    public void registerCasketMenu() {
        ContainerProviderRegistry.INSTANCE.registerFactory(OverlordBlockEntities.CASKET_BLOCK_ENTITY_ID, (syncId, identifier, player, buf) -> {
            final Level world = player.level;
            final BlockPos pos = buf.readBlockPos();
            return world.getBlockState(pos).getMenuProvider(player.level, pos).createMenu(syncId, player.inventory, player);
        });
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createType(Factory<T> factory, Block... blocks) {
        return BlockEntityType.Builder.of(factory::create, blocks).build(null);
    }
}
