package dev.the_fireplace.overlord.blockentity;

import dev.the_fireplace.overlord.block.internal.CasketBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.enums.BedPart;
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
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Objects;

public class CasketBlockEntity extends LockableContainerBlockEntity
{
    private DefaultedList<ItemStack> inventory;
    private Boolean cachedIsFoot = null;

    public CasketBlockEntity() {
        this(false);
    }

    public CasketBlockEntity(boolean isKnownCasketFoot) {
        super(OverlordBlockEntities.CASKET_BLOCK_ENTITY);
        this.inventory = DefaultedList.ofSize(54, ItemStack.EMPTY);
        if (isKnownCasketFoot) {
            this.cachedIsFoot = true;
        }
    }

    @Override
    protected Text getContainerName() {
        if (isCasketFoot()) {
            return this.getHead().getContainerName();
        }

        return new TranslatableText("container.casket");
    }

    @Override
    protected Container createContainer(int syncId, PlayerInventory playerInventory) {
        if (isCasketFoot()) {
            return this.getHead().createContainer(syncId, playerInventory);
        }

        return GenericContainer.createGeneric9x6(syncId, playerInventory, this);
    }

    @Override
    public int getInvSize() {
        return 54;
    }

    @Override
    public boolean isInvEmpty() {
        for (ItemStack stack : getHead().inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getInvStack(int slot) {
        return getHead().inventory.get(slot);
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(getHead().inventory, slot, amount);
        this.getHead().markDirty();

        return itemStack;
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        ItemStack stack = Inventories.removeStack(getHead().inventory, slot);
        getHead().markDirty();
        return stack;
    }

    @Override
    public void setInvStack(int slot, ItemStack stack) {
        CasketBlockEntity head = getHead();
        head.inventory.set(slot, stack);
        int maxStackAmount = head.getInvMaxStackAmount();
        if (stack.getCount() > maxStackAmount) {
            stack.setCount(maxStackAmount);
        }

        this.getHead().markDirty();
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity player) {
        if (Objects.requireNonNull(this.world).getBlockEntity(this.pos) != this) {
            return false;
        }

        return player.squaredDistanceTo((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clear() {
        this.getHead().inventory.clear();
        this.getHead().markDirty();
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.inventory = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
        if (!isCasketFoot()) {
            Inventories.fromTag(compoundTag, this.inventory);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        if (!isCasketFoot()) {
            Inventories.toTag(compoundTag, this.inventory);
        } else {
            Inventories.writeNbt(compoundTag, DefaultedList.ofSize(this.size(), ItemStack.EMPTY));
        }

        return compoundTag;
    }

    private boolean isCasketFoot() {
        return isCasketFoot(null);
    }

    private boolean isCasketFoot(@Nullable BlockState state) {
        if (cachedIsFoot != null) {
            return cachedIsFoot;
        }
        if (state == null && this.world != null) {
            state = this.world.getBlockState(pos);
        }
        cachedIsFoot = state != null ?
            getType().supports(state.getBlock())
                && state.contains(CasketBlock.PART)
                && state.get(CasketBlock.PART).equals(BedPart.FOOT)
            : null;

        return cachedIsFoot != null ? cachedIsFoot : false;
    }

    private CasketBlockEntity getHead() {
        if (!isCasketFoot() || this.world == null) {
            return this;
        }
        BlockState casketState = this.world.getBlockState(pos);
        if (!getType().supports(casketState.getBlock()) || !casketState.contains(HorizontalFacingBlock.FACING)) {
            return this;
        }
        BlockPos headPosition = pos.offset(CasketBlock.getDirectionTowardsOtherPart(
            BedPart.FOOT,
            casketState.get(HorizontalFacingBlock.FACING)
        ));
        BlockEntity head = this.world.getBlockEntity(headPosition);

        return head instanceof CasketBlockEntity ? (CasketBlockEntity) head : this;
    }
}
