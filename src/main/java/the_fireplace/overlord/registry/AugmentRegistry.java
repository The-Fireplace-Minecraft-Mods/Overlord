package the_fireplace.overlord.registry;

import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import the_fireplace.overlord.tools.Augment;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * @author The_Fireplace
 */
public abstract class AugmentRegistry {
    private static HashMap<ItemStack, Augment> augments = Maps.newHashMap();

    public static boolean registerAugment(ItemStack item, Augment augment){
        if(item == null || augment == null)
            return false;
        item.stackSize=1;
        if(!augments.containsKey(item)) {
            augments.put(item, augment);
            return true;
        }else{
            System.out.println("Augment already exists for "+item.getItem());
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
