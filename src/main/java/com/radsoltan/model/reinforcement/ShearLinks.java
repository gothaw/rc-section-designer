package com.radsoltan.model.reinforcement;

import com.radsoltan.model.geometry.Drawable;

public class ShearLinks implements Drawable {

    private final int fyw;
    private final int diameter;
    private final int spacing;
    private final int legs;

    public ShearLinks(int fyw, int diameter, int spacing, int legs) {
        this.fyw = fyw;
        this.diameter = diameter;
        this.spacing = spacing;
        this.legs = legs;
    }

    public int getYieldStrength() {
        return fyw;
    }

    public int getDiameter() {
        return diameter;
    }

    public int getSpacing() {
        return spacing;
    }

    public int getLegs() {
        return legs;
    }

    public double getArea() {
        return legs * 0.25 * diameter * diameter * Math.PI * 1000 / spacing;
    }

    @Override
    public void draw() {

    }
}
