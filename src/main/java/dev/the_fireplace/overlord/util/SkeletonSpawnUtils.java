package dev.the_fireplace.overlord.util;

import dev.the_fireplace.overlord.item.OverlordItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class SkeletonSpawnUtils
{
    public void addMask(LivingEntity entity) {
        int daysSinceUndertaleReleased = (int) ChronoUnit.DAYS.between(LocalDate.parse("2015-09-15", DateTimeFormatter.ofPattern("y-M-d")), LocalDateTime.now());
        int daysSince1710Released = (int) ChronoUnit.DAYS.between(LocalDate.parse("2014-06-26", DateTimeFormatter.ofPattern("y-M-d")), LocalDateTime.now());
        if (entity.getEntityWorld().getRandom().nextInt(daysSinceUndertaleReleased + daysSince1710Released) == 0
            && entity.getEquippedStack(EquipmentSlot.HEAD).isEmpty()
        ) {
            entity.equipStack(EquipmentSlot.HEAD, new ItemStack(OverlordItems.SANS_MASK));
        }
    }
}
