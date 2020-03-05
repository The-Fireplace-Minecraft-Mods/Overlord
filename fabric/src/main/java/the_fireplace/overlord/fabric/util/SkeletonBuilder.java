package the_fireplace.overlord.fabric.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import the_fireplace.overlord.api.Tombstone;
import the_fireplace.overlord.fabric.blockentity.CasketBlockEntity;
import the_fireplace.overlord.fabric.entity.OwnedSkeletonEntity;
import the_fireplace.overlord.fabric.tags.OverlordItemTags;

public class SkeletonBuilder {
    public static final int REQUIRED_BONE_COUNT = 64;
    public static final int REQUIRED_MILK_COUNT = 2;
    public static final int REQUIRED_MUSCLE_COUNT = 32;
    public static final int REQUIRED_SKIN_COUNT = 32;

    public static boolean hasEssentialContents(CasketBlockEntity casket) {
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

    public static boolean hasMuscles(CasketBlockEntity casket) {
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

    public static boolean hasSkin(CasketBlockEntity casket) {
        float skinCount = 0;
        for(int slot=0;slot<casket.getInvSize();slot++) {
            ItemStack stack = casket.getInvStack(slot);
            if(stack.isEmpty())
                continue;
            if(stack.getItem().isIn(OverlordItemTags.FLESH))
                skinCount += stack.getCount();
            else if(stack.getItem().equals(Items.RABBIT_HIDE))
                skinCount += stack.getCount()/4f;
        }
        //Don't use >=32 here because imprecision of floating point may result in it being slightly less than 32
        return skinCount > (REQUIRED_SKIN_COUNT - 0.1f);
    }

    public static OwnedSkeletonEntity build(CasketBlockEntity casket, Tombstone tombstone) {
        OwnedSkeletonEntity entity = OwnedSkeletonEntity.create(casket.getWorld(), tombstone.getOwner());
        return entity;
    }
}
