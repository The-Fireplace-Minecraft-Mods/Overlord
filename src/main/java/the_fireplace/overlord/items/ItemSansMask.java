package the_fireplace.overlord.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.Overlord;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
public class ItemSansMask extends ItemArmor {
    public ItemSansMask(ArmorMaterial materialIn) {
        super(materialIn, -1, EntityEquipmentSlot.HEAD);
        setUnlocalizedName("sans_mask");
    }

    @Override
    @Nonnull
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return Overlord.MODID+":textures/armor/sans.png";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CreativeTabs getCreativeTab(){
        return null;
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack){
        if(!world.isRemote)
        if(player instanceof EntityPlayerMP)
            if(((EntityPlayerMP) player).getStatFile().canUnlockAchievement(Overlord.heya))
                player.addStat(Overlord.heya);
    }
}
