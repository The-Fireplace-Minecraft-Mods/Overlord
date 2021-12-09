package dev.the_fireplace.overlord.network.client.builder;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

import javax.annotation.Nullable;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public final class SetSquadBufferBuilder
{
    public static PacketByteBuf build(@Nullable UUID squadId, int entityId) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeUuid(squadId != null ? squadId : DIContainer.get().getInstance(EmptyUUID.class).get());
        buffer.writeInt(entityId);
        return buffer;
    }
}
