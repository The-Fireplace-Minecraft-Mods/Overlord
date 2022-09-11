package dev.the_fireplace.overlord.network.client.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ClientboundPacketReceiver;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.blockentity.TombstoneBlockEntity;
import dev.the_fireplace.overlord.client.gui.block.TombstoneGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.inject.Singleton;

@Singleton
public final class OpenTombstoneGUIPacketReceiver implements ClientboundPacketReceiver
{
    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf) {
        if (client.level == null) {
            OverlordConstants.getLogger().error("Received open tombstone packet with null client world!");
            return;
        }
        BlockEntity blockEntity = client.level.getBlockEntity(buf.readBlockPos());
        if (blockEntity instanceof TombstoneBlockEntity) {
            client.submit(() -> client.setScreen(new TombstoneGui((TombstoneBlockEntity) blockEntity)));
        } else {
            OverlordConstants.getLogger().error("Received open tombstone packet with missing tombstone block entity!");
        }
    }
}
