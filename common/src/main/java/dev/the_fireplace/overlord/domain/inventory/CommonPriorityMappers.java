package dev.the_fireplace.overlord.domain.inventory;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.ToIntFunction;

public interface CommonPriorityMappers {
    ToIntFunction<ItemStack> armor();

    ToIntFunction<ItemStack> slotArmor(EquipmentSlot slot);

    ToIntFunction<ItemStack> weapon(LivingEntity source, @Nullable LivingEntity target);

    ToIntFunction<ItemStack> ammo(ItemStack forWeapon);
}
