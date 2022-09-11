package dev.the_fireplace.overlord.util;

import dev.the_fireplace.overlord.item.OverlordItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class SkeletonSpawnUtils
{
    private final OverlordItems overlordItems;

    @Inject
    public SkeletonSpawnUtils(OverlordItems overlordItems) {
        this.overlordItems = overlordItems;
    }

    public void addMask(LivingEntity entity) {
        int daysSinceUndertaleReleased = (int) ChronoUnit.DAYS.between(LocalDate.parse("2015-09-15", DateTimeFormatter.ofPattern("y-M-d")), LocalDateTime.now());
        int daysSince1710Released = (int) ChronoUnit.DAYS.between(LocalDate.parse("2014-06-26", DateTimeFormatter.ofPattern("y-M-d")), LocalDateTime.now());
        if (entity.getCommandSenderWorld().getRandom().nextInt(daysSinceUndertaleReleased + daysSince1710Released) == 0
            && entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
        ) {
            entity.setItemSlot(EquipmentSlot.HEAD, new ItemStack(overlordItems.getSansMask()));
        }
    }
}
