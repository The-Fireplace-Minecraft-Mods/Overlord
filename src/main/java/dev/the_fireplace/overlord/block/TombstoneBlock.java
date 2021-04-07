package dev.the_fireplace.overlord.block;

import dev.the_fireplace.overlord.block.internal.AbstractTombstoneBlock;
import dev.the_fireplace.overlord.blockentity.internal.TombstoneBlockEntity;
import net.minecraft.world.BlockView;

public class TombstoneBlock extends AbstractTombstoneBlock {
    public TombstoneBlock(Settings settings) {
        super(settings);
    }

    @Override
    public TombstoneBlockEntity createTombstone(BlockView view) {
        return null;
    }
}
