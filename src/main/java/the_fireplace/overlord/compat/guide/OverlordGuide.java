package the_fireplace.overlord.compat.guide;

import amerifrance.guideapi.api.GuideAPI;
import amerifrance.guideapi.api.GuideBook;
import amerifrance.guideapi.api.IGuideBook;
import amerifrance.guideapi.api.IPage;
import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.category.CategoryItemStack;
import amerifrance.guideapi.entry.EntryItemStack;
import amerifrance.guideapi.page.PageIRecipe;
import amerifrance.guideapi.page.PageText;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import the_fireplace.overlord.Overlord;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Map;

import static the_fireplace.overlord.Overlord.proxy;

/**
 * @author The_Fireplace
 */
@GuideBook
public class OverlordGuide implements IGuideBook {

	public static Book myGuide;

	@Nullable
	@Override
	public Book buildBook() {
		Map<ResourceLocation, EntryAbstract> entries = Maps.newLinkedHashMap();

		List<IPage> pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.1.1.1")));
		pages.add(PageIRecipe.fromJson(new ResourceLocation(Overlord.MODID, "overlords_stamp")));
		pages.add(PageIRecipe.fromJson(new ResourceLocation(Overlord.MODID, "overlords_seal")));
		entries.put(new ResourceLocation(Overlord.MODID, "1.1"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.1.1"), new ItemStack(Overlord.skeleton_maker)));

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.1.2.1")));
		pages.add(PageIRecipe.fromJson(new ResourceLocation(Overlord.MODID, "baby_skeleton_maker")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.1.2.2")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.1.2.3")));
		entries.put(new ResourceLocation(Overlord.MODID, "1.2"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.1.2"), new ItemStack(Overlord.baby_skeleton_maker)));

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.1.3.1")));
		entries.put(new ResourceLocation(Overlord.MODID, "1.3"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.1.3"), new ItemStack(Items.GOLDEN_APPLE, 1, 1)));

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.1.4.1")));
		pages.add(PageIRecipe.fromJson(new ResourceLocation(Overlord.MODID, "skeleton_maker")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.1.4.2")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.1.4.3")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.1.4.4")));
		entries.put(new ResourceLocation(Overlord.MODID, "1.4"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.1.4"), new ItemStack(Overlord.skeleton_maker)));

		List<CategoryAbstract> categories = Lists.newArrayList();
		categories.add(new CategoryItemStack(entries, proxy.translateToLocal("overlord.guide.1"), new ItemStack(Overlord.skeleton_maker)));

		//Section 2
		entries = Maps.newLinkedHashMap();

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.2.1.1")));
		entries.put(new ResourceLocation(Overlord.MODID, "2.1"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.2.1"), new ItemStack(Items.BONE)));

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.2.2.1")));
		entries.put(new ResourceLocation(Overlord.MODID, "2.2"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.2.2"), new ItemStack(Items.GOLDEN_APPLE, 1, 1)));

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.2.3.1")));
		entries.put(new ResourceLocation(Overlord.MODID, "2.3"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.2.3"), new ItemStack(Blocks.BONE_BLOCK)));

		categories.add(new CategoryItemStack(entries, proxy.translateToLocal("overlord.guide.2"), new ItemStack(Items.SKULL)));

		//Section 3
		entries = Maps.newLinkedHashMap();

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.3.1.1")));
		entries.put(new ResourceLocation(Overlord.MODID, "3.1"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.3.1"), new ItemStack(Items.MILK_BUCKET)));

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.3.2.1")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.3.2.2")));
		entries.put(new ResourceLocation(Overlord.MODID, "3.2"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.3.2"), new ItemStack(Blocks.RED_MUSHROOM)));

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.3.3.1")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.3.3.2")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.3.3.3")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.3.3.4")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.3.3.5")));
		entries.put(new ResourceLocation(Overlord.MODID, "3.3"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.3.3"), new ItemStack(Items.SKULL, 1, 1)));

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.3.4.1")));
		pages.add(PageIRecipe.fromJson(new ResourceLocation(Overlord.MODID, "squad_editor")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.3.4.2")));
		entries.put(new ResourceLocation(Overlord.MODID, "3.4"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.3.4"), new ItemStack(Overlord.squad_editor)));

		categories.add(new CategoryItemStack(entries, proxy.translateToLocal("overlord.guide.3"), new ItemStack(Items.LEAD)));

		//Section 4
		entries = Maps.newLinkedHashMap();

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.4.1.1")));
		pages.add(PageIRecipe.fromJson(new ResourceLocation(Overlord.MODID, "skinsuit")));
		pages.add(PageIRecipe.fromJson(new ResourceLocation(Overlord.MODID, "skinsuit_mummy")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.4.1.2")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.4.1.3")));
		entries.put(new ResourceLocation(Overlord.MODID, "4.1"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.4.1"), new ItemStack(Overlord.skinsuit)));

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.4.2.1")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.4.2.2")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.4.2.3") + (Loader.isModLoaded("ic2") ? "\n"+proxy.translateToLocal("overlord.guide.4.2.ic2") : "")));
		entries.put(new ResourceLocation(Overlord.MODID, "4.2"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.4.2"), new ItemStack(Items.CHAINMAIL_CHESTPLATE)));

		categories.add(new CategoryItemStack(entries, proxy.translateToLocal("overlord.guide.4"), new ItemStack(Blocks.BEACON)));

		//Section 5
		entries = Maps.newLinkedHashMap();

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.5.1.1")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.5.1.2")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.5.1.3")));
		entries.put(new ResourceLocation(Overlord.MODID, "5.1"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.5.1"), new ItemStack(Items.CAKE)));

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.5.2.1")));
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.5.2.2")));
		entries.put(new ResourceLocation(Overlord.MODID, "5.2"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.5.2"), new ItemStack(Items.IRON_SWORD)));

		categories.add(new CategoryItemStack(entries, proxy.translateToLocal("overlord.guide.5"), new ItemStack(Items.SKULL, 1, 3)));

		//Section 6
		entries = Maps.newLinkedHashMap();

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.6.1.1")));
		pages.add(PageIRecipe.fromJson(new ResourceLocation(Overlord.MODID, "overlords_seal")));
		entries.put(new ResourceLocation(Overlord.MODID, "6.1"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.6.1"), new ItemStack(Overlord.overlords_seal)));

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.6.2.1")));
		pages.add(PageIRecipe.fromJson(new ResourceLocation(Overlord.MODID, "crown")));
		entries.put(new ResourceLocation(Overlord.MODID, "6.2"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.6.2"), new ItemStack(Overlord.crown)));

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.6.3.1")));
		pages.add(PageIRecipe.fromJson(new ResourceLocation(Overlord.MODID, "keychain_empty")));
		entries.put(new ResourceLocation(Overlord.MODID, "6.3"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.6.3"), new ItemStack(Overlord.keychain)));

		pages = Lists.newArrayList();
		pages.add(new PageText(proxy.translateToLocal("overlord.guide.6.4.1")));
		pages.add(PageIRecipe.fromJson(new ResourceLocation(Overlord.MODID, "rallying_horn")));
		entries.put(new ResourceLocation(Overlord.MODID, "6.4"), new EntryItemStack(pages, proxy.translateToLocal("overlord.guide.6.4"), new ItemStack(Overlord.rallying_horn)));

		categories.add(new CategoryItemStack(entries, proxy.translateToLocal("overlord.guide.6"), new ItemStack(Overlord.crown)));

		// Setup the book's base information
		myGuide = new Book();
		myGuide.setTitle(Overlord.MODNAME);
		myGuide.setDisplayName(proxy.translateToLocal("item.overlord_guide.name"));
		myGuide.setWelcomeMessage(proxy.translateToLocal("overlord.guide.welcome"));
		myGuide.setAuthor("The_Fireplace");
		myGuide.setColor(Color.ORANGE);
		myGuide.setCategoryList(categories);
		myGuide.setRegistryName(new ResourceLocation(Overlord.MODID, "overlord_guide"));
		myGuide.setSpawnWithBook(true);
		return myGuide;
	}

	@Override
	public void handleModel(ItemStack bookStack) {
		GuideAPI.setModel(myGuide);
	}

	@Override
	public void handlePost(ItemStack bookStack) {
		/*CraftingRecipes.addShapelessRecipe(bookStack, Items.BOOK, "bone", Items.MILK_BUCKET);
		CraftingRecipes.addShapelessRecipe(bookStack, Items.BOOK, "bone", Overlord.milk_bottle);
		CraftingRecipes.addShapelessRecipe(bookStack, "book", "bone", Items.MILK_BUCKET);
		CraftingRecipes.addShapelessRecipe(bookStack, "book", "bone", Overlord.milk_bottle);*///TODO
	}
}
