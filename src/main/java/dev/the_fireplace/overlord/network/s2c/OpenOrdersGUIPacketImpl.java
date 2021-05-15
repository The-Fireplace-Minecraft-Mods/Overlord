package dev.the_fireplace.overlord.network.s2c;

import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.api.client.OrdersGuiFactory;
import dev.the_fireplace.overlord.api.entity.OrderableEntity;
import dev.the_fireplace.overlord.api.network.s2cPackets.OpenOrdersGUIPacket;
import dev.the_fireplace.overlord.model.aiconfig.AISettings;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.Objects;

public final class OpenOrdersGUIPacketImpl implements OpenOrdersGUIPacket {
    @Deprecated
    public static final OpenOrdersGUIPacket INSTANCE = new OpenOrdersGUIPacketImpl();
    private static final Identifier ID = new Identifier(Overlord.MODID, "open_ai_gui");

    private OpenOrdersGUIPacketImpl() {}

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public PacketByteBuf buildBuffer(int aiEntityID, AISettings entitySettings) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeCompoundTag(entitySettings.toTag());
        buffer.writeInt(aiEntityID);
        return buffer;
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (client.world == null) {
            Overlord.getLogger().error("Received open orders packet with null client world!");
            return;
        }
        Entity entity = client.world.getEntityById(buf.readInt());
        if (!(entity instanceof OrderableEntity)) {
            Overlord.getLogger().info("Received open orders packet for non orderable entity: {}", Objects.toString(entity));
            return;
        }
        CompoundTag aiCompound = buf.readCompoundTag();
        if (aiCompound == null) {
            Overlord.getLogger().error("Received open orders packet with null ai settings!");
            return;
        }
        AISettings newSettings = new AISettings();
        newSettings.readTag(aiCompound);
        ((OrderableEntity) entity).updateAISettings(newSettings);
        Screen parentScreen = client.currentScreen;
        if (parentScreen == null) {
            Overlord.getLogger().warn("Parent screen is null, attempting to open orders GUI anyways!");
        }
        //noinspection ConstantConditions
        client.openScreen(OrdersGuiFactory.getInstance().build(parentScreen, (OrderableEntity) entity));
    }
}
