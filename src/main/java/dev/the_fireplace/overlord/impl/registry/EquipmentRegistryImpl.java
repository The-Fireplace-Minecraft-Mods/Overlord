package dev.the_fireplace.overlord.impl.registry;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.registry.EquipmentRegistry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.TreeSet;

@Implementation
@Singleton
public final class EquipmentRegistryImpl implements EquipmentRegistry {
    @Inject
    public EquipmentRegistryImpl() {
        if (Registry.ITEM.isEmpty()) {
            throw new IllegalStateException("Tried to access the item registry before it was initialized!");
        }
        for (Item item : Registry.ITEM) {
            if (item.isDamageable() && isEquipment(item)) {
                equipmentIds.add(Registry.ITEM.getId(item));
            }
        }
    }

    private boolean isEquipment(Item item) {
        return item instanceof ArmorItem
            || !item.getModifiers(EquipmentSlot.MAINHAND).isEmpty()
            || !item.getModifiers(EquipmentSlot.OFFHAND).isEmpty()
            || !item.getModifiers(EquipmentSlot.HEAD).isEmpty()
            || !item.getModifiers(EquipmentSlot.CHEST).isEmpty()
            || !item.getModifiers(EquipmentSlot.LEGS).isEmpty()
            || !item.getModifiers(EquipmentSlot.FEET).isEmpty();
    }

    private final TreeSet<Identifier> equipmentIds = new TreeSet<>();

    @Override
    public boolean isEquipment(Identifier identifier) {
        return equipmentIds.contains(identifier);
    }

    @Override
    public Collection<Identifier> getEquipmentIds() {
        return equipmentIds;
    }
}
