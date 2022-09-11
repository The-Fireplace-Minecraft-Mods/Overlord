package dev.the_fireplace.overlord.client.gui.orders;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import dev.the_fireplace.lib.api.network.injectables.PacketSender;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.client.OrdersGuiFactory;
import dev.the_fireplace.overlord.domain.client.ScreenOpener;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.entity.ai.aiconfig.AISettings;
import dev.the_fireplace.overlord.item.OrdersWandItem;
import dev.the_fireplace.overlord.network.ServerboundPackets;
import dev.the_fireplace.overlord.network.client.builder.IssueLocalOrdersBufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class LocalOrdersScreen extends Screen
{
    private final int orderDistance;
    private final ScreenOpener screenOpener;
    private final OrdersGuiFactory ordersGuiFactory;
    private final Squads squads;
    private final PacketSender packetSender;
    private final ServerboundPackets serverboundPackets;
    private final IssueLocalOrdersBufferBuilder issueLocalOrdersBufferBuilder;
    private long lastEntityScanTimestamp = 0;
    private int matchingArmyMemberCount = 0;

    public LocalOrdersScreen(int orderDistance) {
        super(new TranslatableComponent("gui.overlord.local_orders.name"));
        this.orderDistance = orderDistance;
        Injector injector = OverlordConstants.getInjector();
        this.screenOpener = injector.getInstance(ScreenOpener.class);
        this.ordersGuiFactory = injector.getInstance(OrdersGuiFactory.class);
        this.squads = injector.getInstance(Key.get(Squads.class, Names.named("client")));
        this.packetSender = injector.getInstance(PacketSender.class);
        this.serverboundPackets = injector.getInstance(ServerboundPackets.class);
        this.issueLocalOrdersBufferBuilder = injector.getInstance(IssueLocalOrdersBufferBuilder.class);
    }

    @Override
    protected void init() {
        super.init();
        Objects.requireNonNull(this.minecraft);
        Objects.requireNonNull(this.minecraft.player);
        Button.OnPress openOrdersScreen = (b) -> {
            ItemStack wandStack = OrdersWandItem.getActiveWand(this.minecraft.player);
            if (!wandStack.isEmpty()) {
                AISettings settings = new AISettings();
                //noinspection ConstantConditions
                if (wandStack.hasTag() && wandStack.getTag().contains("ai")) {
                    settings.readTag(wandStack.getTag().getCompound("ai"));
                }
                minecraft.setScreen(ordersGuiFactory.build(this, settings));
            }
        };
        //x, y, width, height
        addButton(new Button(width / 2 - 50, height / 2, 100, 20, I18n.get("gui.overlord.orders"), openOrdersScreen));
        addButton(new Button(width / 2 - 50, height / 2 + 22, 100, 20, I18n.get("gui.overlord.select_squad"), (b) -> screenOpener.openSquadSelectorGUI(null)));
        addButton(new Button(width / 2 - 102, height / 2 + 44, 100, 20, I18n.get("gui.overlord.local_orders.issue_orders"), this::issueOrders));
        addButton(new Button(width / 2 + 2, height / 2 + 44, 100, 20, I18n.get("gui.done"), (b) -> this.closeScreen()));
    }

    private void closeScreen() {
        Minecraft.getInstance().setScreen(null);
    }

    private void issueOrders(Button unused) {
        packetSender.sendToServer(serverboundPackets.issueLocalOrders(), issueLocalOrdersBufferBuilder.build());
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
        String currentlyOrderingText = I18n.get("gui.overlord.local_orders.currently_ordering.any", orderDistance);

        ItemStack wandStack = OrdersWandItem.getActiveWand(this.minecraft.player);
        //noinspection ConstantConditions
        if (!wandStack.isEmpty() && wandStack.hasTag() && wandStack.getTag().contains("squad")) {
            UUID squadId = wandStack.getTag().getUUID("squad");
            Squad squad = squads.getSquad(this.minecraft.player.getUUID(), squadId);
            if (squad != null) {
                currentlyOrderingText = I18n.get("gui.overlord.local_orders.currently_ordering.squad", squad.getName(), orderDistance);
            }
        }
        this.font.draw(currentlyOrderingText, width / 2f - font.width(currentlyOrderingText) / 2f, height / 2f - 20, 0xFFFFFF);
        String matchingCountText = I18n.get("gui.overlord.local_orders.matching_count", matchingArmyMemberCount);
        this.font.draw(matchingCountText, width / 2f - font.width(matchingCountText) / 2f, height / 2f - 10, 0xFFFFFF);
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
        UUID squadId = wandStack.hasTag() && wandStack.getTag().contains("squad") ? wandStack.getTag().getUUID("squad") : null;

        Collection<ArmyEntity> nearbyArmyMembers = minecraft.player.level.getEntitiesOfClass(
            ArmyEntity.class,
            minecraft.player.getBoundingBox().inflate(orderDistance),
            entity -> minecraft.player.getUUID().equals(entity.getOwnerUUID()) && (squadId == null || entity.getSquad().equals(squadId))
        );

        matchingArmyMemberCount = nearbyArmyMembers.size();
    }
}
