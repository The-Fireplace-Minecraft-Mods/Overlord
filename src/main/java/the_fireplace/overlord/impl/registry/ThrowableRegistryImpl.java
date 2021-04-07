package the_fireplace.overlord.impl.registry;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import the_fireplace.overlord.api.internal.ThrowableRegistry;

import java.util.Collection;
import java.util.TreeSet;

public final class ThrowableRegistryImpl implements ThrowableRegistry {
    @Deprecated
    public static final ThrowableRegistry INSTANCE = new ThrowableRegistryImpl().init();
    private ThrowableRegistryImpl(){}

    private final TreeSet<Identifier> throwableIds = new TreeSet<>();

    @Override
    public boolean isThrowable(Identifier identifier) {
        return throwableIds.contains(identifier);
    }

    @Override
    public Collection<Identifier> getThrowableIds() {
        return throwableIds;
    }

    private ThrowableRegistryImpl init() {
        if (Registry.ITEM.isEmpty()) {
            throw new IllegalStateException("Tried to access the item registry before it was initialized!");
        }
        //TODO Find a way to get throwables

        return this;
    }
}
