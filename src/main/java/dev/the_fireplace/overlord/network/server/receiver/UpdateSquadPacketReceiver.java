package dev.the_fireplace.overlord.network.server.receiver;

import com.google.common.collect.Lists;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.lib.api.chat.injectables.TextStyles;
import dev.the_fireplace.lib.api.network.interfaces.ServerPacketReceiver;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.data.SquadPatterns;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.domain.rule.SquadEligibleItems;
import dev.the_fireplace.overlord.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.network.ServerToClientPacketIDs;
import dev.the_fireplace.overlord.network.server.builder.SquadUpdateFailedBufferBuilder;
import dev.the_fireplace.overlord.network.server.builder.SquadUpdatedBufferBuilder;
import dev.the_fireplace.overlord.network.server.builder.SyncSquadsBufferBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Implementation
public final class UpdateSquadPacketReceiver implements ServerPacketReceiver
{
    private final Squads squads;
    private final SquadPatterns squadPatterns;
    private final EmptyUUID emptyUUID;
    private final TextStyles textStyles;
    private final SquadEligibleItems squadEligibleItems;

    @Inject
    public UpdateSquadPacketReceiver(
        Squads squads,
        SquadPatterns squadPatterns,
        EmptyUUID emptyUUID,
        TextStyles textStyles,
        SquadEligibleItems squadEligibleItems
    ) {
        this.squads = squads;
        this.squadPatterns = squadPatterns;
        this.emptyUUID = emptyUUID;
        this.textStyles = textStyles;
        this.squadEligibleItems = squadEligibleItems;
    }

    @Override
    public Identifier getId() {
        return ClientToServerPacketIDs.UPDATE_SQUAD;
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID squadId = buf.readUuid();
        boolean isNewSquad = emptyUUID.is(squadId);
        String squadName = buf.readString();
        String pattern = buf.readString();
        ItemStack item = squadEligibleItems.convertToSquadItem(buf.readItemStack());
        Integer skeletonId = null;
        if (buf.isReadable()) {
            skeletonId = buf.readInt();
        }
        if (squadName.isEmpty() || pattern.isEmpty() || item.isEmpty()) {
            logInvalidPacketWarning(player);
            return;
        }
        UUID owner = player.getUuid();
        Squad existingSquad = squads.getSquad(owner, squadId);
        List<Text> errors = getErrors(squadId, isNewSquad, pattern, item, player, existingSquad != null, skeletonId != null ? player.world.getEntityById(skeletonId) : null);
        if (!errors.isEmpty()) {
            responseSender.sendPacket(ServerToClientPacketIDs.SQUAD_UPDATE_FAILED, SquadUpdateFailedBufferBuilder.build(errors));
            return;
        }
        Squad updatedSquad;
        if (isNewSquad) {
            updatedSquad = squads.createNewSquad(owner, pattern, item, squadName);
        } else {
            updatedSquad = existingSquad;
            if (updatedSquad == null) {
                throw new IllegalStateException("Existing squad is missing with no error.");
            }
            updatedSquad.updatePattern(pattern, item);
            updatedSquad.setName(squadName);
        }
        responseSender.sendPacket(ServerToClientPacketIDs.SQUAD_UPDATED, SquadUpdatedBufferBuilder.build(updatedSquad));
        server.getPlayerManager().sendToAll(
            responseSender.createPacket(
                ServerToClientPacketIDs.SYNC_SQUADS,
                SyncSquadsBufferBuilder.buildForOneOwner(owner, squads.getSquadsWithOwner(owner))
            )
        );
    }

    private List<Text> getErrors(UUID squadId, boolean isNewSquad, String pattern, ItemStack item, ServerPlayerEntity owner, boolean squadExists, @Nullable Entity armyEntity) {
        List<Text> errors = new ArrayList<>();
        if (isNewSquad) {
            boolean isPatternTaken = !squadPatterns.isPatternUnused(pattern, item);
            if (isPatternTaken) {
                errors.add(getStyledError("gui.overlord.create_squad.pattern_taken"));
            }
        } else {
            if (!squadExists) {
                errors.add(getStyledError("gui.overlord.create_squad.missing_squad"));
            }
            boolean isPatternTaken = !squadPatterns.isPatternUnusedByOtherSquads(pattern, item, owner.getUuid(), squadId);
            if (isPatternTaken) {
                errors.add(getStyledError("gui.overlord.create_squad.pattern_taken"));
            }
        }
        if (!squadPatterns.canUsePattern(owner.getUuid(), pattern)) {
            errors.add(getStyledError("gui.overlord.create_squad.locked_pattern"));
        }
        boolean isEligibleToUseItem = false;
        Collection<ItemStack> eligibleItems = squadEligibleItems.getEligibleItems(
            Lists.newArrayList(squads.getSquadsWithOwner(owner.getUuid())),
            owner,
            armyEntity
        );
        for (ItemStack eligibleStack : eligibleItems) {
            if (ItemStack.areItemsEqual(eligibleStack, item) && ItemStack.areTagsEqual(eligibleStack, item)) {
                isEligibleToUseItem = true;
                break;
            }
        }
        if (!isEligibleToUseItem) {
            errors.add(getStyledError("gui.overlord.create_squad.locked_item"));
        }

        return errors;
    }

    private Text getStyledError(String translationKey) {
        return new TranslatableText(translationKey).setStyle(textStyles.red());
    }

    private void logInvalidPacketWarning(ServerPlayerEntity player) {
        Overlord.getLogger().warn("Invalid squad data received from player {}", player.getUuid());
    }
}
