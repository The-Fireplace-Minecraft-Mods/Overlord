package the_fireplace.overlord.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import the_fireplace.overlord.fabric.client.renderer.OwnedSkeletonRenderer;
import the_fireplace.overlord.fabric.init.OverlordEntities;
import the_fireplace.overlord.fabric.network.OverlordPackets;

@Environment(EnvType.CLIENT)
public class OverlordClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(OverlordEntities.OWNED_SKELETON_TYPE, (erd, ctx) -> new OwnedSkeletonRenderer(erd));
    }

    private void registerPacketHandlers() {
        ClientSidePacketRegistry.INSTANCE.register(OverlordPackets.OPEN_SKELETON_GUI_PACKET_ID, ((packetContext, packetByteBuf) -> packetContext.getTaskQueue().execute(() -> {
            //MinecraftClient.getInstance().openScreen(new GuiOwnedSkeleton());
        })));
    }
}
