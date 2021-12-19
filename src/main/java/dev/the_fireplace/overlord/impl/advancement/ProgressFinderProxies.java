package dev.the_fireplace.overlord.impl.advancement;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public final class ProgressFinderProxies
{
    private static final Map<Class<? extends PlayerEntity>, ProgressFinderProxy<? extends PlayerEntity>> proxies = new HashMap<>(2);

    public static <T extends PlayerEntity> ProgressFinderProxy<T> getFinder(T player) {
        for (Class<? extends PlayerEntity> playerClass : proxies.keySet()) {
            if (playerClass.isInstance(player)) {
                //noinspection unchecked
                return (ProgressFinderProxy<T>) proxies.get(playerClass);
            }
        }

        throw new IllegalStateException("No known way to find achievement progress for player of class: " + player.getClass().getName());
    }

    public static <T extends PlayerEntity> void addFinder(Class<T> playerClass, ProgressFinderProxy<T> progressFinder) {
        proxies.put(playerClass, progressFinder);
    }

    static {
        addFinder(ServerPlayerEntity.class, new ServerProgressFinder());
    }
}
