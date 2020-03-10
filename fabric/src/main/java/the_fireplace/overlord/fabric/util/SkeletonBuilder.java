package the_fireplace.overlord.fabric.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;
import the_fireplace.overlord.api.Tombstone;
import the_fireplace.overlord.fabric.entity.OwnedSkeletonEntity;
import the_fireplace.overlord.fabric.tags.OverlordItemTags;

import java.util.Objects;

public class SkeletonBuilder {
    public static final int REQUIRED_BONE_COUNT = 64;
    public static final int REQUIRED_MILK_COUNT = 2;
    public static final int REQUIRED_MUSCLE_COUNT = 32;
    public static final int REQUIRED_SKIN_COUNT = 32;

    public static boolean hasEssentialContents(Inventory casket) {
        int boneCount = 0, milkCount = 0;
        for(int slot=0;slot<casket.getInvSize();slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if(stack.isEmpty())
                continue;
            if(stack.getItem().equals(Items.BONE))
                boneCount += stack.getCount();
            else if(stack.getItem().equals(Items.MILK_BUCKET))
                milkCount += stack.getCount();
        }
        return boneCount >= REQUIRED_BONE_COUNT && milkCount >= REQUIRED_MILK_COUNT;
    }

    public static void removeEssentialContents(Inventory casket) {
        int boneCount = REQUIRED_BONE_COUNT, milkCount = REQUIRED_MILK_COUNT;
        for(int slot=0;slot<casket.getInvSize() && (boneCount > 0 || milkCount > 0);slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if(stack.isEmpty())
                continue;
            if(stack.getItem().equals(Items.BONE) && boneCount > 0)
                boneCount = reduceStack(casket, slot, stack, boneCount);
            else if(stack.getItem().equals(Items.MILK_BUCKET) && milkCount > 0)
                milkCount = reduceStack(casket, slot, stack, milkCount);
        }
    }

    public static boolean hasMuscles(Inventory casket) {
        int muscleCount = 0;
        for(int slot=0;slot<casket.getInvSize();slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if(stack.isEmpty())
                continue;
            if(stack.getItem().isIn(OverlordItemTags.MUSCLE_MEAT))
                muscleCount += stack.getCount();
        }
        return muscleCount >= REQUIRED_MUSCLE_COUNT;
    }

    public static void removeMuscles(Inventory casket) {
        int muscleCount = REQUIRED_MUSCLE_COUNT;
        for(int slot=0;slot<casket.getInvSize() && muscleCount > 0;slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if(stack.isEmpty())
                continue;
            if(stack.getItem().isIn(OverlordItemTags.MUSCLE_MEAT))
                muscleCount = reduceStack(casket, slot, stack, muscleCount);
        }
    }

    public static boolean hasSkin(Inventory casket) {
        int skinCount = 0;
        for(int slot=0;slot<casket.getInvSize();slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if(stack.isEmpty())
                continue;
            if(stack.getItem().isIn(OverlordItemTags.FLESH))
                skinCount += stack.getCount();
        }
        return skinCount >= REQUIRED_SKIN_COUNT;
    }

    public static void removeSkin(Inventory casket) {
        int skinCount = REQUIRED_SKIN_COUNT;
        for(int slot=0;slot<casket.getInvSize() && skinCount > 0;slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if(stack.isEmpty())
                continue;
            if(stack.getItem().isIn(OverlordItemTags.FLESH))
                skinCount = reduceStack(casket, slot, stack, skinCount);
        }
    }

    public static OwnedSkeletonEntity build(Inventory casket, World world, Tombstone tombstone) {
        OwnedSkeletonEntity entity = OwnedSkeletonEntity.create(world, tombstone.getOwner());
        removeEssentialContents(casket);
        boolean hasSkin = hasSkin(casket);
        if(hasMuscles(casket)) {
            entity.setHasMuscles(true);
            removeMuscles(casket);
        }
        if(hasSkin) {
            entity.setHasSkin(true);
            removeSkin(casket);
        }
        if(tombstone.getNameText() != null) {
            if(hasSkin) {
                GameProfile profile = Objects.requireNonNull(world.getServer()).getUserCache().findByName(tombstone.getNameText());
                if (profile != null)
                    entity.setSkinsuit(profile.getId());
            }
            entity.setCustomName(new LiteralText(tombstone.getNameText()));
        }
        //TODO Armor
        //TODO Weapons and Tools
        //TODO Augments?
        return entity;
    }

    public static int reduceStack(Inventory inv, int slot, ItemStack stack, int amount) {
        if(stack.getCount() > amount) {
            stack.setCount(stack.getCount() - amount);
            return 0;
        } else {
            inv.setInvStack(slot, stack.getItem() instanceof BucketItem ? new ItemStack(Items.BUCKET) : ItemStack.EMPTY);
            return amount - stack.getCount();
        }
    }
}
