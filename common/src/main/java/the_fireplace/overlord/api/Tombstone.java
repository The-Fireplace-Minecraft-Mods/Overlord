package the_fireplace.overlord.api;

import java.util.UUID;

public interface Tombstone {
    String getNameText();
    void setNameText(String name);
    UUID getOwner();
}
