package the_fireplace.overlord.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import the_fireplace.overlord.Overlord;

/**
 * @author The_Fireplace
 */
public class Recipes {
    public static ItemStack skeleton_maker = new ItemStack(Overlord.skeleton_maker);
    public static ItemStack overlords_seal = new ItemStack(Overlord.overlords_seal);

    public static void addRecipes(){
        addRecipe(skeleton_maker, "bgb", "ihi", 'b', Items.BUCKET, 'g', "blockGlass", 'i', "ingotIron", 'h', Items.GLASS_BOTTLE);
        addRecipe(overlords_seal, "nin", "i i", "nin", 'n', "nuggetGold", 'i', "ingotIron");
    }

    public static void addRecipe(ItemStack output, Object... input){
        GameRegistry.addRecipe(new ShapedOreRecipe(output, input));
    }
}
