package the_fireplace.skeletonwars.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.skeletonwars.CommonProxy;
import the_fireplace.skeletonwars.client.render.SkeletonWarriorRenderFactory;
import the_fireplace.skeletonwars.entity.EntitySkeletonWarrior;

/**
 * @author The_Fireplace
 */
public class ClientProxy extends CommonProxy {
    @Override
    public String translateToLocal(String u, String... args){
        return I18n.format(u, args);
    }

    @Override
    public void registerEntityRenderers(){
        RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonWarrior.class, new SkeletonWarriorRenderFactory());
    }

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
    }
}
