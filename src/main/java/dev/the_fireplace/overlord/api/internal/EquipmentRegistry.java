package dev.the_fireplace.overlord.api.internal;

import dev.the_fireplace.overlord.impl.registry.EquipmentRegistryImpl;
import net.minecraft.util.Identifier;

import java.util.Collection;

public interface EquipmentRegistry {
    static EquipmentRegistry getInstance() {
        //noinspection deprecation
        return EquipmentRegistryImpl.INSTANCE;
    }

    boolean isEquipment(Identifier identifier);

    Collection<Identifier> getEquipmentIds();
}
