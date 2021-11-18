package dev.the_fireplace.overlord.config;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.config.ConfigValues;

@Implementation(name = "default")
public final class DefaultConfigValues implements ConfigValues
{
    @Override
    public int getQuarterGrownMilkCount() {
        return 4;
    }

    @Override
    public int getHalfGrownMilkCount() {
        return 16;
    }

    @Override
    public int getThreeQuartersGrownMilkCount() {
        return 32;
    }

    @Override
    public int getFullyGrownMilkCount() {
        return 64;
    }
}
