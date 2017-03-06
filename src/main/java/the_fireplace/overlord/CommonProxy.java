package the_fireplace.overlord;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author The_Fireplace
 */
public class CommonProxy {
    public String translateToLocal(String key, Object... args){
        return I18n.translateToLocal(key);
    }

    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().player;
    }

    public void registerClient(){}
}
