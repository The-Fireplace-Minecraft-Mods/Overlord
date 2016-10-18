package the_fireplace.overlord.registry;

import com.google.common.collect.Maps;
import com.sun.istack.internal.NotNull;
import net.minecraft.item.ItemStack;
import the_fireplace.overlord.tools.Augment;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author The_Fireplace
 */
public abstract class AugmentRegistry {
    private static HashMap<ItemStack, Augment> augments = Maps.newHashMap();
    private static ArrayList<String> registeredIDs = new ArrayList<>();

    public static boolean registerAugment(@NotNull ItemStack item, @NotNull Augment augment){
        if(item == null || augment == null) {
            System.out.println("Neither argument of registerAugment can be null!");
            return false;
        }
        if(augment.augmentId() == null || augment.augmentId().isEmpty()) {
            System.out.println("Augment "+augment.getClass()+" has no ID, skipping...");
            return false;
        }
        if(!registeredIDs.contains(augment.augmentId())) {
            item.stackSize=1;
            if (!augments.containsKey(item)) {
                augments.put(item, augment);
                registeredIDs.add(augment.augmentId());
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
}
