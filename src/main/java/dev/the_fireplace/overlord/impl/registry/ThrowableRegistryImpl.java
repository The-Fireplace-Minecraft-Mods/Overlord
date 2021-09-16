package dev.the_fireplace.overlord.impl.registry;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.internal.ThrowableRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.TreeSet;

@Implementation
@Singleton
public final class ThrowableRegistryImpl implements ThrowableRegistry {
    @Inject
    public ThrowableRegistryImpl() {
        if (Registry.ITEM.isEmpty()) {
            throw new IllegalStateException("Tried to access the item registry before it was initialized!");
        }
        //TODO Find a way to get throwables
    }

    private final TreeSet<Identifier> throwableIds = new TreeSet<>();

    @Override
    public boolean isThrowable(Identifier identifier) {
        return throwableIds.contains(identifier);
    }

    @Override
    public Collection<Identifier> getThrowableIds() {
        return throwableIds;
    }
}
