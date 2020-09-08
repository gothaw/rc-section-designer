package com.radsoltan.model;

/**
 * Interface for structural elements that are subjected to shear forces and require shear reinforcement.
 */
public interface Shear {

    /**
     * Interface method to calculate shear capacity.
     * The method implementation to calculate required shear reinforcement and assign it to a member variable.
     */
    void calculateShearCapacity();

    /**
     * Calculates maximum shear link spacing for given effective depth.
     * @param effectiveDepth double
     * @return maximum shear link spacing
     */
    default double getMaximumSpacingForShearLinks(double effectiveDepth){
        return 0.75 * effectiveDepth;
    }
}
