package dev.the_fireplace.overlord.loader;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public interface MenuLoaderHelper
{
    void openMenu(ResourceLocation resourceLocation, Player player, Consumer<FriendlyByteBuf> consumer);
}
