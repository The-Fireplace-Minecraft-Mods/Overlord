package dev.the_fireplace.overlord;

import com.google.inject.Injector;
import dev.the_fireplace.annotateddi.api.entrypoints.DIModInitializer;
import dev.the_fireplace.lib.api.datagen.injectables.DataGeneratorFactory;
import dev.the_fireplace.overlord.domain.internal.network.server.ServerPacketRegistry;
import dev.the_fireplace.overlord.init.*;
import dev.the_fireplace.overlord.init.datagen.*;
import dev.the_fireplace.overlord.tags.OverlordBlockTags;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;

public class Overlord implements DIModInitializer {
    public static final String MODID = "overlord";
    private static final Logger LOGGER = LogManager.getLogger(MODID);
    public static Logger getLogger() {
        return LOGGER;
    }

    public static void errorWithStacktrace(String message, Object... args) {
        getLogger().error(message, args);
        new Throwable().printStackTrace();
    }

    @Override
    public void onInitialize(Injector diContainer) {
        getLogger().debug("Preparing bones...");
        OverlordBlocks.registerBlocks();
        OverlordItems.registerItems();
        OverlordBlockEntities.register();
        OverlordEntities.register();
        OverlordParticleTypes.register();
        ServerPacketRegistry.getInstance().registerPacketHandlers();
        //noinspection ConstantConditions//TODO Use environment variables for this
        if (true) {
            getLogger().debug("Generating data...");
            DataGenerator gen = diContainer.getInstance(DataGeneratorFactory.class).createAdditive(Paths.get("..", "..", "common", "src", "main", "resources"));
            gen.install(new BlockTagsProvider(gen));
            gen.install(new EntityTypeTagsProvider(gen));
            gen.install(new ItemTagsProvider(gen));
            gen.install(new RecipesProvider(gen));
            gen.install(new LootTablesProvider(gen));
            try {
                gen.run();
            } catch (IOException e) {
                getLogger().error(e);
            }
        }

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
}
