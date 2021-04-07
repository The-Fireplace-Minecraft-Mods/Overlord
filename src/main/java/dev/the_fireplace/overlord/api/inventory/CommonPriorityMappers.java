package dev.the_fireplace.overlord.api.inventory;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.function.ToIntFunction;

public interface CommonPriorityMappers {
    ToIntFunction<ItemStack> armor();
    ToIntFunction<ItemStack> slotArmor(EquipmentSlot slot);
    ToIntFunction<ItemStack> weapon();
    ToIntFunction<ItemStack> ammo(ItemStack forWeapon);
    ToIntFunction<ItemStack> throwable();
}
