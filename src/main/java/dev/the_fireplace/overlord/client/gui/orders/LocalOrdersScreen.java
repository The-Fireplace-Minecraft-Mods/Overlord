package dev.the_fireplace.overlord.client.gui.orders;

import com.google.inject.Key;
import com.google.inject.name.Names;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.overlord.domain.client.OrdersGuiFactory;
import dev.the_fireplace.overlord.domain.client.ScreenOpener;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.aiconfig.AISettings;
import dev.the_fireplace.overlord.item.OrdersWandItem;
import dev.the_fireplace.overlord.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.network.client.builder.IssueLocalOrdersBufferBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class LocalOrdersScreen extends Screen
{
    private final int orderDistance;
    private final ScreenOpener screenOpener;
    private final OrdersGuiFactory ordersGuiFactory;
    private final Squads squads;
    private long lastEntityScanTimestamp = 0;
    private int matchingArmyMemberCount = 0;

    public LocalOrdersScreen(int orderDistance) {
        super(new TranslatableText("gui.overlord.local_orders.name"));
        this.orderDistance = orderDistance;
        this.screenOpener = DIContainer.get().getInstance(ScreenOpener.class);
        this.ordersGuiFactory = DIContainer.get().getInstance(OrdersGuiFactory.class);
        this.squads = DIContainer.get().getInstance(Key.get(Squads.class, Names.named("client")));
    }

    @Override
    protected void init() {
        super.init();
        Objects.requireNonNull(this.client);
        Objects.requireNonNull(this.client.player);
        ButtonWidget.PressAction openOrdersScreen = (b) -> {
            ItemStack wandStack = OrdersWandItem.getActiveWand(this.client.player);
            if (!wandStack.isEmpty()) {
                AISettings settings = new AISettings();
                //noinspection ConstantConditions
                if (wandStack.hasTag() && wandStack.getTag().contains("ai")) {
                    settings.readTag(wandStack.getTag().getCompound("ai"));
                }
                client.openScreen(ordersGuiFactory.build(this, settings));
            }
        };
        //x, y, width, height
        addDrawableChild(new ButtonWidget(width / 2 - 50, height / 2, 100, 20, new TranslatableText("gui.overlord.orders"), openOrdersScreen));
        addDrawableChild(new ButtonWidget(width / 2 - 50, height / 2 + 22, 100, 20, new TranslatableText("gui.overlord.select_squad"), (b) -> screenOpener.openSquadSelectorGUI(null)));
        addDrawableChild(new ButtonWidget(width / 2 - 102, height / 2 + 44, 100, 20, new TranslatableText("gui.overlord.local_orders.issue_orders"), this::issueOrders));
        addDrawableChild(new ButtonWidget(width / 2 + 2, height / 2 + 44, 100, 20, new TranslatableText("gui.done"), (b) -> this.closeScreen()));
    }

    private void closeScreen() {
        MinecraftClient.getInstance().openScreen(null);
    }

    private void issueOrders(ButtonWidget unused) {
        ClientPlayNetworking.send(ClientToServerPacketIDs.ISSUE_LOCAL_ORDERS, IssueLocalOrdersBufferBuilder.build());
        closeScreen();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (System.currentTimeMillis() - lastEntityScanTimestamp > 2000) {
            lastEntityScanTimestamp = System.currentTimeMillis();
            countMatchingEntities();
        }
        Objects.requireNonNull(this.client);
        Objects.requireNonNull(this.client.player);
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        Text currentlyOrderingText = new TranslatableText("gui.overlord.local_orders.currently_ordering.any", orderDistance);

        ItemStack wandStack = OrdersWandItem.getActiveWand(this.client.player);
        //noinspection ConstantConditions
        if (!wandStack.isEmpty() && wandStack.hasTag() && wandStack.getTag().contains("squad")) {
            UUID squadId = wandStack.getTag().getUuid("squad");
            Squad squad = squads.getSquad(this.client.player.getUuid(), squadId);
            if (squad != null) {
                currentlyOrderingText = new TranslatableText("gui.overlord.local_orders.currently_ordering.squad", squad.getName(), orderDistance);
            }
        }
        this.textRenderer.draw(matrices, currentlyOrderingText, width / 2f - textRenderer.getWidth(currentlyOrderingText) / 2f, height / 2f - 20, 0xFFFFFF);
        Text matchingCountText = new TranslatableText("gui.overlord.local_orders.matching_count", matchingArmyMemberCount);
        this.textRenderer.draw(matrices, matchingCountText, width / 2f - textRenderer.getWidth(matchingCountText) / 2f, height / 2f - 10, 0xFFFFFF);
    }

    private void countMatchingEntities() {
        Objects.requireNonNull(this.client);
        Objects.requireNonNull(this.client.player);
        ItemStack wandStack = OrdersWandItem.getActiveWand(this.client.player);
        if (wandStack.isEmpty()) {
            matchingArmyMemberCount = 0;
            return;
        }

        //noinspection ConstantConditions
        UUID squadId = wandStack.hasTag() && wandStack.getTag().contains("squad") ? wandStack.getTag().getUuid("squad") : null;

        Collection<ArmyEntity> nearbyArmyMembers = client.player.world.getEntitiesByClass(
            ArmyEntity.class,
            client.player.getBoundingBox().expand(orderDistance),
            entity -> client.player.getUuid().equals(entity.getOwnerUuid()) && (squadId == null || entity.getSquad().equals(squadId))
        );

        matchingArmyMemberCount = nearbyArmyMembers.size();
    }
}
