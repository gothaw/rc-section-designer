package com.radsoltan.model.geometry;

public class Rectangle extends Shape  {

    private final int width;
    private final int depth;

    public Rectangle(int width, int depth) {
        this.width = width;
        this.depth = depth;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public double calculateArea() {
        return width * depth;
    }

    @Override
    public double calculateCentroid() {
        return 0.5 * depth;
    }

    @Override
    public double calculateSecondMomentOfArea() {
        return width * Math.pow(depth, 3) / 12;
    }

    @Override
    public int getWidthInCompressionZone(double UlsMoment, double effectiveDepth, double fcd) {
        return getWidth();
    }

    @Override
    public int getWidthInTensionZone(double UlsMoment) {
        return getWidth();
    }

    @Override
    public void draw() {
        // TODO: 18/05/2020
    }
}
