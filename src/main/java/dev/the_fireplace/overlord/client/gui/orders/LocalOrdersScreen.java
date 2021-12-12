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
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
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
        Objects.requireNonNull(this.minecraft);
        Objects.requireNonNull(this.minecraft.player);
        ButtonWidget.PressAction openOrdersScreen = (b) -> {
            ItemStack wandStack = OrdersWandItem.getActiveWand(this.minecraft.player);
            if (!wandStack.isEmpty()) {
                AISettings settings = new AISettings();
                //noinspection ConstantConditions
                if (wandStack.hasTag() && wandStack.getTag().contains("ai")) {
                    settings.readTag(wandStack.getTag().getCompound("ai"));
                }
                minecraft.openScreen(ordersGuiFactory.build(this, settings));
            }
        };
        //x, y, width, height
        addButton(new ButtonWidget(width / 2 - 50, height / 2, 100, 20, I18n.translate("gui.overlord.orders"), openOrdersScreen));
        addButton(new ButtonWidget(width / 2 - 50, height / 2 + 22, 100, 20, I18n.translate("gui.overlord.select_squad"), (b) -> screenOpener.openSquadSelectorGUI(null)));
        addButton(new ButtonWidget(width / 2 - 102, height / 2 + 44, 100, 20, I18n.translate("gui.overlord.local_orders.issue_orders"), this::issueOrders));
        addButton(new ButtonWidget(width / 2 + 2, height / 2 + 44, 100, 20, I18n.translate("gui.done"), (b) -> this.closeScreen()));
    }

    private void closeScreen() {
        MinecraftClient.getInstance().openScreen(null);
    }

    private void issueOrders(ButtonWidget unused) {
        ClientPlayNetworking.send(ClientToServerPacketIDs.ISSUE_LOCAL_ORDERS, IssueLocalOrdersBufferBuilder.build());
        closeScreen();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (System.currentTimeMillis() - lastEntityScanTimestamp > 2000) {
            lastEntityScanTimestamp = System.currentTimeMillis();
            countMatchingEntities();
        }
        Objects.requireNonNull(this.minecraft);
        Objects.requireNonNull(this.minecraft.player);
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        String currentlyOrderingText = I18n.translate("gui.overlord.local_orders.currently_ordering.any", orderDistance);

        ItemStack wandStack = OrdersWandItem.getActiveWand(this.minecraft.player);
        //noinspection ConstantConditions
        if (!wandStack.isEmpty() && wandStack.hasTag() && wandStack.getTag().contains("squad")) {
            UUID squadId = wandStack.getTag().getUuid("squad");
            Squad squad = squads.getSquad(this.minecraft.player.getUuid(), squadId);
            if (squad != null) {
                currentlyOrderingText = I18n.translate("gui.overlord.local_orders.currently_ordering.squad", squad.getName(), orderDistance);
            }
        }
        this.font.draw(currentlyOrderingText, width / 2f - font.getStringWidth(currentlyOrderingText) / 2f, height / 2f - 20, 0xFFFFFF);
        String matchingCountText = I18n.translate("gui.overlord.local_orders.matching_count", matchingArmyMemberCount);
        this.font.draw(matchingCountText, width / 2f - font.getStringWidth(matchingCountText) / 2f, height / 2f - 10, 0xFFFFFF);
    }

    private void countMatchingEntities() {
        Objects.requireNonNull(this.minecraft);
        Objects.requireNonNull(this.minecraft.player);
        ItemStack wandStack = OrdersWandItem.getActiveWand(this.minecraft.player);
        if (wandStack.isEmpty()) {
            matchingArmyMemberCount = 0;
            return;
        }

        //noinspection ConstantConditions
        UUID squadId = wandStack.hasTag() && wandStack.getTag().contains("squad") ? wandStack.getTag().getUuid("squad") : null;

        Collection<ArmyEntity> nearbyArmyMembers = minecraft.player.world.getEntities(
            ArmyEntity.class,
            minecraft.player.getBoundingBox().expand(orderDistance),
            entity -> minecraft.player.getUuid().equals(entity.getOwnerUuid()) && (squadId == null || entity.getSquad().equals(squadId))
        );

        matchingArmyMemberCount = nearbyArmyMembers.size();
    }
}
