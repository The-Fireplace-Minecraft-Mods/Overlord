package dev.the_fireplace.overlord.impl.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.lib.api.io.interfaces.access.StorageReadBuffer;
import dev.the_fireplace.lib.api.io.interfaces.access.StorageWriteBuffer;
import dev.the_fireplace.lib.api.lazyio.injectables.SaveDataStateManager;
import dev.the_fireplace.lib.api.lazyio.interfaces.SaveData;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.visitor.StringNbtWriter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Implementation
@Singleton
public final class SquadsImpl implements Squads
{
    private final SaveDataStateManager saveDataStateManager;
    private final ConcurrentMap<UUID, ConcurrentMap<UUID, Squad>> squadCache;

    @Inject
    public SquadsImpl(SaveDataStateManager saveDataStateManager) {
        this.saveDataStateManager = saveDataStateManager;
        this.squadCache = new ConcurrentHashMap<>();
    }

    @Override
    public void saveSquad(Squad squad) {

    }

    @Override
    public Squad getSquad(UUID owner, UUID squadId) {
        return null;
    }

    @Override
    public Squad removeSquad(UUID owner, UUID squadId) {
        return null;
    }

    @Override
    public boolean isCapeUnused(String capeBase, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canUseCapeBase(UUID player, String capeBase) {
        //TODO adjust when more capes are added
        return true;
    }

    private class SquadImpl implements Squad, SaveData
    {
        private final UUID id;
        private final UUID owner;
        private String capeBase;
        private ItemStack capeItem;
        private String name;

        private SquadImpl(UUID id, UUID owner) {
            this.id = id;
            this.owner = owner;
            this.capeBase = "missing_texture";
            this.capeItem = new ItemStack(Blocks.BARRIER);
            this.name = "Missingno";
            saveDataStateManager.initializeWithAutosave(this, (byte) 10);
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
        public String getCapeBase() {
            return capeBase;
        }

        @Override
        public ItemStack getCapeItem() {
            return capeItem;
        }

        @Override
        public void updateCape(String capeBase, ItemStack capeItem) {
            if (this.capeBase.equals(capeBase) && this.capeItem.equals(capeItem)) {
                return;
            }
            if (!isCapeUnused(capeBase, capeItem)) {
                return;
            }
            this.capeBase = capeBase;
            this.capeItem = capeItem;
            saveDataStateManager.markChanged(this);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
            saveDataStateManager.markChanged(this);
        }

        @Override
        public void readFrom(StorageReadBuffer buffer) {
            this.capeBase = buffer.readString("capeBase", this.capeBase);
            try {
                this.capeItem = ItemStack.fromNbt(StringNbtReader.parse(buffer.readString("capeItem", "")));
            } catch (CommandSyntaxException ignored) {
            }
            this.name = buffer.readString("name", this.name);
        }

        @Override
        public void writeTo(StorageWriteBuffer buffer) {
            buffer.writeString("capeBase", capeBase);
            buffer.writeString("capeItem", new StringNbtWriter().apply(capeItem.writeNbt(new NbtCompound())));
            buffer.writeString("name", name);
        }

        @Override
        public String getDatabase() {
            return Overlord.MODID;
        }

        @Override
        public String getTable() {
            return "squads";
        }

        @Override
        public String getId() {
            return getOwner().toString() + getSquadId().toString();
        }

        private void tearDown() {
            saveDataStateManager.tearDown(this);
        }

        private void delete() {
            tearDown();

        }
    }
}
