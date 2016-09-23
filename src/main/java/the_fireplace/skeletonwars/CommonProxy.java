package the_fireplace.skeletonwars;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author The_Fireplace
 */
public class CommonProxy {
    public String translateToLocal(String key, String... args){
        return key;
    }

    public void registerEntityRenderers(){

    }

    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }
}
