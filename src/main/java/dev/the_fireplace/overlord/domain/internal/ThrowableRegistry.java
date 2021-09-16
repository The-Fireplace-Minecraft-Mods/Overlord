package dev.the_fireplace.overlord.domain.internal;

import net.minecraft.util.Identifier;

import java.util.Collection;

public interface ThrowableRegistry {
    boolean isThrowable(Identifier identifier);

    Collection<Identifier> getThrowableIds();
}
