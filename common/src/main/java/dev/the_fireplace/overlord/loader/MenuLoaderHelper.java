package dev.the_fireplace.overlord.loader;

import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;

public interface MenuLoaderHelper
{
    MenuProvider getSkeletonMenuProvider(OwnedSkeletonEntity entity);

    void openMenu(Player player, MenuProvider menuProvider);
}
