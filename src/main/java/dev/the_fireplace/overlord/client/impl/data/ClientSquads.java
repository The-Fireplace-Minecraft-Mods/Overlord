package dev.the_fireplace.overlord.client.impl.data;

import com.google.common.collect.ImmutableSet;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
@Implementation(name = "client")
@Singleton
public final class ClientSquads implements Squads
{
    private static final Function<UUID, ConcurrentMap<UUID, Squad>> NEW_CONCURRENT_MAP = (unused) -> new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, ConcurrentMap<UUID, Squad>> squadCache = new ConcurrentHashMap<>();

    @Nullable
    @Override
    public Squad getSquad(UUID owner, UUID squadId) {
        return squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP).get(squadId);
    }

    @Override
    public Squad createNewSquad(UUID owner, Identifier patternId, ItemStack stack, String name) {
        throw new UnsupportedOperationException("Do not call this from the client!");
    }

    @Override
    public boolean removeSquad(UUID owner, UUID squadId) {
        throw new UnsupportedOperationException("Do not call this from the client!");
    }

    @Override
    public Collection<? extends Squad> getSquadsWithOwner(UUID owner) {
        return ImmutableSet.copyOf(squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP).values());
    }

    @Override
    public Collection<? extends Squad> getSquads() {
        Collection<Squad> squads = new ArrayList<>();
        squadCache.values().forEach(entry -> squads.addAll(entry.values()));
        return squads;
    }

    public void setSquads(Collection<? extends Squad> squads) {
        this.squadCache.clear();
        for (Squad squad : squads) {
            this.squadCache.computeIfAbsent(squad.getOwner(), NEW_CONCURRENT_MAP).put(squad.getSquadId(), squad);
        }
    }

    public void setSquadsFromOwner(UUID owner, Collection<? extends Squad> squads) {
        this.squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP).clear();
        for (Squad squad : squads) {
            this.squadCache.computeIfAbsent(owner, NEW_CONCURRENT_MAP).put(squad.getSquadId(), squad);
        }
    }
}
