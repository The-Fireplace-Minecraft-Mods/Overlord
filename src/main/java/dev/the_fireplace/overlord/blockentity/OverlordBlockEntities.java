package dev.the_fireplace.overlord.blockentity;

import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.block.OverlordBlocks;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Objects;

public final class OverlordBlockEntities
{
    public static BlockEntityType<CasketBlockEntity> CASKET_BLOCK_ENTITY;
    public static final Identifier CASKET_BLOCK_ENTITY_ID = new Identifier(Overlord.MODID, "casket");

    public static BlockEntityType<GraveMarkerBlockEntity> GRAVE_MARKER_BLOCK_ENTITY;
    public static final Identifier GRAVE_MARKER_BLOCK_ENTITY_ID = new Identifier(Overlord.MODID, "grave_marker");

    public static BlockEntityType<TombstoneBlockEntity> TOMBSTONE_BLOCK_ENTITY;
    public static final Identifier TOMBSTONE_BLOCK_ENTITY_ID = new Identifier(Overlord.MODID, "tombstone");

    public static BlockEntityType<ArmySkullBlockEntity> ARMY_SKULL_BLOCK_ENTITY;
    public static final Identifier ARMY_SKULL_BLOCK_ENTITY_ID = new Identifier(Overlord.MODID, "army_skull");

    public static void register() {
        CASKET_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            CASKET_BLOCK_ENTITY_ID,
            BlockEntityType.Builder.create(
                CasketBlockEntity::new,
                OverlordBlocks.OAK_CASKET,
                OverlordBlocks.BIRCH_CASKET,
                OverlordBlocks.SPRUCE_CASKET,
                OverlordBlocks.JUNGLE_CASKET,
                OverlordBlocks.ACACIA_CASKET,
                OverlordBlocks.DARK_OAK_CASKET
            ).build(null)
        );
        ContainerProviderRegistry.INSTANCE.registerFactory(CASKET_BLOCK_ENTITY_ID, (syncId, identifier, player, buf) -> {
            final World world = player.world;
            final BlockPos pos = buf.readBlockPos();
            return Objects.requireNonNull(world.getBlockState(pos).createContainerFactory(player.world, pos))
                .createMenu(syncId, player.inventory, player);
        });
        GRAVE_MARKER_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            GRAVE_MARKER_BLOCK_ENTITY_ID,
            BlockEntityType.Builder.create(
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
            BlockEntityType.Builder.create(
                TombstoneBlockEntity::new,
                OverlordBlocks.STONE_TOMBSTONE,
                OverlordBlocks.DIORITE_TOMBSTONE,
                OverlordBlocks.GRANITE_TOMBSTONE,
                OverlordBlocks.ANDESITE_TOMBSTONE
            ).build(null)
        );
        ARMY_SKULL_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            ARMY_SKULL_BLOCK_ENTITY_ID,
            FabricBlockEntityTypeBuilder.create(
                ArmySkullBlockEntity::new,
                OverlordBlocks.FLESH_MUSCLE_SKELETON_SKULL,
                OverlordBlocks.FLESH_MUSCLE_SKELETON_WALL_SKULL,
                OverlordBlocks.FLESH_SKELETON_SKULL,
                OverlordBlocks.FLESH_SKELETON_WALL_SKULL,
                OverlordBlocks.MUSCLE_SKELETON_SKULL,
                OverlordBlocks.MUSCLE_SKELETON_WALL_SKULL
            ).build(null));
    }
}
