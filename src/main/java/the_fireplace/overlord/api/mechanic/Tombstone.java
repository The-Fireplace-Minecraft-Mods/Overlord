package the_fireplace.overlord.api.mechanic;

import javax.annotation.Nullable;
import java.util.UUID;

public interface Tombstone {
    String getNameText();
    void setNameText(String name);
    @Nullable
    UUID getOwner();
    void setOwner(@Nullable UUID owner);
}
