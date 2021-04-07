package the_fireplace.overlord.api.internal;

import net.minecraft.util.Identifier;
import the_fireplace.overlord.impl.registry.ThrowableRegistryImpl;

import java.util.Collection;

public interface ThrowableRegistry {
    static ThrowableRegistry getInstance() {
        //noinspection deprecation
        return ThrowableRegistryImpl.INSTANCE;
    }

    boolean isThrowable(Identifier identifier);

    Collection<Identifier> getThrowableIds();
}
