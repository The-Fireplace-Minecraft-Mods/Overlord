package the_fireplace.overlord.fabric.blockentity;

import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import the_fireplace.overlord.fabric.init.OverlordBlockEntities;

import java.util.Objects;

public class CasketBlockEntity extends LockableContainerBlockEntity {
    private DefaultedList<ItemStack> inventory;
    public CasketBlockEntity() {
        super(OverlordBlockEntities.CASKET_BLOCK_ENTITY);
        this.inventory = DefaultedList.ofSize(54, ItemStack.EMPTY);
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.casket");
    }

    @Override
    protected Container createContainer(int syncId, PlayerInventory playerInventory) {
        return GenericContainer.createGeneric9x6(syncId, playerInventory);
    }

    @Override
    public int getInvSize() {
        return 54;
    }

    @Override
    public boolean isInvEmpty() {
        for(ItemStack stack: inventory)
            if(!stack.isEmpty())
                return false;
        return true;
    }

    @Override
    public ItemStack getInvStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(inventory, slot, amount);
        if (!itemStack.isEmpty())
            this.markDirty();

        return itemStack;
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }

    @Override
    public void setInvStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > this.getInvMaxStackAmount())
            stack.setCount(this.getInvMaxStackAmount());

        this.markDirty();
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity player) {
        if (Objects.requireNonNull(this.world).getBlockEntity(this.pos) != this)
            return false;
        else
            return player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.inventory = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
        Inventories.fromTag(compoundTag, this.inventory);
    }

    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        Inventories.toTag(compoundTag, this.inventory);

        return compoundTag;
    }
}
