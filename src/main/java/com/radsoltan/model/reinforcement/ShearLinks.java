package com.radsoltan.model.reinforcement;

import com.radsoltan.model.Drawable;

public class ShearLinks implements Drawable {

    private int shearLinkDiameter;
    private int shearLinkSpacing;
    private int shearLinkLegs;

    public ShearLinks(int shearLinkDiameter, int shearLinkSpacing, int shearLinkLegs) {
        this.shearLinkDiameter = shearLinkDiameter;
        this.shearLinkSpacing = shearLinkSpacing;
        this.shearLinkLegs = shearLinkLegs;
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
