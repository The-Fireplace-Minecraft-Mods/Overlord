package dev.the_fireplace.overlord.loader;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.blockentity.OverlordBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegisterEvent;

@Implementation
public final class ForgeBlockEntityLoaderHelper implements BlockEntityLoaderHelper
{
    private MenuType<ChestMenu> chestMenuType;

    @Override
    public MenuType<ChestMenu> registerCasketMenu() {
        chestMenuType = new MenuType<>((IContainerFactory<ChestMenu>) (windowId, inv, data) -> {
            Player player = inv.player;
            final Level world = player.level;
            final BlockPos pos = data.readBlockPos();
            return (ChestMenu) world.getBlockState(pos).getMenuProvider(player.level, pos).createMenu(windowId, player.getInventory(), player);
        });

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerMenus);

        return chestMenuType;
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createType(Factory<T> factory, Block... blocks) {
        return BlockEntityType.Builder.of(factory::create, blocks).build(null);
    }

    public void registerMenus(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registry.MENU_REGISTRY)) {
            return;
        }
        event.register(Registry.MENU_REGISTRY, OverlordBlockEntities.CASKET_BLOCK_ENTITY_ID, () -> chestMenuType);
    }
}
