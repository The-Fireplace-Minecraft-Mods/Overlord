package dev.the_fireplace.overlord.loader;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.blockentity.OverlordBlockEntities;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

@Implementation
public final class FabricBlockEntityLoaderHelper implements BlockEntityLoaderHelper
{
    @Override
    public MenuType<ChestMenu> registerCasketMenu() {
        return ScreenHandlerRegistry.registerExtended(OverlordBlockEntities.CASKET_BLOCK_ENTITY_ID, (syncId, playerInventory, buf) -> {
            Player player = playerInventory.player;
            final Level world = player.level;
            final BlockPos pos = buf.readBlockPos();
            return (ChestMenu) world.getBlockState(pos).getMenuProvider(player.level, pos).createMenu(syncId, player.getInventory(), player);
        });
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createType(Factory<T> factory, Block... blocks) {
        return FabricBlockEntityTypeBuilder.create(factory::create, blocks).build(null);
    }
}
