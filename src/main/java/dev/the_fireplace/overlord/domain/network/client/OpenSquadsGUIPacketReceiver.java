package dev.the_fireplace.overlord.domain.network.client;

import dev.the_fireplace.lib.api.network.interfaces.ClientPacketReceiver;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface OpenSquadsGUIPacketReceiver extends ClientPacketReceiver
{
}
