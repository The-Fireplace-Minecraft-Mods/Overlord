package dev.the_fireplace.overlord.world;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

public interface NbtMenuProvider extends MenuProvider
{
    void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf);
}
