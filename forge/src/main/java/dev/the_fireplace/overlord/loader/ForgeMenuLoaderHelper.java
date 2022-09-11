package dev.the_fireplace.overlord.loader;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.world.NbtMenuProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

@Implementation
public final class ForgeMenuLoaderHelper implements MenuLoaderHelper
{
    @Override
    public MenuProvider getSkeletonMenuProvider(OwnedSkeletonEntity entity) {
        return new NbtMenuProvider()
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
        if (player instanceof ServerPlayer serverPlayer) {
            if (menuProvider instanceof NbtMenuProvider nbtMenuProvider) {
                NetworkHooks.openGui(serverPlayer, menuProvider, friendlyByteBuf -> nbtMenuProvider.writeScreenOpeningData(serverPlayer, friendlyByteBuf));
            } else {
                player.openMenu(menuProvider);
            }
        }
    }
}
