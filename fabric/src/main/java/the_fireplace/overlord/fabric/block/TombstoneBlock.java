package the_fireplace.overlord.fabric.block;

import net.minecraft.world.BlockView;
import the_fireplace.overlord.fabric.block.internal.AbstractTombstoneBlock;
import the_fireplace.overlord.fabric.blockentity.internal.TombstoneBlockEntity;

public class TombstoneBlock extends AbstractTombstoneBlock {
    public TombstoneBlock(Settings settings) {
        super(settings);
    }

    @Override
    public TombstoneBlockEntity createTombstone(BlockView view) {
        return null;
    }
}
