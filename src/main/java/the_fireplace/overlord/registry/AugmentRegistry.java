package the_fireplace.overlord.registry;

import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import the_fireplace.overlord.tools.Augment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * @author The_Fireplace
 */
public abstract class AugmentRegistry {
    private static HashMap<ItemStack, Augment> augments = Maps.newHashMap();
    private static HashMap<String, Augment> augmentIDs = Maps.newHashMap();

    public static boolean registerAugment(@Nonnull ItemStack item, @Nonnull Augment augment){
        if(augment.augmentId().isEmpty()) {
            System.out.println("Augment "+augment.getClass()+" has no ID, skipping...");
            return false;
        }
        if(!augmentIDs.keySet().contains(augment.augmentId())) {
            item.stackSize=1;
            if (!augments.containsKey(item)) {
                augments.put(item, augment);
                augmentIDs.put(augment.augmentId(), augment);
                return true;
            } else {
                System.out.println("Augment already exists for " + item.getItem());
                return false;
            }
        }else{
            System.out.println("Augment already exists for ID "+augment.augmentId()+", skipping "+augment.getClass()+"...");
            return false;
        }
    }

    @Nullable
    public static Augment getAugment(ItemStack stack){
        if(stack == null)
            return null;
        ItemStack stack1 = stack.copy();
        stack1.stackSize=1;
        for(ItemStack augment:augments.keySet()){
            if(ItemStack.areItemStacksEqual(stack1, augment))
                return augments.get(augment);
        }
        return null;
    }

    @Nullable
    public static Augment getAugment(String id){
        if(id == null || id.isEmpty())
            return null;
        else
            return augmentIDs.get(id);
    }
}
