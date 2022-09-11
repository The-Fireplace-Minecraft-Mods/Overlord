package dev.the_fireplace.overlord.loader;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Implementation
public final class FabricMenuLoaderHelper implements MenuLoaderHelper
{
    @Override
    public MenuProvider getSkeletonMenuProvider(OwnedSkeletonEntity entity) {
        return new ExtendedScreenHandlerFactory()
        {
            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                buf.writeUUID(entity.getUUID());
            }

            @Override
            public Component getDisplayName() {
                return entity.getDisplayName();
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                return entity.getContainer(inv, syncId);
            }
        };
    }

    @Override
    public void openMenu(Player player, MenuProvider menuProvider) {
        player.openMenu(menuProvider);
    }
}
