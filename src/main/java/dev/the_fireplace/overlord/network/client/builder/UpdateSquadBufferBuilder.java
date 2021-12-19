package dev.the_fireplace.overlord.network.client.builder;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public final class UpdateSquadBufferBuilder
{
    public static PacketByteBuf build(@Nullable UUID squadId, String squadName, Identifier patternId, ItemStack item, @Nullable Integer armyEntityId) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeUuid(squadId != null ? squadId : DIContainer.get().getInstance(EmptyUUID.class).get());
        buffer.writeString(squadName);
        buffer.writeIdentifier(patternId);
        buffer.writeItemStack(item);
        if (armyEntityId != null) {
            buffer.writeInt(armyEntityId);
        }
        return buffer;
    }
}
