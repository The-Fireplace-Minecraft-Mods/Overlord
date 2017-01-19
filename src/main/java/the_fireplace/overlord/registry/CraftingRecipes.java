package the_fireplace.overlord.registry;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import the_fireplace.overlord.Overlord;

/**
 * @author The_Fireplace
 */
public class CraftingRecipes {
    public static ItemStack skeleton_maker = new ItemStack(Overlord.skeleton_maker);
    public static ItemStack baby_skeleton_maker = new ItemStack(Overlord.baby_skeleton_maker);
    public static ItemStack overlords_seal = new ItemStack(Overlord.overlords_seal);
    public static ItemStack overlords_stamp = new ItemStack(Overlord.overlords_stamp);
    public static ItemStack skinsuit = new ItemStack(Overlord.skinsuit);
    public static ItemStack squad_editor = new ItemStack(Overlord.squad_editor);

    public static void addRecipes(){
        addRecipe(skeleton_maker, "bgb", "ihi", 'b', Items.BUCKET, 'g', "blockGlass", 'i', "ingotIron", 'h', Items.GLASS_BOTTLE);
        addRecipe(baby_skeleton_maker, "bgb", "b b", 'b', "stone", 'g', "blockGlass");
        addRecipe(overlords_seal, "nin", "i i", "nin", 'n', "nuggetGold", 'i', "nuggetIron");
        addRecipe(overlords_stamp, " s", "sp", " d", 's', "stickWood", 'p', "paper", 'd', "dye");
        addRecipe(overlords_stamp, "s ", "ps", "d ", 's', "stickWood", 'p', "paper", 'd', "dye");
        addShapelessRecipe(skinsuit, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.ROTTEN_FLESH, Items.ROTTEN_FLESH, "dyeGreen", "dyeBlue", "dyeRed");
        addShapelessRecipe(squad_editor, Items.BOOK, "dyeBlack", "bone");
    }

    public static void addRecipe(ItemStack output, Object... input){
        GameRegistry.addRecipe(new ShapedOreRecipe(output, input));
    }

    public static void addShapelessRecipe(ItemStack output, Object... input){
        GameRegistry.addRecipe(new ShapelessOreRecipe(output, input));
    }
}
