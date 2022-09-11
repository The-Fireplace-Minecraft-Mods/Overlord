package dev.the_fireplace.overlord.blockentity;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.block.internal.CasketBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

import javax.annotation.Nullable;
import java.util.Objects;

public class CasketBlockEntity extends BaseContainerBlockEntity
{
    private NonNullList<ItemStack> inventory;
    private Boolean cachedIsFoot = null;

    public CasketBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, false);
    }

    public CasketBlockEntity(BlockPos pos, BlockState state, boolean isKnownCasketFoot) {
        super(OverlordConstants.getInjector().getInstance(OverlordBlockEntities.class).getCasketBlockEntityType(), pos, state);
        this.inventory = NonNullList.withSize(54, ItemStack.EMPTY);
        if (isKnownCasketFoot) {
            this.cachedIsFoot = true;
        }
    }

    @Override
    protected Component getDefaultName() {
        if (isCasketFoot()) {
            return this.getHead().getDefaultName();
        }

        return new TranslatableComponent("container.casket");
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        if (isCasketFoot()) {
            return this.getHead().createMenu(syncId, playerInventory);
        }

        return ChestMenu.sixRows(syncId, playerInventory, this);
    }

    @Override
    public int getContainerSize() {
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
    public ItemStack getItem(int slot) {
        return getHead().inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack itemStack = ContainerHelper.removeItem(getHead().inventory, slot, amount);
        this.getHead().setChanged();

        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = ContainerHelper.takeItem(getHead().inventory, slot);
        getHead().setChanged();
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        CasketBlockEntity head = getHead();
        head.inventory.set(slot, stack);
        int maxStackAmount = head.getMaxStackSize();
        if (stack.getCount() > maxStackAmount) {
            stack.setCount(maxStackAmount);
        }

        this.getHead().setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (Objects.requireNonNull(this.level).getBlockEntity(this.worldPosition) != this) {
            return false;
        }

        return player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clearContent() {
        this.getHead().inventory.clear();
        this.getHead().setChanged();
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!isCasketFoot()) {
            ContainerHelper.loadAllItems(compoundTag, this.inventory);
        }
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag = super.save(compoundTag);
        if (!isCasketFoot()) {
            ContainerHelper.saveAllItems(compoundTag, this.inventory);
        } else {
            ContainerHelper.saveAllItems(compoundTag, NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY));
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
        if (state == null && this.level != null) {
            state = this.level.getBlockState(worldPosition);
        }
        cachedIsFoot = state != null ?
            getType().isValid(state)
                && state.hasProperty(CasketBlock.PART)
                && state.getValue(CasketBlock.PART).equals(BedPart.FOOT)
            : null;

        return cachedIsFoot != null ? cachedIsFoot : false;
    }

    private CasketBlockEntity getHead() {
        if (!isCasketFoot() || this.level == null) {
            return this;
        }
        BlockState casketState = this.level.getBlockState(worldPosition);
        if (!getType().isValid(casketState) || !casketState.hasProperty(HorizontalDirectionalBlock.FACING)) {
            return this;
        }
        BlockPos headPosition = worldPosition.relative(CasketBlock.getDirectionTowardsOtherPart(
            BedPart.FOOT,
            casketState.getValue(HorizontalDirectionalBlock.FACING)
        ));
        BlockEntity head = this.level.getBlockEntity(headPosition);

        return head instanceof CasketBlockEntity ? (CasketBlockEntity) head : this;
    }
}
