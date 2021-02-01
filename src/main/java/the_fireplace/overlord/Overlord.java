package the_fireplace.overlord;

import com.google.common.collect.Lists;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import the_fireplace.overlord.init.*;
import the_fireplace.overlord.init.datagen.*;
import the_fireplace.overlord.tags.OverlordBlockTags;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class Overlord implements ModInitializer, ILoaderHelper {

    private static final List<String> mobIds = Lists.newArrayList(),
            animalIds = Lists.newArrayList(),
            equipmentIds = Lists.newArrayList(),
            throwableIds = Lists.newArrayList();

    @Override
    public void onInitialize() {
        OverlordHelper.LOGGER.debug("Preparing bones...");
        OverlordHelper.setLoaderHelper(this);
        OverlordBlocks.registerBlocks();
        OverlordItems.registerItems();
        OverlordBlockEntities.register();
        OverlordEntities.register();
        OverlordParticleTypes.register();
        //noinspection ConstantConditions//TODO Use environment variables for this
        if(true) {
            OverlordHelper.LOGGER.debug("Generating data...");
            DataGenerator gen = new AdditiveDataGenerator(Paths.get("..", "..", "common", "src", "main", "resources"), Collections.emptySet());
            gen.install(new BlockTagsProvider(gen));
            gen.install(new EntityTypeTagsProvider(gen));
            gen.install(new ItemTagsProvider(gen));
            gen.install(new RecipesProvider(gen));
            gen.install(new LootTablesProvider(gen));
            try {
                gen.run();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        ServerStartCallback.EVENT.register(s -> {
            OverlordHelper.LOGGER.debug("Raising the dead...");
            for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
                if (!entityType.getCategory().isPeaceful())
                    mobIds.add(Registry.ENTITY_TYPE.getId(entityType).toString());
                if (entityType.getCategory().isAnimal())
                    animalIds.add(Registry.ENTITY_TYPE.getId(entityType).toString());
            }
            for(Item item: Registry.ITEM) {
                if(item.isDamageable() && (item instanceof ArmorItem
                    || !item.getModifiers(EquipmentSlot.MAINHAND).isEmpty()
                    || !item.getModifiers(EquipmentSlot.OFFHAND).isEmpty()
                    || !item.getModifiers(EquipmentSlot.HEAD).isEmpty()
                    || !item.getModifiers(EquipmentSlot.CHEST).isEmpty()
                    || !item.getModifiers(EquipmentSlot.LEGS).isEmpty()
                    || !item.getModifiers(EquipmentSlot.FEET).isEmpty()))
                    equipmentIds.add(Registry.ITEM.getId(item).toString());
            }
            //TODO figure out how to find which items can be thrown
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            //TODO Come up with a more permanent solution, shearing any animal will be problematic for compatibility
            if(!world.isClient() && player.getActiveItem().getItem() == Items.SHEARS && entity.getType().getCategory().isAnimal() && player instanceof ServerPlayerEntity) {
                player.getActiveItem().damage(1, world.random, (ServerPlayerEntity) player);
                entity.damage(DamageSource.player(player), 1);
                BlockPos pos = entity.getBlockPos().down();
                for(int x=-1;x<2;x++)
                    for(int z=-1;z<2;z++) {
                        BlockPos pos2 = pos.add(x, 0, z);
                        if(world.getBlockState(pos2).getBlock().matches(OverlordBlockTags.DIRT))
                            world.setBlockState(pos2, OverlordBlocks.BLOOD_SOAKED_SOIL.getDefaultState());
                    }
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }

    @Override
    public List<String> getMobIds() {
        return mobIds;
    }

    @Override
    public List<String> getAnimalIds() {
        return animalIds;
    }

    @Override
    public List<String> getEquipmentIds() {
        return equipmentIds;
    }

    @Override
    public List<String> getThrowableIds() {
        return throwableIds;
    }

    public static void addThrowableId(String id) {
        throwableIds.add(id);
    }
}
