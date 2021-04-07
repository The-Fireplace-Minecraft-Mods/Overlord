package dev.the_fireplace.overlord.impl.registry;

import dev.the_fireplace.overlord.api.internal.EquipmentRegistry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.TreeSet;

public final class EquipmentRegistryImpl implements EquipmentRegistry {
    @Deprecated
    public static final EquipmentRegistry INSTANCE = new EquipmentRegistryImpl().init();
    private EquipmentRegistryImpl(){}

    private final TreeSet<Identifier> equipmentIds = new TreeSet<>();

    @Override
    public boolean isEquipment(Identifier identifier) {
        return equipmentIds.contains(identifier);
    }

    @Override
    public Collection<Identifier> getEquipmentIds() {
        return equipmentIds;
    }

    private EquipmentRegistryImpl init() {
        if (Registry.ITEM.isEmpty()) {
            throw new IllegalStateException("Tried to access the item registry before it was initialized!");
        }
        for (Item item: Registry.ITEM) {
            if (item.isDamageable()
                && (item instanceof ArmorItem
                    || !item.getModifiers(EquipmentSlot.MAINHAND).isEmpty()
                    || !item.getModifiers(EquipmentSlot.OFFHAND).isEmpty()
                    || !item.getModifiers(EquipmentSlot.HEAD).isEmpty()
                    || !item.getModifiers(EquipmentSlot.CHEST).isEmpty()
                    || !item.getModifiers(EquipmentSlot.LEGS).isEmpty()
                    || !item.getModifiers(EquipmentSlot.FEET).isEmpty()
                )
            ) {
                equipmentIds.add(Registry.ITEM.getId(item));
            }
        }

        return this;
    }
}
