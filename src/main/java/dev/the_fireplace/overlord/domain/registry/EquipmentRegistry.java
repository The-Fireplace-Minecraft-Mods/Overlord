package dev.the_fireplace.overlord.domain.registry;

import net.minecraft.util.Identifier;

import java.util.Collection;

public interface EquipmentRegistry {
    boolean isEquipment(Identifier identifier);

    Collection<Identifier> getEquipmentIds();
}