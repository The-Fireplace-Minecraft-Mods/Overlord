package dev.the_fireplace.overlord.impl.registry;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.registry.EquipmentRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.TreeSet;

@Implementation
@Singleton
public final class EquipmentRegistryImpl implements EquipmentRegistry
{
    @Inject
    public EquipmentRegistryImpl() {
        if (Registry.ITEM.stream().toArray().length == 0) {
            throw new IllegalStateException("Tried to access the item registry before it was initialized!");
        }
        for (Item item : Registry.ITEM) {
            if (item.canBeDepleted() && isEquipment(item)) {
                equipmentIds.add(Registry.ITEM.getKey(item));
            }
        }
    }

    private boolean isEquipment(Item item) {
        return item instanceof ArmorItem
            || !item.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND).isEmpty()
            || !item.getDefaultAttributeModifiers(EquipmentSlot.OFFHAND).isEmpty()
            || !item.getDefaultAttributeModifiers(EquipmentSlot.HEAD).isEmpty()
            || !item.getDefaultAttributeModifiers(EquipmentSlot.CHEST).isEmpty()
            || !item.getDefaultAttributeModifiers(EquipmentSlot.LEGS).isEmpty()
            || !item.getDefaultAttributeModifiers(EquipmentSlot.FEET).isEmpty();
    }

    private final TreeSet<ResourceLocation> equipmentIds = new TreeSet<>();

    @Override
    public boolean isEquipment(ResourceLocation identifier) {
        return equipmentIds.contains(identifier);
    }

    @Override
    public Collection<ResourceLocation> getEquipmentIds() {
        return equipmentIds;
    }
}
