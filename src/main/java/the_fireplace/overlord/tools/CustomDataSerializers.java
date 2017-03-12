package the_fireplace.overlord.tools;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public final class CustomDataSerializers {
    @ParametersAreNonnullByDefault
    public static final DataSerializer<UUID> UNIQUE_ID = new DataSerializer<UUID>()
    {
        @Override
        public void write(PacketBuffer buf, UUID value)
        {
            buf.writeUniqueId(value);
        }
        @Override
        public UUID read(PacketBuffer buf)
        {
            return buf.readUniqueId();
        }
        @Override
        @Nonnull
        @SuppressWarnings("unchecked")
        public DataParameter<UUID> createKey(int id)
        {
            return new DataParameter(id, this);
        }
    };
}
