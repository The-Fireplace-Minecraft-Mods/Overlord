package dev.the_fireplace.overlord.api.internal;

import dev.the_fireplace.overlord.impl.registry.ThrowableRegistryImpl;
import net.minecraft.util.Identifier;

import java.util.Collection;

public interface ThrowableRegistry {
    static ThrowableRegistry getInstance() {
        //noinspection deprecation
        return ThrowableRegistryImpl.INSTANCE;
    }

    boolean isThrowable(Identifier identifier);

    Collection<Identifier> getThrowableIds();
}
