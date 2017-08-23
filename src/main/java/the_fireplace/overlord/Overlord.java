package the_fireplace.overlord;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import the_fireplace.overlord.advancements.CriterionRegistry;
import the_fireplace.overlord.augments.*;
import the_fireplace.overlord.blocks.BlockSkeletonMaker;
import the_fireplace.overlord.command.*;
import the_fireplace.overlord.compat.ICompat;
import the_fireplace.overlord.compat.ic2.IC2Compat;
import the_fireplace.overlord.config.ConfigValues;
import the_fireplace.overlord.entity.EntityBabySkeleton;
import the_fireplace.overlord.entity.EntityConvertedSkeleton;
import the_fireplace.overlord.entity.EntityCuringSkeleton;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;
import the_fireplace.overlord.entity.projectile.EntityMilkBottle;
import the_fireplace.overlord.handlers.DispenseBehaviorKeychain;
import the_fireplace.overlord.items.*;
import the_fireplace.overlord.network.OverlordGuiHandler;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.registry.AugmentRegistry;
import the_fireplace.overlord.registry.MilkRegistry;
import the_fireplace.overlord.tileentity.TileEntityBabySkeletonMaker;
import the_fireplace.overlord.tileentity.TileEntitySkeletonMaker;
import the_fireplace.overlord.tools.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * @author The_Fireplace
 */
@Mod.EventBusSubscriber
@Mod(modid = Overlord.MODID, name = Overlord.MODNAME, guiFactory = "the_fireplace.overlord.client.gui.OverlordConfigGuiFactory", updateJSON = "https://bitbucket.org/The_Fireplace/minecraft-mod-updates/raw/master/overlord.json", acceptedMinecraftVersions = "[1.12,)", dependencies = "before:guideapi", version = "${version}")
public final class Overlord {
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
	public static Property MAXARROWDISTANCE_PROPERTY;
	public static Property TEAMHACK_PROPERTY;

	@SidedProxy(clientSide = "the_fireplace." + MODID + ".client.ClientProxy", serverSide = "the_fireplace." + MODID + ".CommonProxy")
	public static CommonProxy proxy;

	private static ICompat modCompat;

	public static final CreativeTabs tabOverlord = new CreativeTabs(MODID) {
		@Nonnull
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Overlord.skeleton_maker);
		}
	};

	public ArrayList<Alliance> pendingAlliances = new ArrayList<>();

	public static final ResourceLocation hornSoundLoc = new ResourceLocation(MODID, "horn");
	public static final SoundEvent HORN_SOUND = new SoundEvent(hornSoundLoc);
	public static final ResourceLocation createSkeletonSoundLoc = new ResourceLocation(MODID, "skeleton_construct");
	public static final SoundEvent CREATE_SKELETON_SOUND = new SoundEvent(createSkeletonSoundLoc);
	public static final ResourceLocation createSkeleton2SoundLoc = new ResourceLocation(MODID, "skeleton_construct_2");
	public static final SoundEvent CREATE_SKELETON_2_SOUND = new SoundEvent(createSkeleton2SoundLoc);

	public static ItemArmor.ArmorMaterial sans = EnumHelper.addArmorMaterial("SANS", "sans_mask", 20, new int[]{0, 0, 0, 0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);

	public static final Block skeleton_maker = new BlockSkeletonMaker("skeleton_maker").setHardness(5F).setResistance(15F);
	public static final Block baby_skeleton_maker = new BlockSkeletonMaker("baby_skeleton_maker").setHardness(4F).setResistance(10F);
	public static final Item overlords_seal = new ItemOverlordsSeal().setUnlocalizedName("overlords_seal").setCreativeTab(tabOverlord).setMaxStackSize(1);
	public static final Item overlords_stamp = new ItemOverlordsSeal(false, true).setUnlocalizedName("overlords_stamp").setCreativeTab(tabOverlord);
	public static final Item squad_editor = new ItemSquadEditor().setUnlocalizedName("squad_editor").setCreativeTab(tabOverlord).setMaxStackSize(1);
	public static final Item sans_mask = new ItemSansMask(sans);
	public static final Item skinsuit = new ItemSkinsuit(SkinType.PLAYER).setUnlocalizedName("skinsuit").setCreativeTab(tabOverlord).setMaxStackSize(1);
	public static final Item skinsuit_mummy = new ItemSkinsuit(SkinType.MUMMY).setUnlocalizedName("skinsuit_mummy").setCreativeTab(tabOverlord).setMaxStackSize(1);
	public static final Item warrior_spawner = new ItemWarriorSpawner().setUnlocalizedName("warrior_spawner").setCreativeTab(tabOverlord).setMaxStackSize(1);
	public static final Item baby_spawner = new ItemBabySpawner().setUnlocalizedName("baby_spawner").setCreativeTab(tabOverlord).setMaxStackSize(1);
	public static final Item converted_spawner = new ItemConvertedSpawner().setUnlocalizedName("converted_spawner").setCreativeTab(tabOverlord).setMaxStackSize(1);
	public static final Item milk_bottle = new ItemMilkBottle().setMaxStackSize(16);
	public static final Item keychain = new ItemKeychain(false).setUnlocalizedName("keychain_empty");
	public static final Item keychain_occupied = new ItemKeychain(true).setUnlocalizedName("keychain_occupied");
	public static final Item crown = new ItemCrown(ItemArmor.ArmorMaterial.GOLD);
	public static final Item rallying_horn = new ItemRallyingHorn().setUnlocalizedName("rallying_horn").setMaxStackSize(1).setCreativeTab(tabOverlord);

	public static void syncConfig() {
		ConfigValues.HELMETDAMAGE = HELMETDAMAGE_PROPERTY.getBoolean();
		ConfigValues.GHOSTLYSKINS = GHOSTLYSKINS_PROPERTY.getBoolean();
		ConfigValues.SKINSUITNAMETAGS = SKINSUITNAMETAGS_PROPERTY.getBoolean();
		ConfigValues.HUNTCREEPERS = HUNTCREEPERS_PROPERTY.getBoolean();
		ConfigValues.SUFFOCATIONWARNING = SUFFOCATIONWARNING_PROPERTY.getBoolean();
		ConfigValues.BONEREQ_WARRIOR = BONEREQ_WARRIOR_PROPERTY.getInt();
		ConfigValues.BONEREQ_BABY = BONEREQ_BABY_PROPERTY.getInt();
		ConfigValues.MAXARROWDISTANCE = MAXARROWDISTANCE_PROPERTY.getDouble();
		ConfigValues.TEAMHACK = TEAMHACK_PROPERTY.getBoolean();
		if (config.hasChanged())
			config.save();
	}

	@SuppressWarnings("deprecation")
	private static Logger LOGGER = FMLLog.getLogger();//default to that so if it somehow gets called before preInit, it isn't null.

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LOGGER = event.getModLog();
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
		MAXARROWDISTANCE_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.MAXARROWDISTANCE_NAME, ConfigValues.MAXARROWDISTANCE_DEFAULT, proxy.translateToLocal(ConfigValues.MAXARROWDISTANCE_NAME + ".tooltip"));
		TEAMHACK_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.TEAMHACK_NAME, ConfigValues.TEAMHACK_DEFAULT, proxy.translateToLocal(ConfigValues.TEAMHACK_NAME + ".tooltip"));
		BONEREQ_WARRIOR_PROPERTY.setMinValue(2);
		BONEREQ_BABY_PROPERTY.setMinValue(1);
		BONEREQ_WARRIOR_PROPERTY.setMaxValue(128);
		BONEREQ_BABY_PROPERTY.setMaxValue(64);
		MAXARROWDISTANCE_PROPERTY.setMinValue(2);
		MAXARROWDISTANCE_PROPERTY.setMaxValue(256);
		if (event.getSide().isClient()) {
			BONEREQ_WARRIOR_PROPERTY.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);
			BONEREQ_BABY_PROPERTY.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);
			MAXARROWDISTANCE_PROPERTY.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);
		}
		syncConfig();

		GameRegistry.registerTileEntity(TileEntitySkeletonMaker.class, "skeleton_maker");
		GameRegistry.registerTileEntity(TileEntityBabySkeletonMaker.class, "baby_skeleton_maker");
		int eid = -1;
		EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":skeleton_warrior"), EntitySkeletonWarrior.class, "skeleton_warrior", ++eid, instance, 128, 2, false);
		EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":skeleton_baby"), EntityBabySkeleton.class, "skeleton_baby", ++eid, instance, 64, 2, false);
		EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":milk_bottle"), EntityMilkBottle.class, "milk_bottle", ++eid, instance, 32, 10, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":skeleton_converted"), EntityConvertedSkeleton.class, "skeleton_converted", ++eid, instance, 116, 2, false);
		EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":skeleton_curing"), EntityCuringSkeleton.class, "skeleton_curing", ++eid, instance, 48, 2, false);
		proxy.registerClient();
		DataSerializers.registerSerializer(CustomDataSerializers.UNIQUE_ID);
		new CriterionRegistry();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		AugmentRegistry.registerAugment(new ItemStack(Items.GOLDEN_APPLE), new AugmentSlowRegen());
		AugmentRegistry.registerAugment(new ItemStack(Items.GOLDEN_APPLE, 1, 1), new AugmentFastRegen());
		AugmentRegistry.registerAugment(new ItemStack(Items.IRON_INGOT), new AugmentIron());
		AugmentRegistry.registerAugment(new ItemStack(Blocks.OBSIDIAN), new AugmentObsidian());
		AugmentRegistry.registerAugment(new ItemStack(Blocks.ANVIL), new AugmentAnvil());
		AugmentRegistry.registerAugment(new ItemStack(Items.SKULL, 1, 1), new AugmentWither());
		AugmentRegistry.registerAugment(new ItemStack(Items.SUGAR), new AugmentJitters());
		AugmentRegistry.registerAugment(new ItemStack(skinsuit_mummy), new AugmentMummy());

		OreDictionary.registerOre("book", squad_editor);

		MilkRegistry.getInstance().registerMilk(new ItemStack(Items.MILK_BUCKET), new ItemStack(Items.BUCKET));
		MilkRegistry.getInstance().registerMilk(new ItemStack(milk_bottle), new ItemStack(Items.GLASS_BOTTLE));

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(keychain_occupied, new DispenseBehaviorKeychain());
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(milk_bottle, new BehaviorProjectileDispense() {
			@Override
			@Nonnull
			protected IProjectile getProjectileEntity(@Nonnull World worldIn, @Nonnull IPosition position, @Nonnull ItemStack stackIn) {
				return new EntityMilkBottle(worldIn, position.getX(), position.getY(), position.getZ());
			}
		});

		if(Loader.isModLoaded("ic2")){
			modCompat = new IC2Compat();
			modCompat.init(event);
		}
	}

	@Mod.EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		MinecraftServer server = event.getServer();
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

	@Mod.EventHandler
	public void serverStarted(FMLServerStartedEvent event) {
		Alliances.load();
		Enemies.load();
		Squads.load();
	}

	private static IForgeRegistry<Block> blockRegistry = null;

	public static void registerBlock(Block block) {
		if(blockRegistry == null){
			logError("Block registry was null, could not register: "+block.getUnlocalizedName());
			return;
		}
		blockRegistry.register(block.setRegistryName(block.getUnlocalizedName().substring(5)));
	}

	private static IForgeRegistry<Item> itemRegistry = null;

	public static void registerItem(Item item) {
		if(itemRegistry == null){
			logError("Item registry was null, could not register: "+item.getUnlocalizedName());
			return;
		}
		itemRegistry.register(item.setRegistryName(item.getUnlocalizedName().substring(5)));
	}

	public static void registerItemForBlock(Block block) {
		if(itemRegistry == null){
			logError("Item registry was null, could not register: "+block.getUnlocalizedName());
			return;
		}
		itemRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	public static void registerItemBlock(ItemBlock itemBlock) {
		if(itemRegistry == null){
			logError("Item registry was null, could not register: "+itemBlock.getUnlocalizedName());
			return;
		}
		itemRegistry.register(itemBlock.setRegistryName(itemBlock.getBlock().getUnlocalizedName().substring(5)));
	}

	@SubscribeEvent
	public static void itemRegistry(RegistryEvent.Register<Item> event) {
		itemRegistry = event.getRegistry();
		registerItem(overlords_seal);
		registerItem(overlords_stamp);
		registerItem(squad_editor);
		registerItem(sans_mask);
		registerItem(skinsuit);
		registerItem(skinsuit_mummy);
		registerItem(warrior_spawner);
		registerItem(baby_spawner);
		registerItem(converted_spawner);
		registerItem(milk_bottle);
		registerItem(keychain);
		registerItem(keychain_occupied);
		registerItem(crown);
		registerItem(rallying_horn);
		registerItemForBlock(skeleton_maker);
		registerItemForBlock(baby_skeleton_maker);
	}

	@SubscribeEvent
	public static void blockRegistry(RegistryEvent.Register<Block> event) {
		blockRegistry = event.getRegistry();
		registerBlock(skeleton_maker);
		registerBlock(baby_skeleton_maker);
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemRenders() {
		rmm(skeleton_maker);
		rmm(baby_skeleton_maker);
		rmm(overlords_seal);
		rmm(overlords_stamp);
		rmm(squad_editor);
		rmm(sans_mask);
		rmm(skinsuit);
		rmm(skinsuit_mummy);
		rmm(warrior_spawner);
		rmm(baby_spawner);
		rmm(converted_spawner);
		rmm(milk_bottle);
		rmm(keychain);
		rmm(keychain_occupied);
		rmm(crown);
		rmm(rallying_horn);
		IStateMapper skeleton_maker_mapper = new StateMap.Builder().ignore(BlockSkeletonMaker.TRIGGERED).build();
		ModelLoader.setCustomStateMapper(skeleton_maker, skeleton_maker_mapper);
		ModelLoader.setCustomStateMapper(baby_skeleton_maker, skeleton_maker_mapper);
	}

	@SideOnly(Side.CLIENT)
	private static void rmm(Block b) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, new ModelResourceLocation(MODID + ':' + b.getUnlocalizedName().substring(5), "inventory"));
	}

	@SideOnly(Side.CLIENT)
	private static void rmm(Item i) {
		ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(MODID + ':' + i.getUnlocalizedName().substring(5), "inventory"));
	}

	@SubscribeEvent
	public static void soundRegistry(RegistryEvent.Register<SoundEvent> event){
		event.getRegistry().register(HORN_SOUND.setRegistryName(hornSoundLoc));
		event.getRegistry().register(CREATE_SKELETON_SOUND.setRegistryName(createSkeletonSoundLoc));
		event.getRegistry().register(CREATE_SKELETON_2_SOUND.setRegistryName(createSkeleton2SoundLoc));
	}

	public static ItemStack crusaderShield() {
		ItemStack shieldStack = new ItemStack(Items.SHIELD);
		NBTTagCompound tagCompound = new NBTTagCompound();
		NBTTagCompound bet = shieldStack.getSubCompound("BlockEntityTag");
		if (bet == null)
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

	public static void logInfo(String log, Object... params) {
		LOGGER.log(Level.INFO, log, params);
	}

	public static void logDebug(String log, Object... params) {
		LOGGER.log(Level.DEBUG, log, params);
	}

	public static void logError(String log, Object... params) {
		LOGGER.log(Level.ERROR, log, params);
	}

	public static void logTrace(String log, Object... params) {
		LOGGER.log(Level.TRACE, log, params);
	}

	public static void logWarn(String log, Object... params) {
		LOGGER.log(Level.WARN, log, params);
	}
}
