package dev.the_fireplace.overlord.mixin;

import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.util.SquadSync;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin
{
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void sendSquadsOnJoinServer(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        try {
            DIContainer.get().getInstance(SquadSync.class).syncTo(player);
        } catch (Exception e) {
            Overlord.getLogger().error(e);
        }
    }
}
