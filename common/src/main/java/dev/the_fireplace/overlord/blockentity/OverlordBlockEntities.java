package dev.the_fireplace.overlord.blockentity;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.block.OverlordBlocks;
import dev.the_fireplace.overlord.loader.BlockEntityLoaderHelper;
import dev.the_fireplace.overlord.loader.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class OverlordBlockEntities
{
    public static final ResourceLocation CASKET_BLOCK_ENTITY_ID = new ResourceLocation(OverlordConstants.MODID, "casket");
    public static final ResourceLocation GRAVE_MARKER_BLOCK_ENTITY_ID = new ResourceLocation(OverlordConstants.MODID, "grave_marker");
    public static final ResourceLocation TOMBSTONE_BLOCK_ENTITY_ID = new ResourceLocation(OverlordConstants.MODID, "tombstone");
    public static final ResourceLocation ARMY_SKULL_BLOCK_ENTITY_ID = new ResourceLocation(OverlordConstants.MODID, "army_skull");
    private final BlockEntityLoaderHelper blockEntityLoaderHelper;
    private final OverlordBlocks overlordBlocks;
    private BlockEntityType<CasketBlockEntity> casketBlockEntityType;

    private BlockEntityType<GraveMarkerBlockEntity> graveMarkerBlockEntityType;

    private BlockEntityType<TombstoneBlockEntity> tombstoneBlockEntityType;

    private BlockEntityType<ArmySkullBlockEntity> armySkullBlockEntityType;

    private RegistryHelper<BlockEntityType<?>> blockEntityRegistry = (id, value) -> Registry.register(Registry.BLOCK_ENTITY_TYPE, id, value);

    @Inject
    public OverlordBlockEntities(BlockEntityLoaderHelper blockEntityLoaderHelper, OverlordBlocks overlordBlocks) {
        this.blockEntityLoaderHelper = blockEntityLoaderHelper;
        this.overlordBlocks = overlordBlocks;
    }

    public void register() {
        casketBlockEntityType = blockEntityLoaderHelper.createType(
            CasketBlockEntity::new,
            overlordBlocks.getOakCasket(),
            overlordBlocks.getBirchCasket(),
            overlordBlocks.getSpruceCasket(),
            overlordBlocks.getJungleCasket(),
            overlordBlocks.getAcaciaCasket(),
            overlordBlocks.getDarkOakCasket()
        );
        blockEntityRegistry.register(CASKET_BLOCK_ENTITY_ID, casketBlockEntityType);
        blockEntityLoaderHelper.registerCasketMenu();
        graveMarkerBlockEntityType = blockEntityLoaderHelper.createType(
            GraveMarkerBlockEntity::new,
            overlordBlocks.getOakGraveMarker(),
            overlordBlocks.getBirchGraveMarker(),
            overlordBlocks.getJungleGraveMarker(),
            overlordBlocks.getSpruceGraveMarker(),
            overlordBlocks.getAcaciaGraveMarker(),
            overlordBlocks.getDarkOakGraveMarker()
        );
        blockEntityRegistry.register(GRAVE_MARKER_BLOCK_ENTITY_ID, graveMarkerBlockEntityType);
        tombstoneBlockEntityType = blockEntityLoaderHelper.createType(
            TombstoneBlockEntity::new,
            overlordBlocks.getStoneTombstone(),
            overlordBlocks.getDioriteTombstone(),
            overlordBlocks.getGraniteTombstone(),
            overlordBlocks.getAndesiteTombstone()
        );
        blockEntityRegistry.register(TOMBSTONE_BLOCK_ENTITY_ID, tombstoneBlockEntityType);
        armySkullBlockEntityType = blockEntityLoaderHelper.createType(
            ArmySkullBlockEntity::new,
            overlordBlocks.getFleshMuscleSkeletonSkull(),
            overlordBlocks.getFleshMuscleSkeletonWallSkull(),
            overlordBlocks.getFleshSkeletonSkull(),
            overlordBlocks.getFleshSkeletonWallSkull(),
            overlordBlocks.getMuscleSkeletonSkull(),
            overlordBlocks.getMuscleSkeletonWallSkull()
        );
        blockEntityRegistry.register(ARMY_SKULL_BLOCK_ENTITY_ID, armySkullBlockEntityType);
    }

    public void setBlockEntityRegistry(RegistryHelper<BlockEntityType<?>> blockEntityRegistry) {
        this.blockEntityRegistry = blockEntityRegistry;
    }

    public BlockEntityType<CasketBlockEntity> getCasketBlockEntityType() {
        return casketBlockEntityType;
    }

    public BlockEntityType<GraveMarkerBlockEntity> getGraveMarkerBlockEntityType() {
        return graveMarkerBlockEntityType;
    }

    public BlockEntityType<TombstoneBlockEntity> getTombstoneBlockEntityType() {
        return tombstoneBlockEntityType;
    }

    public BlockEntityType<ArmySkullBlockEntity> getArmySkullBlockEntityType() {
        return armySkullBlockEntityType;
    }
}
