package the_fireplace.overlord.api.internal;

import net.minecraft.util.Identifier;
import the_fireplace.overlord.impl.registry.EntityRegistryImpl;

import java.util.Collection;

public interface EntityRegistry {
    static EntityRegistry getInstance() {
        //noinspection deprecation
        return EntityRegistryImpl.INSTANCE;
    }

    Collection<Identifier> getMonsterIds();
    Collection<Identifier> getAnimalIds();
}
