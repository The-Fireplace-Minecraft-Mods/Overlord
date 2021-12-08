package dev.the_fireplace.overlord.client.util;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ClientSquad implements Squad
{
    private final UUID id;
    private final UUID owner;
    private final String capeBase;
    private final ItemStack capeItem;
    private final String name;

    public ClientSquad(UUID id, UUID owner, String capeBase, ItemStack capeItem, String name) {
        this.id = id;
        this.owner = owner;
        this.capeBase = capeBase;
        this.capeItem = capeItem;
        this.name = name;
    }

    @Override
    public UUID getSquadId() {
        return id;
    }

    @Override
    public UUID getOwner() {
        return owner;
    }

    @Override
    public String getPattern() {
        return capeBase;
    }

    @Override
    public ItemStack getItem() {
        return capeItem;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void updatePattern(String capeBase, ItemStack capeItem) {
        throw new UnsupportedOperationException("Cannot save squad data on the client!");
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Cannot save squad data on the client!");
    }
}
