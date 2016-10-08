package the_fireplace.overlord.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import the_fireplace.overlord.client.gui.GuiRing;
import the_fireplace.overlord.client.gui.GuiSkeleton;
import the_fireplace.overlord.client.gui.GuiSkeletonMaker;
import the_fireplace.overlord.container.ContainerSkeleton;
import the_fireplace.overlord.container.ContainerSkeletonMaker;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;
import the_fireplace.overlord.tileentity.TileEntitySkeletonMaker;

/**
 * @author The_Fireplace
 */
public class OverlordGuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID) {
            case 0:
                if (entity != null && entity instanceof TileEntitySkeletonMaker) {
                    return new ContainerSkeletonMaker(player.inventory, (TileEntitySkeletonMaker) entity);
                } else {
                    return null;
                }
            case -1:
                return null;
            default:
                if(world.getEntityByID(ID) != null){
                    if(world.getEntityByID(ID) instanceof EntitySkeletonWarrior){
                        return new ContainerSkeleton(player.inventory, (EntitySkeletonWarrior)world.getEntityByID(ID));
                    }
                }
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID) {
            case 0:
                if (entity != null && entity instanceof TileEntitySkeletonMaker) {
                    return new GuiSkeletonMaker(player.inventory, (TileEntitySkeletonMaker) entity);
                } else {
                    return null;
                }
            case -1:
                return new GuiRing();
            default:
                if(world.getEntityByID(ID) != null){
                    if(world.getEntityByID(ID) instanceof EntitySkeletonWarrior){
                        return new GuiSkeleton(player.inventory, (EntitySkeletonWarrior)world.getEntityByID(ID));
                    }
                }
                return null;
        }
    }
}
