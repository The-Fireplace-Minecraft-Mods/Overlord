package the_fireplace.overlord.tools;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;

import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class CustomDataSerializers {
    public static final DataSerializer<UUID> UNIQUE_ID = new DataSerializer<UUID>()
    {
        public void write(PacketBuffer buf, UUID value)
        {
            buf.writeUuid(value);
        }
        public UUID read(PacketBuffer buf)
        {
            return buf.readUuid();
        }
        public DataParameter<UUID> createKey(int id)
        {
            return new DataParameter(id, this);
        }
    };
}
