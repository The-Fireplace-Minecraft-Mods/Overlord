package dev.the_fireplace.overlord.impl.advancement;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public final class ProgressFinderProxies
{
    private static final Map<Class<? extends Player>, ProgressFinderProxy<? extends Player>> proxies = new HashMap<>(2);

    public static <T extends Player> ProgressFinderProxy<T> getFinder(T player) {
        for (Class<? extends Player> playerClass : proxies.keySet()) {
            if (playerClass.isInstance(player)) {
                //noinspection unchecked
                return (ProgressFinderProxy<T>) proxies.get(playerClass);
            }
        }

        throw new IllegalStateException("No known way to find achievement progress for player of class: " + player.getClass().getName());
    }

    public static <T extends Player> void addFinder(Class<T> playerClass, ProgressFinderProxy<T> progressFinder) {
        proxies.put(playerClass, progressFinder);
    }

    static {
        addFinder(ServerPlayer.class, new ServerProgressFinder());
    }
}
