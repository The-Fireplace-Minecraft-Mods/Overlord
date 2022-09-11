package dev.the_fireplace.overlord.loader;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

@Implementation
public final class FabricMenuLoaderHelper implements MenuLoaderHelper
{
    @Override
    public void openMenu(ResourceLocation resourceLocation, Player player, Consumer<FriendlyByteBuf> consumer) {
        ContainerProviderRegistry.INSTANCE.openContainer(resourceLocation, player, consumer);
    }
}
