package the_fireplace.overlord.fabric.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.world.World;
import the_fireplace.overlord.OverlordHelper;

import java.util.UUID;

public class OwnedSkeletonEntity extends LivingEntity {

    private UUID owner = null, skinsuit = null;
    //There are 5 growth phases, 0 being baby and 4 being fully grown.
    private byte growthPhase = 0;
    private boolean hasSkin = false, hasMuscles = false;

    public OwnedSkeletonEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return null;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return null;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public Arm getMainArm() {
        return null;
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
