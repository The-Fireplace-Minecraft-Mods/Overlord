package the_fireplace.overlord.fabric;

import com.google.common.collect.Lists;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import the_fireplace.overlord.ILoaderHelper;
import the_fireplace.overlord.OverlordHelper;
import the_fireplace.overlord.fabric.init.OverlordBlockEntities;
import the_fireplace.overlord.fabric.init.OverlordBlocks;
import the_fireplace.overlord.fabric.init.OverlordEntities;
import the_fireplace.overlord.fabric.init.OverlordItems;
import the_fireplace.overlord.fabric.init.datagen.*;

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
        //noinspection ConstantConditions//TODO Use environment variables for this
        if(false) {
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
