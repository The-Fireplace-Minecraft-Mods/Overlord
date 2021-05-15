package dev.the_fireplace.overlord.network.c2s;

import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.api.entity.OrderableEntity;
import dev.the_fireplace.overlord.api.network.c2sPackets.GetOrdersPacket;
import dev.the_fireplace.overlord.api.network.s2cPackets.OpenOrdersGUIPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.Objects;

public final class GetAIPacketHandler implements GetOrdersPacket {
    @Deprecated
    public static final GetOrdersPacket INSTANCE = new GetAIPacketHandler();
    private static final Identifier ID = new Identifier(Overlord.MODID, "get_ai");

    private GetAIPacketHandler() {}

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public PacketByteBuf buildBuffer(int aiEntityID) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(aiEntityID);
        return buffer;
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        //TODO check player proximity and ownership
        int entityId = buf.readInt();
        Entity entity = player.getEntityWorld().getEntityById(entityId);
        if (!(entity instanceof OrderableEntity)) {
            Overlord.getLogger().info("Entity is not orderable: {}", Objects.toString(entity));
            return;
        }

        PacketByteBuf ordersPacketBuffer = OpenOrdersGUIPacket.getInstance().buildBuffer(entityId, ((OrderableEntity) entity).getAISettings());
        responseSender.sendPacket(OpenOrdersGUIPacket.getInstance().getId(), ordersPacketBuffer);
    }
}
