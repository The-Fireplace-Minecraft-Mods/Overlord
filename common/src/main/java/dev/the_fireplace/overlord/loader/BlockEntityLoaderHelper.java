package dev.the_fireplace.overlord.loader;

import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface BlockEntityLoaderHelper
{
    MenuType<ChestMenu> registerCasketMenu();

    <T extends BlockEntity> BlockEntityType<T> createType(Factory<T> factory, Block... blocks);

    @FunctionalInterface
    interface Factory<T extends BlockEntity>
    {
        T create();
    }
}
