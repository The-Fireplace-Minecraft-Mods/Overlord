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
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.SetMilkMessage;
import the_fireplace.overlord.registry.AugmentRegistry;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class TileEntitySkeletonMaker extends TileEntity implements ITickable, ISidedInventory, ISkeletonMaker {
    private ItemStack[] inventory;
    byte milk = 0;
    public static final int[] clearslots = new int[]{6,7,8,9,10,11,12};

    public TileEntitySkeletonMaker() {
        inventory = new ItemStack[13];
    }

    @Override
    public void update()
    {
        if(!getStackInSlot(4).isEmpty() && getStackInSlot(4).getItem() == Items.MILK_BUCKET && getMilk() < 2){
            if(!getStackInSlot(5).isEmpty() && getStackInSlot(5).getItem() == Items.BUCKET && getStackInSlot(5).getCount() < getStackInSlot(5).getMaxStackSize()) {
                setMilk((byte) (getMilk() + 1));
                getStackInSlot(5).grow(1);
                if(getStackInSlot(4).getCount() > 1)
                    getStackInSlot(4).shrink(1);
                else
                    setInventorySlotContents(4, ItemStack.EMPTY);
            }else if(getStackInSlot(5).isEmpty()){
                setMilk((byte) (getMilk() + 1));
                if(getStackInSlot(4).getCount() > 1)
                    getStackInSlot(4).shrink(1);
                else
                    setInventorySlotContents(4, ItemStack.EMPTY);
                setInventorySlotContents(5, new ItemStack(Items.BUCKET));
            }
        }else if(!getStackInSlot(4).isEmpty() && getStackInSlot(4).getItem() == Overlord.milk_bottle && getMilk() < 2){
            if(!getStackInSlot(5).isEmpty() && getStackInSlot(5).getItem() == Items.GLASS_BOTTLE && getStackInSlot(5).getCount() < getStackInSlot(5).getMaxStackSize()) {
                setMilk((byte) (getMilk() + 1));
                getStackInSlot(5).grow(1);
                if(getStackInSlot(4).getCount() > 1)
                    getStackInSlot(4).shrink(1);
                else
                    setInventorySlotContents(4, ItemStack.EMPTY);
            }else if(getStackInSlot(5).isEmpty()){
                setMilk((byte) (getMilk() + 1));
                if(getStackInSlot(4).getCount() > 1)
                    getStackInSlot(4).shrink(1);
                else
                    setInventorySlotContents(4, ItemStack.EMPTY);
                setInventorySlotContents(5, new ItemStack(Items.GLASS_BOTTLE));
            }
        }
    }

    @Override
    public void spawnSkeleton(){
        UUID owner = null;
        if(!getStackInSlot(0).isEmpty()){
            if(getStackInSlot(0).getTagCompound() != null){
                owner = UUID.fromString(getStackInSlot(0).getTagCompound().getString("Owner"));
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
        if(!getStackInSlot(3).isEmpty()){
            ItemStack augment = getStackInSlot(3).copy();
            augment.setCount(1);
            skeletonWarrior.equipInventory.setInventorySlotContents(6, augment);
            if(getStackInSlot(3).getCount() > 1){
                getStackInSlot(3).shrink(1);
            }else{
                setInventorySlotContents(3, ItemStack.EMPTY);
            }
        }

        world.spawnEntity(skeletonWarrior);
        if(!getStackInSlot(12).isEmpty())
            skeletonWarrior.applySkinsuit(getStackInSlot(12));
        setMilk((byte)0);
        for(int i:clearslots){
            setInventorySlotContents(i, ItemStack.EMPTY);
        }
        if(!getStackInSlot(1).isEmpty()){
            if(getStackInSlot(1).getCount() == ConfigValues.BONEREQ_WARRIOR)
                setInventorySlotContents(1, ItemStack.EMPTY);
            else if(getStackInSlot(1).getCount() < ConfigValues.BONEREQ_WARRIOR) {
                setInventorySlotContents(1, ItemStack.EMPTY);
                if(!getStackInSlot(2).isEmpty())
                if (getStackInSlot(2).getCount() <= ConfigValues.BONEREQ_WARRIOR)
                    setInventorySlotContents(2, ItemStack.EMPTY);
                else
                    getStackInSlot(2).shrink(ConfigValues.BONEREQ_WARRIOR);
            }else
                getStackInSlot(1).shrink(ConfigValues.BONEREQ_WARRIOR);
        }else{
            if(!getStackInSlot(2).isEmpty())
            if(getStackInSlot(2).getCount() <= ConfigValues.BONEREQ_WARRIOR)
                setInventorySlotContents(2, ItemStack.EMPTY);
            else
                getStackInSlot(2).shrink(ConfigValues.BONEREQ_WARRIOR);
        }
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, getBlockMetadata(), getUpdateTag());
    }

    @Override
    @Nonnull
    public NBTTagCompound getUpdateTag(){
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    @Nonnull
    public String getName() {
        return Overlord.proxy.translateToLocal("tile.skeleton_maker.name");
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("tile.skeleton_maker.name");
    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemStack : inventory)
            if(!itemStack.isEmpty())
                return false;
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int index) {
        if(inventory[index] != null)
            return inventory[index];
        else
            return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int index, int count) {
        ItemStack is = getStackInSlot(index);
        if (!is.isEmpty()) {
            if (is.getCount() <= count) {
                setInventorySlotContents(index, ItemStack.EMPTY);
            } else {
                is = is.splitStack(count);
                markDirty();
            }
        }
        return is;
    }

    @Override
    @Nonnull
    public ItemStack removeStackFromSlot(int index) {
        ItemStack is = getStackInSlot(index);
        setInventorySlotContents(index, ItemStack.EMPTY);
        return is;
    }

    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        inventory[index] = stack;

        if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
        return player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64;
    }

    @Override
    public void openInventory(@Nonnull EntityPlayer player) {
    }

    @Override
    public void closeInventory(@Nonnull EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        return (index == 0 && stack.getItem() == Overlord.overlords_seal) || ((index == 1 || index == 2) && stack.getItem() == Items.BONE) || (index == 3 && AugmentRegistry.getAugment(stack) != null) || (index == 4 && (stack.getItem() == Items.MILK_BUCKET || stack.getItem() == Overlord.milk_bottle)) || (index > 5 && index < 10 && stack.getItem().isValidArmor(stack, getSlotEquipmentType(index), null) || (index == 12 && stack.getItem() == Overlord.skinsuit));
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
            inventory[i] = ItemStack.EMPTY;
        }
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagList list = new NBTTagList();
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack is = getStackInSlot(i);
            if (!is.isEmpty()) {
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
                    setInventorySlotContents(slot, new ItemStack(item));
                }
            }
        } else {
            System.out.println("List was null when reading TileEntitySkeletonMaker NBTTagCompound");
        }
        this.milk = compound.getByte("Milk");
    }

    @Override
    @Nonnull
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        if (side == EnumFacing.EAST || side == EnumFacing.WEST || side == EnumFacing.NORTH || side == EnumFacing.SOUTH || side == EnumFacing.UP) {
            return new int[]{1, 2, 3, 4, 6, 7, 8, 9, 12};
        }else if (side == EnumFacing.DOWN) {
            return new int[]{5};
        }else{
            throw new IllegalArgumentException("Invalid side: " + side);
        }
    }

    @Override
    public boolean canInsertItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
        if (!stack.isEmpty()) {
            if (index >= 1 &&  index < 5 || index >= 6 && index < 10 || index == 12) {
                if(this.isItemValidForSlot(index, stack))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
        if (!stack.isEmpty())
            if (index == 5)
                return true;
        return false;
    }

    IItemHandler handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
    IItemHandler handlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);
    IItemHandler handlerSide = new SidedInvWrapper(this, EnumFacing.WEST);

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing)
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
