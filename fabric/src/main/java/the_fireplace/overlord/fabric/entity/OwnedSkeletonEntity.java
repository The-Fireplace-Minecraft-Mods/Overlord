package the_fireplace.overlord.fabric.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import the_fireplace.overlord.OverlordHelper;
import the_fireplace.overlord.api.Ownable;
import the_fireplace.overlord.model.AISettings;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.UUID;

public class OwnedSkeletonEntity extends LivingEntity implements Ownable {

    private UUID owner = null, skinsuit = null;
    //TODO These initial values should not be random in the final mod, leaving them randomly set for testing purposes
    private static int i = 0;
    //There are 5 growth phases, 0 being baby and 4 being fully grown.
    private byte growthPhase = (byte)(i++ % 5);
    private boolean hasSkin = (i / 5) % 2 == 0, hasMuscles = (i / 5) % 3 == 0;

    private AISettings aiSettings = new AISettings();

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

    @Override
    public UUID getOwnerId() {
        return owner;
    }

    @Nullable
    public ItemEntity dropItem(ItemStack stack, boolean bl) {
        return this.dropItem(stack, false, bl);
    }

    @Nullable
    public ItemEntity dropItem(ItemStack stack, boolean bl, boolean bl2) {
        if (stack.isEmpty()) {
            return null;
        } else {
            double d = this.getEyeY() - 0.30000001192092896D;
            ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), d, this.getZ(), stack);
            itemEntity.setPickupDelay(40);
            if (bl2) {
                itemEntity.setThrower(this.getUuid());
            }

            float f;
            float g;
            if (bl) {
                f = this.random.nextFloat() * 0.5F;
                g = this.random.nextFloat() * 6.2831855F;
                itemEntity.setVelocity(-MathHelper.sin(g) * f, 0.20000000298023224D, MathHelper.cos(g) * f);
            } else {
                g = MathHelper.sin(this.pitch * 0.017453292F);
                float j = MathHelper.cos(this.pitch * 0.017453292F);
                float k = MathHelper.sin(this.yaw * 0.017453292F);
                float l = MathHelper.cos(this.yaw * 0.017453292F);
                float m = this.random.nextFloat() * 6.2831855F;
                float n = 0.02F * this.random.nextFloat();
                itemEntity.setVelocity((double)(-k * j * 0.3F) + Math.cos(m) * (double)n, -g * 0.3F + 0.1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F, (double)(l * j * 0.3F) + Math.sin(m) * (double)n);
            }

            return itemEntity;
        }
    }
}
