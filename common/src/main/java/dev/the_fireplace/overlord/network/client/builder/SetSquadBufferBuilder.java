package dev.the_fireplace.overlord.network.client.builder;

import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public final class SetSquadBufferBuilder
{
    private final EmptyUUID emptyUUID;

    @Inject
    public SetSquadBufferBuilder(EmptyUUID emptyUUID) {
        this.emptyUUID = emptyUUID;
    }

    public FriendlyByteBuf buildForEntity(@Nullable UUID squadId, int entityId) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeUUID(squadId != null ? squadId : emptyUUID.get());
        buffer.writeInt(entityId);
        return buffer;
    }

    public FriendlyByteBuf buildForWand(@Nullable UUID squadId) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeUUID(squadId != null ? squadId : emptyUUID.get());
        buffer.writeInt(-1);
        return buffer;
    }
}
