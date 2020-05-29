package com.radsoltan.model.geometry;

public class Rectangle extends Shape {

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
    public int getWidthInCompressionZone(double UlsMoment) {
        return getWidth();
    }

    @Override
    public int getWidthInTensionZone(double UlsMoment) {
        return getWidth();
    }

    @Override
    public double calculateAreaInTensionZonePriorCracking(double UlsMoment) {
        return getWidthInTensionZone(UlsMoment) * calculateCentroid();
    }

    @Override
    public double calculateFactorForNonUniformSelfEquilibratingStresses() {
        if (depth <= 300) {
            return 1.0;
        } else if (depth < 800) {
            return (800 - depth) * 0.35 / 500 + 0.65;
        } else {
            return 0.65;
        }
    }

    @Override
    public double calculateFactorForStressDistributionPriorCracking() {
        return 0.4;
    }

    @Override
    public void draw() {
        // TODO: 18/05/2020
    }
}
