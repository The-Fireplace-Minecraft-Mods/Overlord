package the_fireplace.overlord.fabric.init;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import the_fireplace.overlord.OverlordHelper;
import the_fireplace.overlord.fabric.blockentity.CasketBlockEntity;

public class OverlordBlockEntities {
    public static BlockEntityType<CasketBlockEntity> CASKET_BLOCK_ENTITY;

    public static void register() {
        CASKET_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(OverlordHelper.MODID, "casket"), BlockEntityType.Builder.create(CasketBlockEntity::new, OverlordBlocks.OAK_CASKET).build(null));
    }
}
