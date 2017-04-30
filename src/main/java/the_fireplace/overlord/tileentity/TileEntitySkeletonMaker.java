package the_fireplace.overlord.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.config.ConfigValues;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;
import the_fireplace.overlord.items.ItemOverlordsSeal;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.SetMilkMessage;
import the_fireplace.overlord.registry.AugmentRegistry;
import the_fireplace.overlord.registry.MilkRegistry;

import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class TileEntitySkeletonMaker extends TileEntity implements ITickable, ISidedInventory, ISkeletonMaker {
    private ItemStack[] inventory;
    public static final String PROP_NAME = "TileEntitySkeletonMaker";
    byte milk = 0;
    public static final int[] clearslots = new int[]{6,7,8,9,10,11,12};

    public TileEntitySkeletonMaker() {
        inventory = new ItemStack[13];
    }

    @Override
    public void update()
    {
        if(getStackInSlot(4) != null && MilkRegistry.getInstance().isMilk(getStackInSlot(4)) && getMilk() < 2){
            if(getStackInSlot(5) != null && MilkRegistry.getInstance().getEmptiedStack(getStackInSlot(4)) != null && getStackInSlot(5).getItem() == MilkRegistry.getInstance().getEmptiedStack(getStackInSlot(4)).getItem() && getStackInSlot(5).stackSize < getStackInSlot(5).getMaxStackSize()) {
                setMilk((byte) (getMilk() + 1));
                getStackInSlot(5).stackSize++;
                if(getStackInSlot(4).stackSize > 1)
                    getStackInSlot(4).stackSize--;
                else
                    setInventorySlotContents(4, null);
            }else if(getStackInSlot(5) == null){
                setMilk((byte) (getMilk() + 1));
                setInventorySlotContents(5, MilkRegistry.getInstance().getEmptiedStack(getStackInSlot(4)));
                if(getStackInSlot(4).stackSize > 1)
                    getStackInSlot(4).stackSize--;
                else
                    setInventorySlotContents(4, null);
            }
        }
    }

    @Override
    public void spawnSkeleton(){
        UUID owner = null;
        if(getStackInSlot(0) != null){
            if(getStackInSlot(0).getTagCompound() != null){
                owner = UUID.fromString(getStackInSlot(0).getTagCompound().getString("Owner"));
                if(getStackInSlot(0).getItem() instanceof ItemOverlordsSeal)
                    if(((ItemOverlordsSeal)getStackInSlot(0).getItem()).isConsumable())
                        getStackInSlot(0).stackSize--;
            }
        }
        EntitySkeletonWarrior skeletonWarrior = new EntitySkeletonWarrior(world, owner);
        skeletonWarrior.setLocationAndAngles(pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 1, 0);
        skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.HEAD, getStackInSlot(9));
        skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.CHEST, getStackInSlot(8));
        skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.LEGS, getStackInSlot(7));
        skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.FEET, getStackInSlot(6));
        skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, getStackInSlot(10));
        skeletonWarrior.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, getStackInSlot(11));
        if(getStackInSlot(3) != null){
            ItemStack augment = getStackInSlot(3).copy();
            augment.stackSize=1;
            skeletonWarrior.equipInventory.setInventorySlotContents(6, augment);
            if(getStackInSlot(3).stackSize > 1){
                getStackInSlot(3).stackSize--;
            }else{
                setInventorySlotContents(3, null);
            }
        }

        world.spawnEntity(skeletonWarrior);
        if(getStackInSlot(12) != null)
            skeletonWarrior.applySkinsuit(getStackInSlot(12));
        setMilk((byte)0);
        for(int i:clearslots){
            setInventorySlotContents(i, null);
        }
        if(getStackInSlot(1) != null){
            if(getStackInSlot(1).stackSize == ConfigValues.BONEREQ_WARRIOR)
                setInventorySlotContents(1, null);
            else if(getStackInSlot(1).stackSize < ConfigValues.BONEREQ_WARRIOR) {
                setInventorySlotContents(1, null);
                if(getStackInSlot(2) != null)
                if (getStackInSlot(2).stackSize <= ConfigValues.BONEREQ_WARRIOR)
                    setInventorySlotContents(2, null);
                else
                    getStackInSlot(2).stackSize -= ConfigValues.BONEREQ_WARRIOR;
            }else
                getStackInSlot(1).stackSize -= ConfigValues.BONEREQ_WARRIOR;
        }else{
            if(getStackInSlot(2) != null)
            if(getStackInSlot(2).stackSize <= ConfigValues.BONEREQ_WARRIOR)
                setInventorySlotContents(2, null);
            else
                getStackInSlot(2).stackSize -= ConfigValues.BONEREQ_WARRIOR;
        }
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, getBlockMetadata(), getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag(){
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public String getName() {
        return Overlord.proxy.translateToLocal("tile.skeleton_maker.name");
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("tile.skeleton_maker.name");
    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack is = getStackInSlot(index);
        if (is != null) {
            if (is.stackSize <= count) {
                setInventorySlotContents(index, null);
            } else {
                is = is.splitStack(count);
                markDirty();
            }
        }
        return is;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack is = getStackInSlot(index);
        setInventorySlotContents(index, null);
        return is;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory[index] = stack;

        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return (index == 0 && stack.getItem() instanceof ItemOverlordsSeal) || ((index == 1 || index == 2) && stack.getItem() == Items.BONE) || (index == 3 && AugmentRegistry.getAugment(stack) != null) || (index == 4 && (stack.getItem() == Items.MILK_BUCKET || stack.getItem() == Overlord.milk_bottle)) || (index > 5 && index < 10 && stack.getItem().isValidArmor(stack, getSlotEquipmentType(index), null) || (index == 12 && stack.getItem() == Overlord.skinsuit));
    }

    private EntityEquipmentSlot getSlotEquipmentType(int index){
        if(index == 6)
            return EntityEquipmentSlot.FEET;
        if(index == 7)
            return EntityEquipmentSlot.LEGS;
        if(index == 8)
            return EntityEquipmentSlot.CHEST;
        if(index == 9)
            return EntityEquipmentSlot.HEAD;
        if(index == 10)
            return EntityEquipmentSlot.MAINHAND;
        return EntityEquipmentSlot.OFFHAND;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < inventory.length; ++i) {
            inventory[i] = null;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagList list = new NBTTagList();
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack is = getStackInSlot(i);
            if (is != null) {
                NBTTagCompound item = new NBTTagCompound();

                item.setByte("SlotSkeletonMaker", (byte) i);
                is.writeToNBT(item);

                list.appendTag(item);
            }
        }
        compound.setByte("Milk", milk);
        compound.setTag("ItemsSkeletonMaker", list);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList list = (NBTTagList) compound.getTag("ItemsSkeletonMaker");
        if (list != null) {
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound item = (NBTTagCompound) list.get(i);
                int slot = item.getByte("SlotSkeletonMaker");
                if (slot >= 0 && slot < getSizeInventory()) {
                    setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
                }
            }
        } else {
            Overlord.logWarn("List was null when reading TileEntitySkeletonMaker NBTTagCompound");
        }
        this.milk = compound.getByte("Milk");
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.EAST || side == EnumFacing.WEST || side == EnumFacing.NORTH || side == EnumFacing.SOUTH || side == EnumFacing.UP) {
            return new int[]{1, 2, 3, 4, 6, 7, 8, 9, 12};
        }else if (side == EnumFacing.DOWN) {
            return new int[]{5};
        }
        return null;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        if (stack != null) {
            if (index >= 1 &&  index < 5 || index >= 6 && index < 10 || index == 12) {
                if(this.isItemValidForSlot(index, stack))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        if (stack != null)
            if (index == 5)
                return true;
        return false;
    }

    IItemHandler handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
    IItemHandler handlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);
    IItemHandler handlerSide = new SidedInvWrapper(this, EnumFacing.WEST);

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            if (facing == EnumFacing.DOWN)
                return (T) handlerBottom;
            else if (facing == EnumFacing.UP)
                return (T) handlerTop;
            else
                return (T) handlerSide;
        return super.getCapability(capability, facing);
    }

    public byte getMilk(){
        return milk;
    }

    public void setMilk(byte milk){
        this.milk = milk;
        markDirty();
        if(!world.isRemote) {
            PacketDispatcher.sendToAllAround(new SetMilkMessage(pos, milk), world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 16);
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }
}
