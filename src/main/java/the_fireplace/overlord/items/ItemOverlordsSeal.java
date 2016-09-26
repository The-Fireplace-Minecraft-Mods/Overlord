package the_fireplace.overlord.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import static the_fireplace.overlord.Overlord.proxy;

/**
 * @author The_Fireplace
 */
public class ItemOverlordsSeal extends Item {
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        if(stack.getTagCompound() == null)
            stack.setTagCompound(new NBTTagCompound());
        if (!stack.getTagCompound().hasKey("Owner")) {
            stack.getTagCompound().setString("Owner", playerIn.getUniqueID().toString());
            stack.getTagCompound().setString("OwnerName", playerIn.getDisplayNameString());
            return new ActionResult(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult(EnumActionResult.PASS, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("Owner")) {
            tooltip.add(proxy.translateToLocal("item.overlords_seal.tooltip", stack.getTagCompound().getString("OwnerName")));
        }else{
            tooltip.add(proxy.translateToLocal("item.overlords_seal.tooltip.default"));
        }
    }
}
