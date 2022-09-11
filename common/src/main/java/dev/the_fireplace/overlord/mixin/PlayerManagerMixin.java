package dev.the_fireplace.overlord.mixin;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.util.SquadSync;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerManagerMixin
{
    @Inject(method = "placeNewPlayer", at = @At("RETURN"))
    private void sendSquadsOnJoinServer(Connection connection, ServerPlayer player, CallbackInfo ci) {
        try {
            OverlordConstants.getInjector().getInstance(SquadSync.class).syncTo(player);
        } catch (Exception e) {
            OverlordConstants.getLogger().error(e);
        }
    }
}
