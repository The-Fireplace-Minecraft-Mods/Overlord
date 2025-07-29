package dev.the_fireplace.overlord.loader;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Singleton;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.OverlordConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;

@Singleton
@Implementation
public final class ForgeCreativeTabHelper implements CreativeTabHelper
{
//    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = REGISTRAR.register("example", () -> CreativeModeTab.builder()
//        // Set name of tab to display
//        .title(Component.translatable("item_group." + MOD_ID + ".example"))
//        // Set icon of creative tab
//        .icon(() -> new ItemStack(ITEM.get()))
//        // Add default items to tab
//        .displayItems((params, output) -> {
//            output.accept(ITEM.get());
//            output.accept(BLOCK.get());
//        })
//        .build()
//    );

    private final Multimap<ItemLike, ItemLike> itemAfters = ArrayListMultimap.create();

    @Override
    public void registerItemAfter(ItemLike afterLast, ItemLike... items) {
        OverlordConstants.getLogger().debug("[Forge] Prefilling items to load after item {}", afterLast.asItem().getDescription().getString());
        itemAfters.putAll(afterLast, Arrays.asList(items));
    }

//    @SubscribeEvent
//    public void registerItemAfter(CreativeModeTabEvent.BuildContents event) {
//        OverlordConstants.getLogger().debug("[Forge] Prefilling items to creative tab {}", event.getTab().getDisplayName().getString());
//        itemAfters.entries().stream()
//            .filter(entry -> event.getTab().contains(new ItemStack(entry.getKey())))
//            .findFirst()
//            .ifPresent(entry -> event.accept(entry::getValue));
//    }

    @SubscribeEvent
    public void buildContents(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new ResourceLocation(OverlordConstants.MODID, "overlord"), builder ->
            // Set name of tab to display
            builder.title(Component.translatable("item_group." + OverlordConstants.MODID + ".overlord"))
                .icon(() -> new ItemStack(Items.SKELETON_SKULL))
                .displayItems((params, output) -> {
                    itemAfters.values().forEach(output::accept);
                })
        );
    }
}
