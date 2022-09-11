package dev.the_fireplace.overlord.network.server.receiver;

import com.google.common.collect.Lists;
import dev.the_fireplace.lib.api.chat.injectables.TextStyles;
import dev.the_fireplace.lib.api.network.injectables.PacketSender;
import dev.the_fireplace.lib.api.network.interfaces.ClientboundPacketSpecification;
import dev.the_fireplace.lib.api.network.interfaces.ServerboundPacketReceiver;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.data.SquadPatterns;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.domain.registry.PatternRegistry;
import dev.the_fireplace.overlord.domain.rule.SquadEligibleItems;
import dev.the_fireplace.overlord.network.ClientboundPackets;
import dev.the_fireplace.overlord.network.server.builder.SquadUpdateFailedBufferBuilder;
import dev.the_fireplace.overlord.network.server.builder.SquadUpdatedBufferBuilder;
import dev.the_fireplace.overlord.network.server.builder.SyncSquadsBufferBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Singleton
public final class UpdateSquadPacketReceiver implements ServerboundPacketReceiver
{
    private final Squads squads;
    private final SquadPatterns squadPatterns;
    private final EmptyUUID emptyUUID;
    private final TextStyles textStyles;
    private final SquadEligibleItems squadEligibleItems;
    private final PatternRegistry patternRegistry;
    private final ClientboundPackets clientboundPackets;
    private final PacketSender packetSender;
    private final SquadUpdatedBufferBuilder squadUpdatedBufferBuilder;
    private final SquadUpdateFailedBufferBuilder squadUpdateFailedBufferBuilder;
    private final SyncSquadsBufferBuilder syncSquadsBufferBuilder;

    @Inject
    public UpdateSquadPacketReceiver(
        Squads squads,
        SquadPatterns squadPatterns,
        EmptyUUID emptyUUID,
        TextStyles textStyles,
        SquadEligibleItems squadEligibleItems,
        PatternRegistry patternRegistry,
        ClientboundPackets clientboundPackets,
        PacketSender packetSender,
        SquadUpdatedBufferBuilder squadUpdatedBufferBuilder,
        SquadUpdateFailedBufferBuilder squadUpdateFailedBufferBuilder,
        SyncSquadsBufferBuilder syncSquadsBufferBuilder
    ) {
        this.squads = squads;
        this.squadPatterns = squadPatterns;
        this.emptyUUID = emptyUUID;
        this.textStyles = textStyles;
        this.squadEligibleItems = squadEligibleItems;
        this.patternRegistry = patternRegistry;
        this.clientboundPackets = clientboundPackets;
        this.packetSender = packetSender;
        this.squadUpdatedBufferBuilder = squadUpdatedBufferBuilder;
        this.squadUpdateFailedBufferBuilder = squadUpdateFailedBufferBuilder;
        this.syncSquadsBufferBuilder = syncSquadsBufferBuilder;
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf) {
        UUID squadId = buf.readUUID();
        boolean isNewSquad = emptyUUID.is(squadId);
        String squadName = buf.readUtf();
        ResourceLocation patternId = buf.readResourceLocation();
        ItemStack item = squadEligibleItems.convertToSquadItem(buf.readItem());
        Integer skeletonId = null;
        if (buf.isReadable()) {
            skeletonId = buf.readInt();
        }
        if (squadName.isBlank() || item.isEmpty()) {
            logInvalidPacketWarning(player);
            return;
        }
        UUID owner = player.getUUID();
        Squad existingSquad = squads.getSquad(owner, squadId);
        List<Component> errors = getErrors(squadId, isNewSquad, patternId, item, player, existingSquad != null, skeletonId != null ? player.level.getEntity(skeletonId) : null);
        if (!errors.isEmpty()) {
            packetSender.sendToClient(handler, clientboundPackets.squadUpdateFailed(), squadUpdateFailedBufferBuilder.build(errors));
            return;
        }
        Squad updatedSquad;
        if (isNewSquad) {
            updatedSquad = squads.createNewSquad(owner, patternId, item, squadName);
        } else {
            updatedSquad = existingSquad;
            if (updatedSquad == null) {
                throw new IllegalStateException("Existing squad is missing with no error.");
            }
            updatedSquad.updatePattern(patternId, item);
            updatedSquad.setName(squadName);
        }
        packetSender.sendToClient(handler, clientboundPackets.squadUpdated(), squadUpdatedBufferBuilder.build(updatedSquad));

        syncSquadChangeToClients(server, owner);
    }

    private void syncSquadChangeToClients(MinecraftServer server, UUID squadOwner) {
        ClientboundPacketSpecification specification = clientboundPackets.syncSquads();
        FriendlyByteBuf packetContents = syncSquadsBufferBuilder.buildForOneOwner(squadOwner, squads.getSquadsWithOwner(squadOwner));
        for (ServerPlayer onlinePlayer : server.getPlayerList().getPlayers()) {
            packetSender.sendToClient(
                onlinePlayer.connection,
                specification,
                packetContents
            );
        }
    }

    private List<Component> getErrors(UUID squadId, boolean isNewSquad, ResourceLocation patternId, ItemStack item, ServerPlayer owner, boolean squadExists, @Nullable Entity armyEntity) {
        List<Component> errors = new ArrayList<>();
        if (isNewSquad) {
            boolean isPatternTaken = !squadPatterns.isPatternUnused(patternId, item);
            if (isPatternTaken) {
                errors.add(getStyledError("gui.overlord.create_squad.pattern_taken"));
            }
        } else {
            if (!squadExists) {
                errors.add(getStyledError("gui.overlord.create_squad.missing_squad"));
            }
            boolean isPatternTaken = !squadPatterns.isPatternUnusedByOtherSquads(patternId, item, owner.getUUID(), squadId);
            if (isPatternTaken) {
                errors.add(getStyledError("gui.overlord.create_squad.pattern_taken"));
            }
        }
        if (!patternRegistry.getById(patternId).canBeUsedBy(owner)) {
            errors.add(getStyledError("gui.overlord.create_squad.locked_pattern"));
        }
        boolean isEligibleToUseItem = false;
        Collection<ItemStack> eligibleItems = squadEligibleItems.getEligibleItems(
            Lists.newArrayList(squads.getSquadsWithOwner(owner.getUUID())),
            owner,
            armyEntity
        );
        for (ItemStack eligibleStack : eligibleItems) {
            if (ItemStack.matches(eligibleStack, item)) {
                isEligibleToUseItem = true;
                break;
            }
        }
        if (!isEligibleToUseItem) {
            errors.add(getStyledError("gui.overlord.create_squad.locked_item"));
        }

        return errors;
    }

    private Component getStyledError(String translationKey) {
        return new TranslatableComponent(translationKey).setStyle(textStyles.red());
    }

    private void logInvalidPacketWarning(ServerPlayer player) {
        OverlordConstants.getLogger().warn("Invalid squad data received from player {}", player.getUUID());
    }
}
