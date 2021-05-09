package dev.the_fireplace.overlord.model.aiconfig.movement;

import dev.the_fireplace.overlord.model.aiconfig.SettingsComponent;
import net.minecraft.nbt.CompoundTag;

public class PositionSetting implements SettingsComponent {
    private int x;
    private int y;
    private int z;
    public PositionSetting(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.putInt("x", x);
        tag.putInt("y", y);
        tag.putInt("z", z);

        return tag;
    }

    @Override
    public void readTag(CompoundTag tag) {
        if (tag.contains("x")) {
            x = tag.getInt("x");
        }
        if (tag.contains("y")) {
            y = tag.getInt("y");
        }
        if (tag.contains("z")) {
            z = tag.getInt("z");
        }
    }
}
