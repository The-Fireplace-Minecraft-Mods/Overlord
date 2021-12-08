package dev.the_fireplace.overlord.client.gui.squad;

import dev.the_fireplace.overlord.client.gui.rendertools.OverlayButtonWidget;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import javax.annotation.Nullable;
import java.util.*;

@Environment(EnvType.CLIENT)
public class SelectorScreen extends Screen
{
    private final Screen parent;
    private final Collection<? extends Squad> ownedSquads;
    @Nullable
    private final Integer entityId;
    @Nullable
    private final UUID currentSquad;
    private SelectorWidget selectorWidget;

    public SelectorScreen(Text title, Screen parent, Collection<? extends Squad> ownedSquads, @Nullable Integer entityId, @Nullable UUID currentSquad) {
        super(title);
        this.parent = parent;
        this.currentSquad = currentSquad;
        this.entityId = entityId;
        this.ownedSquads = ownedSquads;
    }

    @Override
    protected void init() {
        selectorWidget = createSquadSelector();
        this.addDrawableChild(selectorWidget);
        //TODO select none?
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 202, this.height - 30, 200, 20, Text.of("Confirm and exit"), (button) -> {
            if (entityId != null) {
                //TODO send update packet
            }
            closeScreen();
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, this.height - 30, 200, 20, Text.of("Cancel"), (button) -> {
            closeScreen();
        }));
        this.addDrawableChild(new OverlayButtonWidget(0, this.height - 54, this.width / 3, 20, Text.of("Create Squad"), (button) -> {
            Collection<ItemStack> squadItems = getSquadItems();
            this.client.openScreen(new EditScreen(this, squadItems, null));
        }));
        //TODO edit squad button
        //TODO delete squad button
    }

    private Collection<ItemStack> getSquadItems() {
        Objects.requireNonNull(this.client);
        Collection<ItemStack> squadItems = new ArrayList<>();
        Entity entity = this.entityId != null && this.client.world != null ? this.client.world.getEntityById(entityId) : null;
        if (entity instanceof ArmyEntity) {
            if (entity instanceof OwnedSkeletonEntity) {
                squadItems.add(((OwnedSkeletonEntity) entity).getAugmentBlockStack().copy());
            }
            Inventory entityInventory = ((ArmyEntity) entity).getInventory();
            squadItems.addAll(getStacksFromInventory(entityInventory));
        }
        ClientPlayerEntity player = this.client.player;
        if (player != null) {
            Inventory playerInventory = player.getInventory();
            squadItems.addAll(getStacksFromInventory(playerInventory));
        }
        for (Squad ownedSquad : ownedSquads) {
            squadItems.add(ownedSquad.getItem());
        }
        return reduceAndDeduplicate(squadItems);
    }

    private Collection<ItemStack> getStacksFromInventory(Inventory inventory) {
        Collection<ItemStack> inventoryItems = new ArrayList<>(inventory.size() / 2);
        for (int slotIndex = 0; slotIndex < inventory.size(); slotIndex++) {
            ItemStack stack = inventory.getStack(slotIndex);
            if (!stack.isEmpty()) {
                inventoryItems.add(stack.copy());
            }
        }
        return inventoryItems;
    }

    private Collection<ItemStack> reduceAndDeduplicate(Collection<ItemStack> itemStacks) {
        Collection<ItemStack> reducedStacks = new ArrayList<>(itemStacks.size() - 2);
        for (ItemStack stack : itemStacks) {
            if (stack.isEmpty()) {
                continue;
            }
            stack = stack.copy();
            stack.setCount(1);
            ItemStack finalStack = stack;
            if (reducedStacks.stream().noneMatch(reducedStack -> ItemStack.areEqual(reducedStack, finalStack))) {
                reducedStacks.add(stack);
            }
        }

        return reducedStacks;
    }

    private SelectorWidget createSquadSelector() {
        SelectorWidget selectorWidget = new SelectorWidget(this.client, this.width / 3, this.height - 52, 0, this.height - 54, 30);
        selectorWidget.addSquads(ownedSquads);
        if (currentSquad != null) {
            selectorWidget.selectSquad(currentSquad);
        }
        return selectorWidget;
    }

    private void closeScreen() {
        MinecraftClient.getInstance().openScreen(parent);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrixStack);
        //TODO draw selected squad's data
        super.render(matrixStack, mouseX, mouseY, delta);
    }

    public void displayNewSquad(Squad squad) {
        selectorWidget.addSquads(Set.of(squad));
        selectorWidget.selectSquad(squad.getSquadId());
    }
}
