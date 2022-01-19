package com.radsoltan.model.reinforcement;

import com.radsoltan.model.geometry.Drawable;

/**
 * Abstract class used to describe both slab and beam reinforcement.
 * Includes abstract method that needs to be implemented by both classes.
 */
public abstract class Reinforcement implements Drawable {

    /**
     * Gets total area of the top reinforcement.
     *
     * @return total area of the top reinforcement
     */
    public abstract double getTotalAreaOfTopReinforcement();

    /**
     * Gets centroid of the top reinforcement.
     *
     * @param nominalCoverTop nominal cover for the top face of the element in mm
     * @return centroid of the top reinforcement
     */
    public abstract double getCentroidOfTopReinforcement(int nominalCoverTop);

    /**
     * Gets total area of the bottom reinforcement.
     *
     * @return total area of the bottom reinforcement
     */
    public abstract double getTotalAreaOfBottomReinforcement();

    /**
     * Gets centroid of the bottom reinforcement.
     *
     * @param nominalCoverBottom nominal cover fore the bottom face of the element in mm
     * @return centroid of the bottom reinforcement
     */
    public abstract double getCentroidOfBottomReinforcement(int nominalCoverBottom);

    /**
     * Gets reinforcement description.
     *
     * @return reinforcement description
     */
    public abstract String getDescription();
}
