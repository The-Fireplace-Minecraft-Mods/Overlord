package dev.the_fireplace.overlord.network.client.builder;

import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public final class UpdateSquadBufferBuilder
{
    private final EmptyUUID emptyUUID;

    @Inject
    public UpdateSquadBufferBuilder(EmptyUUID emptyUUID) {
        this.emptyUUID = emptyUUID;
    }

    public FriendlyByteBuf build(@Nullable UUID squadId, String squadName, ResourceLocation patternId, ItemStack item, @Nullable Integer armyEntityId) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeUUID(squadId != null ? squadId : emptyUUID.get());
        buffer.writeUtf(squadName);
        buffer.writeResourceLocation(patternId);
        buffer.writeItem(item);
        if (armyEntityId != null) {
            buffer.writeInt(armyEntityId);
        }
        return buffer;
    }
}
