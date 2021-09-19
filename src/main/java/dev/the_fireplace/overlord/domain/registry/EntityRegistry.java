package dev.the_fireplace.overlord.domain.registry;

import net.minecraft.util.Identifier;

import java.util.Collection;

public interface EntityRegistry {
    Collection<Identifier> getMonsterIds();
    Collection<Identifier> getAnimalIds();
}
