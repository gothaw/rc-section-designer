package com.radsoltan.model.geometry;

public class Rectangle extends Shape {

    private final int width;
    private final int depth;

    public Rectangle(int width, int depth) {
        this.width = width;
        this.depth = depth;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public double getArea() {
        return width * depth;
    }

    @Override
    public double getCentroid() {
        return 0.5 * depth;
    }

    @Override
    public double getSecondMomentOfArea() {
        return width * Math.pow(depth, 3) / 12;
    }

    @Override
    public int getWidthInCompressionZone(double UlsMoment) {
        return getWidth();
    }

    @Override
    public int getWidthInTensionZone(double UlsMoment) {
        return getWidth();
    }

    @Override
    public double getAreaInTensionZonePriorCracking(double UlsMoment) {
        return getWidthInTensionZone(UlsMoment) * getCentroid();
    }

    @Override
    public double getFactorForNonUniformSelfEquilibratingStresses(double UlsMoment) {
        if (depth <= 300) {
            return 1.0;
        } else if (depth < 800) {
            return (800 - depth) * 0.35 / 500 + 0.65;
        } else {
            return 0.65;
        }
    }

    @Override
    public double getFactorForStressDistributionPriorCracking(double UlsMoment) {
        return 0.4;
    }

    @Override
    public String getDescription() {
        return String.format("Rectangle: %d mm deep x %d mm wide.", depth, width);
    }

    @Override
    public void draw() {
        // TODO: 18/05/2020
    }
}
