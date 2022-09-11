package dev.the_fireplace.overlord.impl.world;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.world.UndeadDaylightDamager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

@Implementation
public final class DaylightDamager implements UndeadDaylightDamager
{
    private final Random random = new Random();

    @Override
    public void applyDamage(LivingEntity entity) {
        if (entity.getCommandSenderWorld().isClientSide()) {
            return;
        }
        ItemStack helmetStack = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (!helmetStack.isEmpty()) {
            if (!helmetStack.isDamageableItem()) {
                return;
            }
            helmetStack.setDamageValue(helmetStack.getDamageValue() + random.nextInt(2));
            if (helmetStack.getDamageValue() >= helmetStack.getMaxDamage()) {
                entity.broadcastBreakEvent(EquipmentSlot.HEAD);
                entity.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
            }
        } else {
            entity.setSecondsOnFire(8);
        }
    }
}
