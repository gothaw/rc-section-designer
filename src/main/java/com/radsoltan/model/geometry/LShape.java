package com.radsoltan.model.geometry;

public class LShape extends TShape{

    public LShape(int width, int depth, int flangeWidth, int flangeThickness) {
        super(width, depth, flangeWidth, flangeThickness);
    }

    @Override
    public String getDescription() {
        return String.format("L shape: %d mm downstand x %d mm web width + flange %d mm wide x %d mm thick.", getDownstandDepth(), getWebWidth(), getFlangeWidth(), getFlangeThickness());
    }

    @Override
    public void draw() {
        // TODO: 18/05/2020
    }
}
