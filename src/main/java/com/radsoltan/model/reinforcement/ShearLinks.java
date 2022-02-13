package com.radsoltan.model.reinforcement;

import com.radsoltan.model.geometry.Drawable;

/**
 * Class used to describe shear links for a beam.
 */
public class ShearLinks implements Drawable {

    private final int fyw;
    private final int diameter;
    private final int spacing;
    private final int legs;

    /**
     * Constructor.
     *
     * @param fyw      field strength in MPa
     * @param diameter shear links diameter in mm
     * @param spacing  shear links spacing in mm
     * @param legs     number of legs
     */
    public ShearLinks(int fyw, int diameter, int spacing, int legs) {
        this.fyw = fyw;
        this.diameter = diameter;
        this.spacing = spacing;
        this.legs = legs;
    }

    /**
     * Getter for shear links yield strength.
     *
     * @return yield strength
     */
    public int getYieldStrength() {
        return fyw;
    }

    /**
     * Getter for shear links diameter.
     *
     * @return shear links diameter
     */
    public int getDiameter() {
        return diameter;
    }

    /**
     * Getter for shear links spacing.
     *
     * @return shear links spacing
     */
    public int getSpacing() {
        return spacing;
    }

    /**
     * Getter for number of shear links legs.
     *
     * @return number of shear legs
     */
    public int getLegs() {
        return legs;
    }

    /**
     * Gets area of the shear links per 1 m run of the beam.
     *
     * @return area of shear links per 1 m
     */
    public double getArea() {
        return legs * 0.25 * diameter * diameter * Math.PI * 1000 / spacing;
    }

    /**
     * Draws shear links.
     */
    @Override
    public void draw() {
        // TODO: 18/01/2022 Implement when working on beam project
    }

    /**
     * Method checks if shear links have been setup correctly and can be drawn.
     *
     * @return true if shear links can be drawn
     */
    @Override
    public boolean isSetupToBeDrawn() {
        return false;
    }
}
