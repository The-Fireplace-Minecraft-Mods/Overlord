package dev.the_fireplace.overlord.blockentity;

import dev.the_fireplace.overlord.block.internal.CasketBlock;
import dev.the_fireplace.overlord.init.OverlordBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Objects;

public class CasketBlockEntity extends LockableContainerBlockEntity
{
    private DefaultedList<ItemStack> inventory;
    private Boolean cachedIsFoot = null;

    public CasketBlockEntity() {
        super(OverlordBlockEntities.CASKET_BLOCK_ENTITY);
        this.inventory = DefaultedList.ofSize(54, ItemStack.EMPTY);
    }

    @Override
    protected Text getContainerName() {
        if (isCasketFoot()) {
            return this.getHead().getContainerName();
        }

        return new TranslatableText("container.casket");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        if (isCasketFoot()) {
            return this.getHead().createScreenHandler(syncId, playerInventory);
        }

        return GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory, this);
    }

    @Override
    public int size() {
        return 54;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : getHead().inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return getHead().inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(getHead().inventory, slot, amount);
        if (!itemStack.isEmpty()) {
            this.getHead().markDirty();
        }

        return itemStack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(getHead().inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        CasketBlockEntity head = getHead();
        head.inventory.set(slot, stack);
        int maxStackAmount = head.getMaxCountPerStack();
        if (stack.getCount() > maxStackAmount) {
            stack.setCount(maxStackAmount);
        }

        this.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (Objects.requireNonNull(this.world).getBlockEntity(this.pos) != this) {
            return false;
        }

        return player.squaredDistanceTo((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clear() {
        this.getHead().inventory.clear();
    }

    @Override
    public void fromTag(BlockState state, NbtCompound compoundTag) {
        super.fromTag(state, compoundTag);
        if (!isCasketFoot(state)) {
            this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
            Inventories.readNbt(compoundTag, this.inventory);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound compoundTag) {
        super.writeNbt(compoundTag);
        if (!isCasketFoot()) {
            Inventories.writeNbt(compoundTag, this.inventory);
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
