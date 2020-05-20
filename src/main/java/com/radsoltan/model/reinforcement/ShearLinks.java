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

    @Override
    public void draw() {

    }
}
