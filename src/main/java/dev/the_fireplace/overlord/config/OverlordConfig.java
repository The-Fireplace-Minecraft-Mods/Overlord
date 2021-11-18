package dev.the_fireplace.overlord.config;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.lib.api.io.interfaces.access.StorageReadBuffer;
import dev.the_fireplace.lib.api.io.interfaces.access.StorageWriteBuffer;
import dev.the_fireplace.lib.api.lazyio.injectables.ConfigStateManager;
import dev.the_fireplace.lib.api.lazyio.interfaces.Config;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.config.ConfigValues;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Implementation("dev.the_fireplace.overlord.domain.config.ConfigValues")
@Singleton
public final class OverlordConfig implements Config, ConfigValues
{
    private final ConfigValues defaultConfig;

    private int quarterMilkCount;
    private int halfMilkCount;
    private int threeQuartersMilkCount;
    private int fullyGrownMilkCount;

    @Inject
    public OverlordConfig(ConfigStateManager configStateManager, @Named("default") ConfigValues defaultConfig) {
        this.defaultConfig = defaultConfig;
        configStateManager.initialize(this);
    }

    @Override
    public String getId() {
        return Overlord.MODID;
    }

    @Override
    public void readFrom(StorageReadBuffer buffer) {
        quarterMilkCount = buffer.readInt("quarterMilkCount", defaultConfig.getQuarterGrownMilkCount());
        halfMilkCount = buffer.readInt("halfMilkCount", defaultConfig.getHalfGrownMilkCount());
        threeQuartersMilkCount = buffer.readInt("threeQuartersMilkCount", defaultConfig.getThreeQuartersGrownMilkCount());
        fullyGrownMilkCount = buffer.readInt("fullyGrownMilkCount", defaultConfig.getFullyGrownMilkCount());
    }

    @Override
    public void writeTo(StorageWriteBuffer buffer) {
        buffer.writeInt("quarterMilkCount", quarterMilkCount);
        buffer.writeInt("halfMilkCount", halfMilkCount);
        buffer.writeInt("threeQuartersMilkCount", threeQuartersMilkCount);
        buffer.writeInt("fullyGrownMilkCount", fullyGrownMilkCount);
    }

    @Override
    public int getQuarterGrownMilkCount() {
        return quarterMilkCount;
    }

    public void setQuarterMilkCount(int quarterMilkCount) {
        this.quarterMilkCount = quarterMilkCount;
    }

    @Override
    public int getHalfGrownMilkCount() {
        return halfMilkCount;
    }

    public void setHalfMilkCount(int halfMilkCount) {
        this.halfMilkCount = halfMilkCount;
    }

    @Override
    public int getThreeQuartersGrownMilkCount() {
        return threeQuartersMilkCount;
    }

    public void setThreeQuartersMilkCount(int threeQuartersMilkCount) {
        this.threeQuartersMilkCount = threeQuartersMilkCount;
    }

    @Override
    public int getFullyGrownMilkCount() {
        return fullyGrownMilkCount;
    }

    public void setFullyGrownMilkCount(int fullyGrownMilkCount) {
        this.fullyGrownMilkCount = fullyGrownMilkCount;
    }
}
