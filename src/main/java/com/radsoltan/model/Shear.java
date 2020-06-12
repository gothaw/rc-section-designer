package com.radsoltan.model;

public interface Shear {
    void calculateShearCapacity();

    default double getMaximumSpacingForShearLinks(double effectiveDepth){
        return 0.75 * effectiveDepth;
    }
}
