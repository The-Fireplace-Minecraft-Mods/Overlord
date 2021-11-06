package dev.the_fireplace.overlord.init;

import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.blockentity.CasketBlockEntity;
import dev.the_fireplace.overlord.blockentity.GraveMarkerBlockEntity;
import dev.the_fireplace.overlord.blockentity.TombstoneBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public final class OverlordBlockEntities
{
    public static BlockEntityType<CasketBlockEntity> CASKET_BLOCK_ENTITY;
    public static final Identifier CASKET_BLOCK_ENTITY_ID = new Identifier(Overlord.MODID, "casket");
    public static ScreenHandlerType<GenericContainerScreenHandler> CASKET_SCREEN_HANDLER;

    public static BlockEntityType<GraveMarkerBlockEntity> GRAVE_MARKER_BLOCK_ENTITY;
    public static final Identifier GRAVE_MARKER_BLOCK_ENTITY_ID = new Identifier(Overlord.MODID, "grave_marker");

    public static BlockEntityType<TombstoneBlockEntity> TOMBSTONE_BLOCK_ENTITY;
    public static final Identifier TOMBSTONE_BLOCK_ENTITY_ID = new Identifier(Overlord.MODID, "tombstone");

    public static void register() {
        CASKET_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            CASKET_BLOCK_ENTITY_ID,
            FabricBlockEntityTypeBuilder.create(
                CasketBlockEntity::new,
                OverlordBlocks.OAK_CASKET,
                OverlordBlocks.BIRCH_CASKET,
                OverlordBlocks.SPRUCE_CASKET,
                OverlordBlocks.JUNGLE_CASKET,
                OverlordBlocks.ACACIA_CASKET,
                OverlordBlocks.DARK_OAK_CASKET
            ).build(null)
        );
        CASKET_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(CASKET_BLOCK_ENTITY_ID, (syncId, playerInventory, buf) -> {
            PlayerEntity player = playerInventory.player;
            final World world = player.world;
            final BlockPos pos = buf.readBlockPos();
            return (GenericContainerScreenHandler) world.getBlockState(pos).createScreenHandlerFactory(player.world, pos).createMenu(syncId, player.getInventory(), player);
        });
        GRAVE_MARKER_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            GRAVE_MARKER_BLOCK_ENTITY_ID,
            FabricBlockEntityTypeBuilder.create(
                GraveMarkerBlockEntity::new,
                OverlordBlocks.OAK_GRAVE_MARKER,
                OverlordBlocks.BIRCH_GRAVE_MARKER,
                OverlordBlocks.JUNGLE_GRAVE_MARKER,
                OverlordBlocks.SPRUCE_GRAVE_MARKER,
                OverlordBlocks.ACACIA_GRAVE_MARKER,
                OverlordBlocks.DARK_OAK_GRAVE_MARKER
            ).build(null)
        );
        TOMBSTONE_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            TOMBSTONE_BLOCK_ENTITY_ID,
            FabricBlockEntityTypeBuilder.create(
                TombstoneBlockEntity::new,
                OverlordBlocks.STONE_TOMBSTONE,
                OverlordBlocks.DIORITE_TOMBSTONE,
                OverlordBlocks.GRANITE_TOMBSTONE,
                OverlordBlocks.ANDESITE_TOMBSTONE
            ).build(null)
        );
    }
}
