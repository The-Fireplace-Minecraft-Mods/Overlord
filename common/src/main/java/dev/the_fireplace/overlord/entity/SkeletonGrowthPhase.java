package dev.the_fireplace.overlord.entity;

public enum SkeletonGrowthPhase
{
    BABY,
    QUARTER,
    HALF,
    THREE_QUARTERS,
    ADULT;

    public boolean isAtLeast(SkeletonGrowthPhase phase) {
        return this.ordinal() >= phase.ordinal();
    }
}
