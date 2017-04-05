package the_fireplace.overlord.registry;

import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.tools.Augment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.HashMap;

/**
 * @author The_Fireplace
 */
@ParametersAreNullableByDefault
public final class AugmentRegistry {
    private static HashMap<ItemStack, String> augments = Maps.newHashMap();
    private static HashMap<String, Augment> augmentIDs = Maps.newHashMap();

    public static boolean registerAugment(ItemStack item, @Nonnull Augment augment){
        if(augment.augmentId().isEmpty()) {
            Overlord.logError("Augment "+augment.getClass()+" has no ID, skipping...");
            return false;
        }
        if(item != null) {
            if (!augments.containsKey(item)) {
                item.stackSize = 1;
                augments.put(item, augment.augmentId());
            } else {
                Overlord.logError("Augment already exists for " + item.getItem() + ", skipping...");
                return false;
            }
        } else {
            Overlord.logError("Tried to register augment for null itemstack");
        }
        if(!augmentIDs.keySet().contains(augment.augmentId())) {
            augmentIDs.put(augment.augmentId(), augment);
        }else{
            Overlord.logWarn("Augment already exists for ID "+augment.augmentId()+", skipping "+augment.getClass()+"...");
        }
        return true;
    }

    @Nullable
    public static Augment getAugment(ItemStack stack){
        if(stack == null)
            return null;
        ItemStack stack1 = stack.copy();
        stack1.stackSize = 1;
        for(ItemStack augmentItem:augments.keySet()){
            if(ItemStack.areItemStacksEqual(stack1, augmentItem))
                return getAugment(augments.get(augmentItem));
        }
        return null;
    }

    @Nullable
    public static Augment getAugment(@Nullable String id){
        if(id == null || id.isEmpty())
            return null;
        else
            return augmentIDs.get(id);
    }
}
