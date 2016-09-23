package the_fireplace.skeletonwars;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.skeletonwars.blocks.BlockSkeletonMaker;
import the_fireplace.skeletonwars.entity.EntitySkeletonWarrior;
import the_fireplace.skeletonwars.items.ItemNamePlate;
import the_fireplace.skeletonwars.items.ItemSansMask;
import the_fireplace.skeletonwars.network.PacketDispatcher;
import the_fireplace.skeletonwars.network.SSGuiHandler;
import the_fireplace.skeletonwars.tileentity.TileEntitySkeletonMaker;

/**
 * @author The_Fireplace
 */
@Mod(modid= SkeletonWars.MODID, name= SkeletonWars.MODNAME)
public class SkeletonWars {
    public static final String MODNAME = "Skeleton Wars";
    public static final String MODID = "skeletonwars";

    @Mod.Instance(MODID)
    public static SkeletonWars instance;

    @SidedProxy(clientSide = "the_fireplace."+MODID+".client.ClientProxy", serverSide = "the_fireplace."+MODID+".CommonProxy")
    public static CommonProxy proxy;

    public static final CreativeTabs tabSkeletonWars = new CreativeTabs(MODID) {
        @Override
        public Item getTabIconItem() {
            return Item.getItemFromBlock(SkeletonWars.skeleton_maker);
        }
    };

    public static ItemArmor.ArmorMaterial sans = EnumHelper.addArmorMaterial("SANS", "sans_mask", 20, new int[]{0,0,0,0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);

    public static final Block skeleton_maker = new BlockSkeletonMaker();
    public static final Item name_plate = new ItemNamePlate().setUnlocalizedName("name_plate").setCreativeTab(tabSkeletonWars).setMaxStackSize(1);
    public static final Item sans_mask = new ItemSansMask(sans);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        PacketDispatcher.registerPackets();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new SSGuiHandler());
        registerBlock(skeleton_maker);
        registerItem(name_plate);
        registerItem(sans_mask);
        GameRegistry.registerTileEntity(TileEntitySkeletonMaker.class, "skeleton_maker");
        if (event.getSide().isClient())
            registerItemRenders();
        int eid=-1;
        EntityRegistry.registerModEntity(EntitySkeletonWarrior.class, "skeleton_warrior", ++eid, instance, 32, 10, true);
        proxy.registerEntityRenderers();
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
    }

    @SideOnly(Side.CLIENT)
    public void registerItemRenders(){
        rmm(skeleton_maker);
        rmm(name_plate);
        rmm(sans_mask);
    }

    @SideOnly(Side.CLIENT)
    private void rmm(Block b) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, new ModelResourceLocation(MODID + ":" + b.getUnlocalizedName().substring(5), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    private void rmm(Item i) {
        ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(MODID + ":" + i.getUnlocalizedName().substring(5), "inventory"));
    }

    public void registerBlock(Block block) {
        GameRegistry.register(block.setRegistryName(block.getUnlocalizedName().substring(5)));
        GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
    }

    public void registerBlockNoItem(Block block) {
        GameRegistry.register(block.setRegistryName(block.getUnlocalizedName().substring(5)));
    }

    public void registerItem(Item item) {
        GameRegistry.register(item.setRegistryName(item.getUnlocalizedName().substring(5)));
    }

    public void registerItemBlock(ItemBlock itemBlock){
        GameRegistry.register(itemBlock.setRegistryName(itemBlock.block.getUnlocalizedName().substring(5)));
    }
}
