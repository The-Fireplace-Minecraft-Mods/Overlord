package the_fireplace.overlord;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.blocks.BlockSkeletonMaker;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;
import the_fireplace.overlord.items.ItemOverlordsSeal;
import the_fireplace.overlord.items.ItemSansMask;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.OverlordGuiHandler;
import the_fireplace.overlord.tileentity.TileEntitySkeletonMaker;
import the_fireplace.overlord.tools.Alliances;
import the_fireplace.overlord.tools.CustomDataSerializers;

/**
 * @author The_Fireplace
 */
@Mod(modid= Overlord.MODID, name= Overlord.MODNAME)
public class Overlord {
    public static final String MODNAME = "Overlord";
    public static final String MODID = "overlord";

    @Mod.Instance(MODID)
    public static Overlord instance;

    @SidedProxy(clientSide = "the_fireplace."+MODID+".client.ClientProxy", serverSide = "the_fireplace."+MODID+".CommonProxy")
    public static CommonProxy proxy;

    public static final CreativeTabs tabOverlord = new CreativeTabs(MODID) {
        @Override
        public Item getTabIconItem() {
            return Item.getItemFromBlock(Overlord.skeleton_maker);
        }
    };

    public static ItemArmor.ArmorMaterial sans = EnumHelper.addArmorMaterial("SANS", "sans_mask", 20, new int[]{0,0,0,0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);

    public static final Block skeleton_maker = new BlockSkeletonMaker();
    public static final Item overlords_seal = new ItemOverlordsSeal().setUnlocalizedName("overlords_seal").setCreativeTab(tabOverlord).setMaxStackSize(1);
    public static final Item sans_mask = new ItemSansMask(sans);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        PacketDispatcher.registerPackets();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new OverlordGuiHandler());
        registerBlock(skeleton_maker);
        registerItem(overlords_seal);
        registerItem(sans_mask);
        GameRegistry.registerTileEntity(TileEntitySkeletonMaker.class, "skeleton_maker");
        if (event.getSide().isClient())
            registerItemRenders();
        int eid=-1;
        EntityRegistry.registerModEntity(EntitySkeletonWarrior.class, "skeleton_warrior", ++eid, instance, 32, 5, true);
        proxy.registerEntityRenderers();
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
        DataSerializers.registerSerializer(CustomDataSerializers.UNIQUE_ID);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        Alliances.load();
    }

    @SideOnly(Side.CLIENT)
    public void registerItemRenders(){
        rmm(skeleton_maker);
        rmm(overlords_seal);
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
