package the_fireplace.overlord.fabric.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.world.World;
import the_fireplace.overlord.OverlordHelper;

import java.util.Collections;
import java.util.UUID;

public class OwnedSkeletonEntity extends LivingEntity {

    private UUID owner = null, skinsuit = null;
    private static int i = 0;
    //There are 5 growth phases, 0 being baby and 4 being fully grown.
    private byte growthPhase = (byte)(i++ % 5);
    private boolean hasSkin = (i / 5) % 2 == 0, hasMuscles = (i / 5) % 3 == 0;

    public OwnedSkeletonEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return Collections.emptySet();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    public void setGrowthPhase(byte newPhase) {
        if(newPhase < 0) {
            newPhase = 0;
            OverlordHelper.errorWithStacktrace("Attempt was made to set grown phase < 0!");
        } else if(newPhase > 4) {
            newPhase = 4;
            OverlordHelper.errorWithStacktrace("Attempt was made to set grown phase > 4!");
        }
        growthPhase = newPhase;
    }

    public byte getGrowthPhase() {
        return growthPhase;
    }

    public void setSkinsuit(UUID playerId) {
        this.skinsuit = playerId;
    }

    public UUID getSkinsuit() {
        return skinsuit;
    }

    public boolean hasSkin() {
        return hasSkin;
    }

    public void setHasSkin(boolean hasSkin) {
        this.hasSkin = hasSkin;
    }

    public boolean hasMuscles() {
        return hasMuscles;
    }

    public void setHasMuscles(boolean hasMuscles) {
        this.hasMuscles = hasMuscles;
    }

    public boolean isMeleeAttacking() {
        return false;
    }
}
