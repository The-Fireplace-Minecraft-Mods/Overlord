package the_fireplace.overlord.registry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import the_fireplace.overlord.Overlord;

/**
 * @author The_Fireplace
 */
public final class CraftingRecipes {
    public static ItemStack skeleton_maker = new ItemStack(Overlord.skeleton_maker);
    public static ItemStack baby_skeleton_maker = new ItemStack(Overlord.baby_skeleton_maker);
    public static ItemStack overlords_seal = new ItemStack(Overlord.overlords_seal);
    public static ItemStack overlords_stamp = new ItemStack(Overlord.overlords_stamp);
    public static ItemStack skinsuit = new ItemStack(Overlord.skinsuit);
    public static ItemStack squad_editor = new ItemStack(Overlord.squad_editor);
    public static ItemStack keychain = new ItemStack(Overlord.keychain);
    public static ItemStack crown = new ItemStack(Overlord.crown);

    public static void addRecipes(){
        addRecipe(skeleton_maker, "b b", "BgB", "BBB", 'b', "ingotBrickNether", 'g', "blockGlass", 'B', Blocks.NETHER_BRICK);
        addRecipe(baby_skeleton_maker, "b b", "bgb", "sss", 'b', "stone", 'g', "blockGlass", 's', Blocks.STONEBRICK);
        addSealRecipe(overlords_seal, "nin", "i i", "nin", 'n', "nuggetGold", 'i', "ingotIron");
        addSealRecipe(overlords_seal, "nin", "i i", "nin", 'n', "nuggetGold", 'i', "nuggetIron");
        addSealRecipe(overlords_stamp, " s", "sp", " d", 's', "stickWood", 'p', "paper", 'd', "dye");
        addSealRecipe(overlords_stamp, "s ", "ps", "d ", 's', "stickWood", 'p', "paper", 'd', "dye");
        addRecipe(new ItemStack(Items.CAKE), "mmm", "ses", "www", 'm', Overlord.milk_bottle, 's', Items.SUGAR, 'e', "egg", 'w', "cropWheat");
        addShapelessRecipe(skinsuit, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.ROTTEN_FLESH, Items.ROTTEN_FLESH, "dyeGreen", "dyeBlue", "dyeRed");
        addShapelessRecipe(squad_editor, Items.BOOK, "dyeBlack", "bone");
        addRecipe(keychain, "ji ", "i i", " i ", 'i', "ingotIron", 'j', "nuggetGold");
        addRecipe(keychain, "ii ", "i i", " i ", 'i', "nuggetIron");
        addRecipe(crown, "i i", "iii", " i ", 'i', "nuggetGold");
    }

    /**
     * Adds a shaped OreDictionary recipe
     * @param output
     * The recipe output
     * @param input
     * The recipe input information
     */
    public static void addRecipe(ItemStack output, Object... input){
        GameRegistry.addRecipe(new ShapedOreRecipe(output, input));
    }

    /**
     * Adds a recipe for an item which will have the Owner and OwnerName NBT Tags. Uses Ore
     * @param output
     * The recipe output
     * @param input
     * The recipe input information
     */
    public static void addSealRecipe(ItemStack output, Object... input){
        GameRegistry.addRecipe(new SealRecipe(output, input));
    }

    /**
     * Adds a shapeless OreDictionary recipe
     * @param output
     * The recipe output
     * @param input
     * The recipe input information
     */
    public static void addShapelessRecipe(ItemStack output, Object... input){
        GameRegistry.addRecipe(new ShapelessOreRecipe(output, input));
    }
}
