package the_fireplace.overlord.registry;

import com.google.common.base.Throwables;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.ShapedOreRecipe;
import the_fireplace.overlord.items.ItemOverlordsSeal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * @author The_Fireplace
 */
public class SealRecipe extends ShapedOreRecipe {
    public SealRecipe(Block result, Object... recipe) {
        super(result, recipe);
    }

    public SealRecipe(Item result, Object... recipe) {
        super(result, recipe);
    }

    public SealRecipe(@Nonnull ItemStack result, Object... recipe) {
        super(result, recipe);
    }

    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
        ItemStack output = super.getCraftingResult(inv);
        EntityPlayer user = findPlayer(inv);
        if(output != null)
        if(output.getItem() instanceof ItemOverlordsSeal && user != null){
            if(output.getTagCompound() == null)
                output.setTagCompound(new NBTTagCompound());
            if (!output.getTagCompound().hasKey("Owner")) {
                output.getTagCompound().setString("Owner", user.getUniqueID().toString());
                output.getTagCompound().setString("OwnerName", user.getDisplayNameString());
            }
        }
        return output;
    }

    private static final Field eventHandlerField = ReflectionHelper.findField(InventoryCrafting.class, "eventHandler", "field_70465_c");
    private static final Field containerPlayerPlayerField = ReflectionHelper.findField(ContainerPlayer.class, "player", "field_82862_h");
    private static final Field slotCraftingPlayerField = ReflectionHelper.findField(SlotCrafting.class, "player", "field_75238_b");

    @Nullable
    private static EntityPlayer findPlayer(@Nonnull InventoryCrafting inv) {
        try {
            Container container = (Container) eventHandlerField.get(inv);
            if (container instanceof ContainerPlayer) {
                return (EntityPlayer) containerPlayerPlayerField.get(container);
            } else if (container instanceof ContainerWorkbench) {
                return (EntityPlayer) slotCraftingPlayerField.get(container.getSlot(0));
            } else {
                // don't know the player
                return null;
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
