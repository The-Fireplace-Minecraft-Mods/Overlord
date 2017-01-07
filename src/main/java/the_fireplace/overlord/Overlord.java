package the_fireplace.overlord;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import the_fireplace.overlord.augments.*;
import the_fireplace.overlord.blocks.BlockBabySkeletonMaker;
import the_fireplace.overlord.blocks.BlockSkeletonMaker;
import the_fireplace.overlord.command.*;
import the_fireplace.overlord.config.ConfigValues;
import the_fireplace.overlord.entity.EntityBabySkeleton;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;
import the_fireplace.overlord.entity.projectile.EntityMilkBottle;
import the_fireplace.overlord.items.*;
import the_fireplace.overlord.network.OverlordGuiHandler;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.registry.AugmentRegistry;
import the_fireplace.overlord.registry.CraftingRecipes;
import the_fireplace.overlord.tileentity.TileEntityBabySkeletonMaker;
import the_fireplace.overlord.tileentity.TileEntitySkeletonMaker;
import the_fireplace.overlord.tools.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * @author The_Fireplace
 */
@Mod(modid= Overlord.MODID, name= Overlord.MODNAME, guiFactory = "the_fireplace.overlord.client.gui.OverlordConfigGuiFactory", updateJSON = "http://thefireplace.bitnamiapp.com/jsons/overlord.json", acceptedMinecraftVersions = "[1.11,)")
public class Overlord {
    public static final String MODNAME = "Overlord";
    public static final String MODID = "overlord";

    @Mod.Instance(MODID)
    public static Overlord instance;

    public static Configuration config;
    public static Property HELMETDAMAGE_PROPERTY;
    public static Property GHOSTLYSKINS_PROPERTY;
    public static Property SKINSUITNAMETAGS_PROPERTY;
    public static Property HUNTCREEPERS_PROPERTY;
    public static Property SUFFOCATIONWARNING_PROPERTY;
    public static Property BONEREQ_WARRIOR_PROPERTY;
    public static Property BONEREQ_BABY_PROPERTY;

    @SidedProxy(clientSide = "the_fireplace."+MODID+".client.ClientProxy", serverSide = "the_fireplace."+MODID+".CommonProxy")
    public static CommonProxy proxy;

    public static final CreativeTabs tabOverlord = new CreativeTabs(MODID) {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Overlord.skeleton_maker);
        }
    };

    public ArrayList<Alliance> pendingAlliances = new ArrayList<>();

    public static ItemArmor.ArmorMaterial sans = EnumHelper.addArmorMaterial("SANS", "sans_mask", 20, new int[]{0,0,0,0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);

    public static final Block skeleton_maker = new BlockSkeletonMaker();
    public static final Block baby_skeleton_maker = new BlockBabySkeletonMaker();
    public static final Item overlords_seal = new ItemOverlordsSeal().setUnlocalizedName("overlords_seal").setCreativeTab(tabOverlord).setMaxStackSize(1);
    public static final Item overlords_stamp = new ItemOverlordsSeal(false, true).setUnlocalizedName("overlords_stamp").setCreativeTab(tabOverlord);
    public static final Item squad_editor = new ItemSquadEditor().setUnlocalizedName("squad_editor").setCreativeTab(tabOverlord).setMaxStackSize(1);
    public static final Item sans_mask = new ItemSansMask(sans);
    public static final Item skinsuit = new Item().setUnlocalizedName("skinsuit").setCreativeTab(tabOverlord).setMaxStackSize(1);
    public static final Item warrior_spawner = new ItemWarriorSpawner().setUnlocalizedName("warrior_spawner").setCreativeTab(tabOverlord).setMaxStackSize(1);
    public static final Item baby_spawner = new ItemBabySpawner().setUnlocalizedName("baby_spawner").setCreativeTab(tabOverlord).setMaxStackSize(1);
    public static final Item milk_bottle = new ItemMilkBottle().setMaxStackSize(16);

    public static void syncConfig() {
        ConfigValues.HELMETDAMAGE = HELMETDAMAGE_PROPERTY.getBoolean();
        ConfigValues.GHOSTLYSKINS = GHOSTLYSKINS_PROPERTY.getBoolean();
        ConfigValues.SKINSUITNAMETAGS = SKINSUITNAMETAGS_PROPERTY.getBoolean();
        ConfigValues.HUNTCREEPERS = HUNTCREEPERS_PROPERTY.getBoolean();
        ConfigValues.SUFFOCATIONWARNING = SUFFOCATIONWARNING_PROPERTY.getBoolean();
        ConfigValues.BONEREQ_WARRIOR = BONEREQ_WARRIOR_PROPERTY.getInt();
        ConfigValues.BONEREQ_BABY = BONEREQ_BABY_PROPERTY.getInt();
        if (config.hasChanged())
            config.save();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        PacketDispatcher.registerPackets();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new OverlordGuiHandler());
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        HELMETDAMAGE_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.HELMETDAMAGE_NAME, ConfigValues.HELMETDAMAGE_DEFAULT, proxy.translateToLocal(ConfigValues.HELMETDAMAGE_NAME + ".tooltip"));
        GHOSTLYSKINS_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.GHOSTLYSKINS_NAME, ConfigValues.GHOSTLYSKINS_DEFAULT, proxy.translateToLocal(ConfigValues.GHOSTLYSKINS_NAME + ".tooltip"));
        SKINSUITNAMETAGS_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.SKINSUITNAMETAGS_NAME, ConfigValues.SKINSUITNAMETAGS_DEFAULT, proxy.translateToLocal(ConfigValues.SKINSUITNAMETAGS_NAME + ".tooltip"));
        HUNTCREEPERS_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.HUNTCREEPERS_NAME, ConfigValues.HUNTCREEPERS_DEFAULT, proxy.translateToLocal(ConfigValues.HUNTCREEPERS_NAME + ".tooltip"));
        SUFFOCATIONWARNING_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.SUFFOCATIONWARNING_NAME, ConfigValues.SUFFOCATIONWARNING_DEFAULT, proxy.translateToLocal(ConfigValues.SUFFOCATIONWARNING_NAME + ".tooltip"));
        BONEREQ_WARRIOR_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.BONEREQ_WARRIOR_NAME, ConfigValues.BONEREQ_WARRIOR_DEFAULT, proxy.translateToLocal(ConfigValues.BONEREQ_WARRIOR_NAME + ".tooltip"));
        BONEREQ_BABY_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.BONEREQ_BABY_NAME, ConfigValues.BONEREQ_BABY_DEFAULT, proxy.translateToLocal(ConfigValues.BONEREQ_BABY_NAME + ".tooltip"));
        BONEREQ_WARRIOR_PROPERTY.setMinValue(2);
        BONEREQ_BABY_PROPERTY.setMinValue(1);
        BONEREQ_WARRIOR_PROPERTY.setMaxValue(128);
        BONEREQ_BABY_PROPERTY.setMaxValue(64);
        if(event.getSide().isClient()){
            BONEREQ_WARRIOR_PROPERTY.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);
            BONEREQ_BABY_PROPERTY.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);
        }
        syncConfig();
        registerBlock(skeleton_maker);
        registerBlock(baby_skeleton_maker);
        registerItem(overlords_seal);
        registerItem(overlords_stamp);
        registerItem(squad_editor);
        registerItem(sans_mask);
        registerItem(skinsuit);
        registerItem(warrior_spawner);
        registerItem(baby_spawner);
        registerItem(milk_bottle);
        OreDictionary.registerOre("book", squad_editor);
        GameRegistry.registerTileEntity(TileEntitySkeletonMaker.class, "skeleton_maker");
        GameRegistry.registerTileEntity(TileEntityBabySkeletonMaker.class, "baby_skeleton_maker");
        int eid=-1;
        EntityRegistry.registerModEntity(new ResourceLocation(MODID+":skeleton_warrior"), EntitySkeletonWarrior.class, "skeleton_warrior", ++eid, instance, 128, 2, false);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID+":skeleton_baby"), EntityBabySkeleton.class, "skeleton_baby", ++eid, instance, 64, 2, false);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID+":milk_bottle"), EntityMilkBottle.class, "milk_bottle", ++eid, instance, 32, 10, true);
        proxy.registerClient();
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
        DataSerializers.registerSerializer(CustomDataSerializers.UNIQUE_ID);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        Alliances.load();
        Enemies.load();
        Squads.load();
        addAchievements();
        CraftingRecipes.addRecipes();
        AugmentRegistry.registerAugment(new ItemStack(Items.GOLDEN_APPLE), new AugmentSlowRegen());
        AugmentRegistry.registerAugment(new ItemStack(Items.GOLDEN_APPLE, 1, 1), new AugmentFastRegen());
        AugmentRegistry.registerAugment(new ItemStack(Items.IRON_INGOT), new AugmentIron());
        AugmentRegistry.registerAugment(new ItemStack(Blocks.OBSIDIAN), new AugmentObsidian());
        AugmentRegistry.registerAugment(new ItemStack(Blocks.ANVIL), new AugmentAnvil());
        AugmentRegistry.registerAugment(new ItemStack(Items.SKULL, 1, 1), new AugmentWither());
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        ICommandManager command = server.getCommandManager();

        ServerCommandManager serverCommand = (ServerCommandManager) command;

        serverCommand.registerCommand(new CommandAlly());
        serverCommand.registerCommand(new CommandAllyAccept());
        serverCommand.registerCommand(new CommandAllyList());
        serverCommand.registerCommand(new CommandAllyReject());
        serverCommand.registerCommand(new CommandAllyRemove());
        serverCommand.registerCommand(new CommandEnemy());
        serverCommand.registerCommand(new CommandEnemyList());
        serverCommand.registerCommand(new CommandEnemyRemove());
    }

    @SideOnly(Side.CLIENT)
    public void registerItemRenders(){
        rmm(skeleton_maker);
        rmm(baby_skeleton_maker);
        rmm(overlords_seal);
        rmm(overlords_stamp);
        rmm(squad_editor);
        rmm(sans_mask);
        rmm(skinsuit);
        rmm(warrior_spawner);
        rmm(baby_spawner);
        rmm(milk_bottle);
    }

    @SideOnly(Side.CLIENT)
    private void rmm(Block b) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, new ModelResourceLocation(MODID + ':' + b.getUnlocalizedName().substring(5), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    private void rmm(Item i) {
        ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(MODID + ':' + i.getUnlocalizedName().substring(5), "inventory"));
    }

    public void registerBlock(Block block) {
        GameRegistry.register(block.setRegistryName(block.getUnlocalizedName().substring(5)));
        GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
    }

    public void registerItem(Item item) {
        GameRegistry.register(item.setRegistryName(item.getUnlocalizedName().substring(5)));
    }

    public static ItemStack crusaderShield(){
        ItemStack shieldStack = new ItemStack(Items.SHIELD);
        NBTTagCompound tagCompound = new NBTTagCompound();
        NBTTagCompound bet = shieldStack.getSubCompound("BlockEntityTag");
        if(bet == null)
            bet = new NBTTagCompound();
        bet.setInteger("Base", 15);
        NBTTagList nbttaglist = new NBTTagList();
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("Pattern", "sc");
        nbttagcompound.setInteger("Color", 1);
        nbttaglist.appendTag(nbttagcompound);
        bet.setTag("Patterns", nbttaglist);
        tagCompound.setTag("BlockEntityTag", bet);
        shieldStack.setTagCompound(tagCompound);
        return shieldStack;
    }

    public static Achievement firstBaby = new Achievement("firstbaby", "firstbaby", 1, 1, Items.SKULL, AchievementList.BUILD_PICKAXE);
    public static Achievement firstSkeleton = new Achievement("firstskeleton", "firstskeleton", 0, 0, Items.SKULL, AchievementList.BUILD_PICKAXE);
    public static Achievement secondSkeleton = new Achievement("secondskeleton", "secondskeleton", 0, 2, Items.SKULL, firstSkeleton);
    public static Achievement firstMilk = new Achievement("firstmilk", "firstmilk", 2, 0, Items.MILK_BUCKET, firstSkeleton);
    public static Achievement firstLevel = new Achievement("firstlevel", "firstlevel", 0, -2, Items.BONE, firstSkeleton);
    public static Achievement armedSkeleton = new Achievement("armedskeleton", "armedskeleton", -2, 1, Items.WOODEN_SWORD, firstSkeleton);
    public static Achievement sally = new Achievement("sally", "sally", -2, -1, skinsuit, firstSkeleton);
    public static Achievement crusader = new Achievement("crusader", "crusader", -3, 0, crusaderShield(), firstSkeleton);
    public static Achievement milk256 = new Achievement("milk256", "milk256", 4, 0, Items.MILK_BUCKET, firstMilk);
    public static Achievement milk9001 = new Achievement("milk9001", "milk9001", 6, 0, Items.MILK_BUCKET, milk256);

    public static Achievement nmyi = new Achievement("interference", "interference", 2, 2, Items.ARROW, AchievementList.BUILD_WORK_BENCH);

    public static Achievement alliance = new Achievement("alliance", "alliance", 2, -2, Items.SHIELD, AchievementList.OPEN_INVENTORY);
    public static Achievement breakalliance = new Achievement("breakalliance", "breakalliance", 4, -2, Items.IRON_AXE, alliance);

    public static Achievement warmonger = new Achievement("warmonger", "warmonger", 2, -3, Items.IRON_SWORD, AchievementList.OPEN_INVENTORY);
    public static Achievement forgiver = new Achievement("forgiver", "forgiver", 4, -3, Items.CAKE, warmonger);

    public static Achievement wardog = new Achievement("wardog", "wardog", -3, 2, Items.COOKED_BEEF, AchievementList.OPEN_INVENTORY);

    public static Achievement heya = new Achievement("sans", "sans", 4, 4, sans_mask, AchievementList.OPEN_INVENTORY);

    private static void addAchievements(){
        firstBaby.registerStat();
        firstSkeleton.registerStat();
        secondSkeleton.registerStat();
        firstLevel.registerStat();
        firstMilk.registerStat();
        armedSkeleton.registerStat();
        sally.registerStat();
        crusader.registerStat();
        milk256.registerStat();
        milk9001.registerStat();
        milk9001.setSpecial();
        nmyi.registerStat();
        alliance.registerStat();
        breakalliance.registerStat();
        warmonger.registerStat();
        forgiver.registerStat();
        wardog.registerStat();
        heya.registerStat();
        heya.setSpecial();
        AchievementPage.registerAchievementPage(new AchievementPage(MODNAME,
                firstBaby, firstSkeleton, secondSkeleton, firstLevel, firstMilk, armedSkeleton, sally, crusader, milk256, milk9001, nmyi, alliance, breakalliance, warmonger, forgiver, wardog, heya));
    }
}
