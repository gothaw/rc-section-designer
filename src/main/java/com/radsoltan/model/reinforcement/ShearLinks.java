package com.radsoltan.model.reinforcement;

import com.radsoltan.model.Drawable;

public class ShearLinks implements Drawable {

    private final int fyw;
    private final int shearLinkDiameter;
    private final int shearLinkSpacing;
    private final int shearLinkLegs;

    public ShearLinks(int fyw, int shearLinkDiameter, int shearLinkSpacing, int shearLinkLegs) {
        this.fyw = fyw;
        this.shearLinkDiameter = shearLinkDiameter;
        this.shearLinkSpacing = shearLinkSpacing;
        this.shearLinkLegs = shearLinkLegs;
    }

    public int getYieldStrength() {
        return fyw;
    }

    public int getShearLinkDiameter() {
        return shearLinkDiameter;
    }

    public int getShearLinkSpacing() {
        return shearLinkSpacing;
    }

    public int getShearLinkLegs() {
        return shearLinkLegs;
    }

    @Override
    public void draw() {

    }
}
